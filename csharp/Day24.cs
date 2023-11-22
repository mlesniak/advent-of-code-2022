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
                    case '>':
                        pos.X = (pos.X + 1) % Width;
                        break;
                    case '<':
                        pos.X = pos.X - 1;
                        if (pos.X < 0)
                        {
                            pos.X = Width - 1;
                        }
                        break;
                    case '^':
                        pos.Y = pos.Y - 1;
                        if (pos.Y < 0)
                        {
                            pos.Y = Height - 1;
                        }
                        break;
                    case 'v':
                        pos.Y = (pos.Y + 1) % Height;
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
        grid.Height = lines.Length - 2;
        grid.Width = lines[0].Length - 2;
        for (var row = 1; row < lines.Length - 1; row++)
        {
            for (var col = 1; col < lines[row].Length - 1; col++)
            {
                var c = lines[row][col];
                if (c != '.' && c != '#')
                {
                    grid.Blizzards[new Position(col - 1, row - 1)] = new List<char> {c};
                }
            }
        }

        return grid;
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
