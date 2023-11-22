using static Lesniak.AdventOfCode2022.Choice;

namespace Lesniak.AdventOfCode2022;

enum Choice
{
    North,
    South,
    West,
    East
}

record ElfPosition(int X, int Y)
{
    public override string ToString() => $"{X}/{Y}";
}

// Not beautiful, but it works...
record Elf(List<Choice> Choices)
{
    public ElfPosition? Next = null;
    public ElfPosition Position;

    public override string ToString() => $"Elve/{Position}";
}

public class Day23
{
    public static void Run()
    {
        var elves = new HashSet<Elf>();
        var lines = File.ReadAllLines("23.txt");
        for (var y = 0; y < lines.Length; y++)
        {
            // Console.WriteLine(lines[y]);
            for (var x = 0; x < lines[y].Length; x++)
            {
                if (lines[y][x] == '#')
                {
                    var choices = new List<Choice> {North, South, West, East};
                    var position = new ElfPosition(x, y);
                    var elf = new Elf(choices) {Position = position};
                    elves.Add(elf);
                }
            }
        }

        // foreach (var position in elves)
        // {
        //     Console.WriteLine(position);
        // }
        Render(elves);

        for (int round = 1; round <= 10; round++)
        {
            Console.WriteLine($"\n\n--- Round {round}");
            // First half. Compute positions.
            // Console.WriteLine("First half");
            foreach (var elf in elves)
            {
                // Console.WriteLine($"For {elf}");
                ComputeNewPosition(elves, elf);
                // Console.WriteLine($"  Computed new position {elf.Next}");
            }
            // Second half. Determine if allowed.
            // Console.WriteLine("Second half");
            foreach (var elf in elves)
            {
                // Console.WriteLine($"For {elf}");
                int movingTo = CountMovingTo(elves, elf.Next);
                if (movingTo == 1)
                {
                    elf.Position = elf.Next;
                    // Console.WriteLine($"  Moved to {elf.Position}");
                }
                else
                {
                    // Console.WriteLine($"  Stayed at {elf.Position}");
                }
                // Choices are constant moved. Actually no need
                // for a list.
                var c = elf.Choices[0];
                elf.Choices.RemoveAt(0);
                elf.Choices.Add(c);
            }
            Render(elves);
        }
        
        int minx = elves.MinBy(elf => elf.Position.X).Position.X;
        int miny = elves.MinBy(elf => elf.Position.Y).Position.Y;
        int maxx = elves.MaxBy(elf => elf.Position.X).Position.X;
        int maxy = elves.MaxBy(elf => elf.Position.Y).Position.Y;

        var squares = (maxx - minx + 1) * (maxy - miny + 1);
        var result = squares - elves.Count;
        Console.WriteLine(result);
    }

    private static void Render(HashSet<Elf> elves)
    {
        int minx = elves.MinBy(elf => elf.Position.X).Position.X;
        int miny = elves.MinBy(elf => elf.Position.Y).Position.Y;
        int maxx = elves.MaxBy(elf => elf.Position.X).Position.X;
        int maxy = elves.MaxBy(elf => elf.Position.Y).Position.Y;

        for (int row = miny; row <= maxy; row++)
        {
            for (int col = minx; col <= maxx; col++)
            {
                var p = new ElfPosition(col, row);
                var elf = elves.Any(elf => elf.Position == p);
                if (elf)
                {
                    Console.Write("#");
                }
                else
                {
                    Console.Write(".");
                }
            }
            Console.WriteLine();
        }
    }

    private static void ComputeNewPosition(HashSet<Elf> elves, Elf elf)
    {
        var x = elf.Position.X;
        var y = elf.Position.Y;

        // If there are no elves around, do nothing.
        if (IsFree(elves, x, y - 1) && IsFree(elves, x - 1, y - 1) && IsFree(elves, x + 1, y - 1) &&
            IsFree(elves, x, y + 1) && IsFree(elves, x - 1, y + 1) && IsFree(elves, x + 1, y + 1) &&
            IsFree(elves, x - 1, y - 1) && IsFree(elves, x - 1, y) && IsFree(elves, x - 1, y + 1) &&
            IsFree(elves, x + 1, y - 1) && IsFree(elves, x + 1, y) && IsFree(elves, x + 1, y + 1))
        {
            // Keep elve at his position.
            elf.Next = elf.Position;
            return;
        }

        foreach (var elveChoice in elf.Choices)
        {
            switch (elveChoice)
            {
                case North:
                    if (IsFree(elves, x, y - 1) && IsFree(elves, x - 1, y - 1) && IsFree(elves, x + 1, y - 1))
                    {
                        elf.Next = new ElfPosition(x, y - 1);
                        return;
                    }
                    break;
                case South:
                    if (IsFree(elves, x, y + 1) && IsFree(elves, x - 1, y + 1) && IsFree(elves, x + 1, y + 1))
                    {
                        elf.Next = new ElfPosition(x, y + 1);
                        return;
                    }
                    break;
                case West:
                    if (IsFree(elves, x - 1, y - 1) && IsFree(elves, x - 1, y) && IsFree(elves, x - 1, y + 1))
                    {
                        elf.Next = new ElfPosition(x - 1, y);
                        return;
                    }
                    break;
                case East:
                    if (IsFree(elves, x + 1, y - 1) && IsFree(elves, x + 1, y) && IsFree(elves, x + 1, y + 1))
                    {
                        elf.Next = new ElfPosition(x + 1, y);
                        return;
                    }
                    break;
                default:
                    throw new ArgumentOutOfRangeException($"{elveChoice}");
            }
        }

        // Keep elve at his position.
        elf.Next = elf.Position;
    }

    private static bool IsFree(HashSet<Elf> elves, int x, int y)
    {
        return !elves.Any(elve => elve.Position.X == x && elve.Position.Y == y);
    }

    private static int CountMovingTo(HashSet<Elf> elves, ElfPosition goal)
    {
        return elves.Count(elve => elve.Next!.Equals(goal));
    }
}
