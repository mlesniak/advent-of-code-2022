using System.Text.RegularExpressions;

using static Lesniak.AdventOfCode2022.Direction;

namespace Lesniak.AdventOfCode2022;

record Command(int? Steps, char? Direction)
{
    public override string ToString()
    {
        if (Steps == null)
        {
            return Direction!.ToString();
        }

        return $"{Steps}";
    }
}

record MapState(int X, int Y, Direction Dir)
{
    public int Score()
    {
        int dirScore = Dir switch
        {
            North => 3,
            South => 1,
            West => 2,
            East => 0,
            _ => throw new ArgumentOutOfRangeException()
        };
        return 1000 * (Y + 1) + 4 * (X + 1) + dirScore;
    }

    public override string ToString() => $"Y={Y + 1}/X={X + 1} D={Dir}";
}

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
        var state = new MapState(0, 0, East);
        Load(out var grid, out var maxWidth, out var maxHeight, out var commands);
        state = ComputeStartingPosition(grid, state);

        foreach (Command command in commands)
        {
            Console.WriteLine($"\nExecuting {command} on {state}");
            var c = command;

            (state, c) = Compute(state, c, grid, maxWidth, maxHeight);
            while (c != null)
            {
                Console.WriteLine($"  Executing {c} on {state}");
                (state, c) = Compute(state, c, grid, maxWidth, maxHeight);
            }
        }

        Console.WriteLine(state.Score());

        // foreach (Command command in commands)
        // {
        //     Console.WriteLine($"\ny={y + 1} / x={x + 1} dir={dir} for {command}");
        //     if (command.Steps.HasValue)
        //     {
        //         var steps = command.Steps.Value;
        //         Console.WriteLine($"Walking {steps}");
        //         switch (dir)
        //         {
        //             case North:
        //                 var stopNorth = false;
        //                 while (steps-- > 0 && !stopNorth)
        //                 {
        //                     var nx = x;
        //                     var ny = (y - 1) % maxHeight;
        //                     if (ny >= 0 && grid[ny][nx] == '#')
        //                     {
        //                         break;
        //                     }
        //                     if (ny < 0 || grid[ny][nx] == ' ')
        //                     {
        //                         // Find opposite end.
        //                         var sy = maxHeight - 1;
        //                         var prev = ny;
        //                         while (true)
        //                         {
        //                             if (grid[sy][nx] == '#')
        //                             {
        //                                 // Ok, wall ahead, let's abort.
        //                                 stopNorth = true;
        //                                 ny = prev + 1;
        //                                 break;
        //                             }
        //                             if (grid[sy][nx] == '.')
        //                             {
        //                                 ny = sy;
        //                                 break;
        //                             }
        //                             sy--;
        //                         }
        //                     }
        //                     x = nx;
        //                     y = ny;
        //                 }
        //                 break;
        //             case East:
        //                 var stopEast = false;
        //                 while (steps-- > 0 && !stopEast)
        //                 {
        //                     var nx = (x + 1) % maxWidth;
        //                     var ny = y;
        //                     if (grid[ny][nx] == '#')
        //                     {
        //                         break;
        //                     }
        //                     if (grid[ny][nx] == ' ')
        //                     {
        //                         // Find opposite end.
        //                         var sx = 0;
        //                         var prev = nx;
        //                         while (true)
        //                         {
        //                             if (grid[ny][sx] == '#')
        //                             {
        //                                 // Ok, wall ahead, let's abort.
        //                                 stopEast = true;
        //                                 nx = prev - 1;
        //                                 break;
        //                             }
        //                             if (grid[ny][sx] == '.')
        //                             {
        //                                 nx = sx;
        //                                 break;
        //                             }
        //                             sx++;
        //                         }
        //                     }
        //                     x = nx;
        //                     y = ny;
        //                 }
        //                 break;
        //             case South:
        //                 var stopSouth = false;
        //                 while (steps-- > 0 && !stopSouth)
        //                 {
        //                     var nx = x;
        //                     var ny = (y + 1) % maxHeight;
        //                     if (grid[ny][nx] == '#')
        //                     {
        //                         break;
        //                     }
        //                     if (grid[ny][nx] == ' ')
        //                     {
        //                         // Find opposite end.
        //                         var sy = 0;
        //                         var prev = ny;
        //                         while (true)
        //                         {
        //                             if (grid[sy][nx] == '#')
        //                             {
        //                                 // Ok, wall ahead, let's abort.
        //                                 stopSouth = true;
        //                                 ny = prev - 1;
        //                                 break;
        //                             }
        //                             if (grid[sy][nx] == '.')
        //                             {
        //                                 ny = sy;
        //                                 break;
        //                             }
        //                             sy++;
        //                         }
        //                     }
        //                     x = nx;
        //                     y = ny;
        //                 }
        //                 break;
        //             case West:
        //                 var stopWest = false;
        //                 while (steps-- > 0 && !stopWest)
        //                 {
        //                     var nx = (x - 1) % maxWidth;
        //                     var ny = y;
        //                     if (nx >=0 && grid[ny][nx] == '#')
        //                     {
        //                         break;
        //                     }
        //                     if (nx < 0 || grid[ny][nx] == ' ')
        //                     {
        //                         // Find opposite end.
        //                         var sx = maxWidth - 1;
        //                         var prev = nx + 1;
        //                         while (true)
        //                         {
        //                             if (grid[ny][sx] == '#')
        //                             {
        //                                 // Ok, wall ahead, let's abort.
        //                                 stopWest = true;
        //                                 nx = prev;
        //                                 break;
        //                             }
        //                             if (grid[ny][sx] == '.')
        //                             {
        //                                 nx = sx;
        //                                 break;
        //                             }
        //                             sx--;
        //                         }
        //                     }
        //                     x = nx;
        //                     y = ny;
        //                 }
        //                 break;
        //         }
        //     }
    }

    private static (MapState, Command?) Compute(MapState state, Command command, char[][] grid, int maxWidth,
        int maxHeight)
    {
        // Consider direction.
        if (command.Direction.HasValue)
        {
            var cdir = command.Direction.Value;
            switch (state.Dir)
            {
                case North:
                    if (cdir == 'R')
                    {
                        return (state with {Dir = East}, null);
                    }
                    return (state with {Dir = West}, null);
                case East:
                    if (cdir == 'R')
                    {
                        return (state with {Dir = South}, null);
                    }
                    return (state with {Dir = North}, null);
                case South:
                    if (cdir == 'R')
                    {
                        return (state with {Dir = West}, null);
                    }
                    return (state with {Dir = East}, null);
                case West:
                    if (cdir == 'R')
                    {
                        return (state with {Dir = North}, null);
                    }
                    return (state with {Dir = South}, null);
            }
        }

        if (command.Steps == 0)
        {
            return (state, null);
        }

        int nx;
        int ny;
        Direction dir;
        switch (state.Dir)
        {
            case North:
                break;
            case South:
                nx = state.X;
                ny = (state.Y + 1) % maxHeight;
                dir = state.Dir;
                if (grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (grid[ny][nx] == ' ')
                {
                    // Find next element on the opposite side if possible.
                    // Can also be a block -> null.
                    var dx = 0;
                    var dy = 1;
                    var sx = state.X;
                    var sy = 0;
                    var sdir = dir;

                    while (true)
                    {
                        if (grid[sy][sx] == '#')
                        {
                            return (state, null);
                        }
                        if (grid[sy][sx] == ' ')
                        {
                            sx += dx;
                            sy += dy;
                        }
                        if (grid[sy][sx] == '.')
                        {
                            return (new MapState(X: sx, Y: sy, Dir: sdir), command with {Steps = command.Steps - 1});
                        }
                    }
                }
                return (new MapState(X: nx, Y: ny, Dir: dir), command with {Steps = command.Steps - 1});
            case West:
                break;
            case East:
                nx = (state.X + 1) % maxWidth;
                ny = state.Y;
                dir = state.Dir;
                if (grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (grid[ny][nx] == ' ')
                {
                    // Find next element on the opposite side if possible.
                    // Can also be a block -> null.
                    // For part 2, this will be dynamic.
                    var dx = 1;
                    var dy = 0;
                    var sx = 0;
                    var sy = state.Y;
                    var sdir = dir;

                    while (true)
                    {
                        if (grid[sy][sx] == '#')
                        {
                            return (state, null);
                        }
                        if (grid[sy][sx] == ' ')
                        {
                            sx += dx;
                            sy += dy;
                        }
                        if (grid[sy][sx] == '.')
                        {
                            return (new MapState(X: sx, Y: sy, Dir: sdir), command with {Steps = command.Steps - 1});
                        }
                    }
                }
                return (new MapState(X: nx, Y: ny, Dir: dir), command with {Steps = command.Steps - 1});
            default:
                throw new ArgumentOutOfRangeException();
        }

        return (state, null);
    }

    private static MapState ComputeStartingPosition(char[][] grid, MapState state)
    {
        var stop = false;
        for (int gy = 0; gy < grid.Length && !stop; gy++)
        {
            for (int gx = 0; gx < grid.Length; gx++)
            {
                if (grid[gy][gx] == '.')
                {
                    state = state with {X = gx, Y = gy};
                    stop = true;
                    break;
                }
            }
        }
        return state;
    }

    private static void Load(out char[][] grid, out int maxWidth, out int maxHeight, out IEnumerable<Command> commands)
    {
        var lines = File.ReadAllLines("22.txt");
        maxHeight = lines.Length - 2;
        maxWidth = lines
            .TakeWhile(line => line.Length > 0)
            .MaxBy(line => line.Length)!.Length;

        grid = new char[maxHeight][];
        for (int lineIdx = 0; lineIdx < lines.Length - 2; lineIdx++)
        {
            string row = lines[lineIdx].PadRight(maxWidth, ' ');
            grid[lineIdx] = row.ToCharArray();
        }
        commands = Regex.Split(lines[^1], @"(\d+|\D)")
            .Where(line => line.Length > 0)
            .Select(part =>
            {
                if (Int32.TryParse(part, out int steps))
                {
                    return new Command(steps, null);
                }
                return new Command(null, part[0]);
            });
    }
}
