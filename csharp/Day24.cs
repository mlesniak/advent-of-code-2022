using System.Text;

namespace Lesniak.AdventOfCode2022;

class Position
{
    public int Y { get; set; }
    public int X { get; set; }

    public Position(int x, int y)
    {
        X = x;
        Y = y;
    }

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj))
        {
            return false;
        }
        if (ReferenceEquals(this, obj))
        {
            return true;
        }
        if (obj.GetType() != this.GetType())
        {
            return false;
        }
        Position other = (Position)obj;
        return Y == other.Y && X == other.X;
    }

    public override int GetHashCode() => HashCode.Combine(Y, X);

    public override string ToString() => $"{X}/{Y}";
}

// All independent, we could precompute this. This was easy
// to implement, hence I've ignored this optimization for now.
class Grid
{
    private Dictionary<Position, List<char>> Blizzards = new();
    public int Width { get; set; }
    public int Height { get; set; }

    public Grid Step()
    {
        var copy = new Grid() {Width = Width, Height = Height};
        copy.Blizzards = new Dictionary<Position, List<char>>();

        // Adjust position for each blizzard independently.
        foreach (var pair in Blizzards)
        {
            foreach (var c in pair.Value)
            {
                var pos = new Position(pair.Key.X, pair.Key.Y);
                switch (c)
                {
                    case '#':
                        // We never move the wall, but having these
                        // might make the state transitions easier
                        // since we have natural walls.
                        break;
                    case '>':
                        pos.X = (pos.X + 1) % (Width - 1);
                        if (pos.X == 0)
                        {
                            pos.X = 1;
                        }
                        break;
                    case '<':
                        pos.X = pos.X - 1;
                        if (pos.X == 0)
                        {
                            pos.X = Width - 2;
                        }
                        break;
                    case '^':
                        pos.Y = pos.Y - 1;
                        if (pos.Y == 0)
                        {
                            pos.Y = Height - 2;
                        }
                        break;
                    case 'v':
                        pos.Y = (pos.Y + 1) % (Height - 1);
                        if (pos.Y == 0)
                        {
                            pos.Y = 1;
                        }
                        break;
                    default:
                        throw new InvalidProgramException($"Invalid blizzard char {pair.Value}");
                }
                var poss = copy.Blizzards.GetValueOrDefault(pos, new List<char>());
                poss.Add(c);
                copy.Blizzards[pos] = poss;
            }
        }

        return copy;
    }

    public override string ToString() => ToString(null);

    public string ToString(Position? pos)
    {
        var sb = new StringBuilder();
        for (var row = 0; row < Height; row++)
        {
            for (var col = 0; col < Width; col++)
            {
                if (Blizzards.TryGetValue(new Position(col, row), out List<char> cs))
                {
                    if (cs.Count > 1)
                    {
                        sb.Append($"{cs.Count}");
                    }
                    else
                    {
                        sb.Append(cs[0]);
                    }
                }
                else if (pos != null && pos.X == col && pos.Y == row)
                {
                    sb.Append('E');
                }
                else
                {
                    sb.Append('.');
                }
            }
            sb.Append('\n');
        }
        sb.Append('\n');

        return sb.ToString();
    }

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(null, obj))
        {
            return false;
        }
        if (ReferenceEquals(this, obj))
        {
            return true;
        }
        if (obj.GetType() != this.GetType())
        {
            return false;
        }
        return Blizzards.Equals(((Grid)obj).Blizzards);
    }

    public override int GetHashCode() => Blizzards.GetHashCode();

    public bool IsFree(int x, int y)
    {
        return !Blizzards.ContainsKey(new Position(x, y));
    }

    public static Grid Load(string filename)
    {
        var grid = new Grid();

        var lines = File.ReadAllLines(filename);
        grid.Height = lines.Length;
        grid.Width = lines[0].Length;
        for (var row = 0; row < lines.Length; row++)
        {
            for (var col = 0; col < lines[row].Length; col++)
            {
                var c = lines[row][col];
                if (c != '.')
                {
                    grid.Blizzards[new Position(col, row)] = new List<char> {c};
                }
            }
        }

        Cache[0] = grid;
        return grid;
    }

    public static Dictionary<int, Grid> Cache = new();

    public static Grid GetForMinute(int minute)
    {
        if (Cache.TryGetValue(minute, out Grid grid))
        {
            return grid;
        }

        var g = GetForMinute(minute - 1);
        var step = g.Step();
        Cache[minute] = step;
        return step;
    }
}

class BlizzardState
{
    public int Minute;

    // public Grid Grid;
    public Position Pos;

    public override string ToString()
    {
        var grid = Grid.GetForMinute(Minute);
        var sb = new StringBuilder();
        sb.Append($"Minute={Minute}\n");
        sb.Append($"Pos={Pos}\n");
        sb.Append(grid.ToString(Pos));
        return sb.ToString();
    }

    public List<BlizzardState> Nexts()
    {
        var res = new List<BlizzardState>();

        // Since we are never doing anything directly with the 
        // reference, using the same for all states should be fine.
        // (and we do not have to create separate copies).
        var nextGrid = Grid.GetForMinute(Minute + 1);

        // (0,0) is wait.
        var dirs = new List<(int, int)>
        {
            (-1, 0),
            (1, 0),
            (0, 1),
            (0, -1),
            (0, 0)
        };
        foreach (var (dx, dy) in dirs)
        {
            var nx = Pos.X + dx;
            var ny = Pos.Y + dy;
            if (nx <= 0 || nx >= nextGrid.Width || ny <= 0 || (ny >= nextGrid.Height && nx != nextGrid.Width - 1))
            {
                continue;
            }
            if (nextGrid.IsFree(nx, ny))
            {
                res.Add(new BlizzardState() {Minute = Minute + 1, Pos = new Position(nx, ny)});
            }
        }

        return res;
    }
}

public class Day24
{
    public static void Run()
    {
        var grid = Grid.Load("24.txt");

        // var seen = new HashSet<BlizzardState>();
        var queue = new Queue<BlizzardState>();
        var root = new BlizzardState {Minute = 0, Pos = new Position(1, 0)};
        queue.Enqueue(root);

        // Console.WriteLine(grid.Width);
        // Console.WriteLine(grid.Height);
        var goal = new Position(grid.Width - 2, grid.Height - 1);
        var max = 0;
        while (queue.Any())
        {
            var cur = queue.Dequeue();
            if (cur.Minute > max)
            {
                Console.WriteLine(cur.Minute);
                max = cur.Minute;
            }
            // seen.Add(cur);
            // Console.WriteLine($"\nLooking at\n{cur}");

            foreach (var nextState in cur.Nexts())
            {
                if (nextState.Pos.Equals(goal))
                {
                    // Console.WriteLine("Found");
                    Console.WriteLine(nextState.Minute);
                    queue.Clear();
                    break;
                }
                // Console.WriteLine($"  Adding\n{nextState}");
                // Console.WriteLine(goal);
                // if (!seen.Contains(nextState))
                // {
                queue.Enqueue(nextState);
                // }
            }
            // Console.WriteLine("Press any key");
            // Console.ReadKey();
        }

        // var minute = 0;
        // while (true)
        // {
        //     Console.WriteLine($"--- Minute {minute}");
        //     Console.WriteLine(grid);
        //     Console.WriteLine("Press any key");
        //     Console.ReadKey();
        //     minute++;
        //     grid = grid.Step();
        // }
    }
}
