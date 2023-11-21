using System.Text.RegularExpressions;

using static Lesniak.AdventOfCode2022.Direction;

namespace Lesniak.AdventOfCode2022;

record Command(int? Steps, char? Direction);

enum Direction
{
    North,
    South,
    West,
    East
}

public class Day22
{
    public static void Run()
    {
        var lines = File.ReadAllLines("22.txt");
        int maxHeight = lines.Length - 2;
        int maxWidth = lines
            .TakeWhile(line => line.Length > 0)
            .MaxBy(line => line.Length)!.Length;

        char[][] grid = new char[maxHeight][];
        for (int lineIdx = 0; lineIdx < lines.Length - 2; lineIdx++)
        {
            string row = lines[lineIdx].PadRight(maxWidth, ' ');
            grid[lineIdx] = row.ToCharArray();
        }
        var commands = Regex.Split(lines[^1], @"(\d+|\D)")
            .Where(line => line.Length > 0)
            .Select(part =>
            {
                if (Int32.TryParse(part, out int steps))
                {
                    return new Command(steps, null);
                }
                return new Command(null, part[0]);
            });

        var x = 0;
        var y = 0;
        var dir = East;

        var stop = false;
        for (int gy = 0; gy < grid.Length && !stop; gy++)
        {
            for (int gx = 0; gx < grid.Length; gx++)
            {
                if (grid[gy][gx] == '.')
                {
                    x = gx;
                    y = gy;
                    stop = true;
                    break;
                }
            }
        }

        foreach (Command command in commands)
        {
            Console.WriteLine($"\ny={y + 1} / x={x + 1} dir={dir} for {command}");
            if (command.Steps.HasValue)
            {
                var steps = command.Steps.Value;
                Console.WriteLine($"Walking {steps}");
                switch (dir)
                {
                    case North:
                        var stopNorth = false;
                        while (steps-- > 0 && !stopNorth)
                        {
                            var nx = x;
                            var ny = (y - 1) % maxHeight;
                            if (ny < 0)
                            {
                                ny = maxHeight - 1;
                            }
                            if (grid[ny][nx] == '#')
                            {
                                break;
                            }
                            if (grid[ny][nx] == ' ')
                            {
                                // Find opposite end.
                                var sy = maxHeight - 1;
                                while (true)
                                {
                                    if (grid[sy][nx] == '#')
                                    {
                                        // Ok, wall ahead, let's abort.
                                        stopNorth = true;
                                        ny++;
                                        break;
                                    }
                                    if (grid[sy][nx] == '.')
                                    {
                                        ny = sy;
                                        break;
                                    }
                                    sy--;
                                }
                            }
                            x = nx;
                            y = ny;
                        }
                        break;
                    case East:
                        var stopEast = false;
                        while (steps-- > 0 && !stopEast)
                        {
                            var nx = (x + 1) % maxWidth;
                            var ny = y;
                            if (grid[ny][nx] == '#')
                            {
                                break;
                            }
                            if (grid[ny][nx] == ' ')
                            {
                                // Find opposite end.
                                var sx = 0;
                                while (true)
                                {
                                    if (grid[ny][sx] == '#')
                                    {
                                        // Ok, wall ahead, let's abort.
                                        stopEast = true;
                                        nx--;
                                        break;
                                    }
                                    if (grid[ny][sx] == '.')
                                    {
                                        nx = sx;
                                        break;
                                    }
                                    sx++;
                                }
                            }
                            x = nx;
                            y = ny;
                        }
                        break;
                    case South:
                        var stopSouth = false;
                        while (steps-- > 0 && !stopSouth)
                        {
                            var nx = x;
                            var ny = (y + 1) % maxHeight;
                            if (grid[ny][nx] == '#')
                            {
                                break;
                            }
                            if (grid[ny][nx] == ' ')
                            {
                                // Find opposite end.
                                var sy = 0;
                                while (true)
                                {
                                    if (grid[sy][nx] == '#')
                                    {
                                        // Ok, wall ahead, let's abort.
                                        stopSouth = true;
                                        ny--;
                                        break;
                                    }
                                    if (grid[sy][nx] == '.')
                                    {
                                        ny = sy;
                                        break;
                                    }
                                    sy++;
                                }
                            }
                            x = nx;
                            y = ny;
                        }
                        break;
                    case West:
                        var stopWest = false;
                        while (steps-- > 0 && !stopWest)
                        {
                            var nx = (x - 1) % maxWidth;
                            if (nx < 0)
                            {
                                nx = maxWidth - 1;
                            }
                            var ny = y;
                            if (grid[ny][nx] == '#')
                            {
                                break;
                            }
                            if (grid[ny][nx] == ' ')
                            {
                                // Find opposite end.
                                var sx = 0;
                                while (true)
                                {
                                    if (grid[ny][sx] == '#')
                                    {
                                        // Ok, wall ahead, let's abort.
                                        stopWest = true;
                                        nx++;
                                        break;
                                    }
                                    if (grid[ny][sx] == '.')
                                    {
                                        nx = sx;
                                        break;
                                    }
                                    sx--;
                                }
                            }
                            x = nx;
                            y = ny;
                        }
                        break;
                }
            }
            else
            {
                switch (dir)
                {
                    case North:
                        if (command.Direction == 'R')
                        {
                            dir = East;
                        }
                        else
                        {
                            dir = West;
                        }
                        break;
                    case East:
                        if (command.Direction == 'R')
                        {
                            dir = South;
                        }
                        else
                        {
                            dir = North;
                        }
                        break;
                    case South:
                        if (command.Direction == 'R')
                        {
                            dir = West;
                        }
                        else
                        {
                            dir = East;
                        }
                        break;
                    case West:
                        if (command.Direction == 'R')
                        {
                            dir = North;
                        }
                        else
                        {
                            dir = South;
                        }
                        break;
                }
                Console.WriteLine($"New direction {dir}");
            }
        }

        Console.WriteLine($"x={x}, y={y}");
        int dirScore = dir switch
        {
            North => 3,
            South => 1,
            West => 2,
            East => 0,
            _ => throw new ArgumentOutOfRangeException()
        };
        var result = 1000 * (y+1) + 4 * (x+1) + dirScore;
        Console.WriteLine(result);
    }
}
