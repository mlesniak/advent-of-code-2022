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
    private int Width { get; set; }
    private int Height { get; set; }

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

    public override string ToString()
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

        return grid;
    }
}

class BlizzardState
{
    public int Minute;
    public Grid Grid;
    public Position Pos;

    public override string ToString() => $"{Minute} {Pos}";

    public List<BlizzardState> Nexts()
    {
        // Move Blizzards.
        // Check fields around.
        throw new NotImplementedException();
    }
}

public class Day24
{
    public static void Run()
    {
        var grid = Grid.Load("24.txt");

        var minute = 0;
        while (true)
        {
            Console.WriteLine($"--- Minute {minute}");
            Console.WriteLine(grid);
            Console.WriteLine("Press any key");
            Console.ReadKey();
            minute++;
            grid = grid.Step();
        }
    }
}
