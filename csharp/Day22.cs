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
                nx = state.X;
                ny = (state.Y - 1) % maxHeight;
                dir = state.Dir;
                if (ny >= 0 && grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (ny < 0 || grid[ny][nx] == ' ')
                {
                    var (dx, dy, sx, sy, sdir) = Cube(state, maxWidth, maxHeight);
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
            case South:
                nx = state.X;
                ny = state.Y + 1;
                dir = state.Dir;
                if (ny < maxHeight && grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (ny == maxHeight || grid[ny][nx] == ' ')
                {
                    var (dx, dy, sx, sy, sdir) = Cube(state, maxWidth, maxHeight);
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
                nx = (state.X - 1) % maxWidth;
                ny = state.Y;
                dir = state.Dir;
                if (nx >= 0 && grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (nx < 0 || grid[ny][nx] == ' ')
                {
                    var (dx, dy, sx, sy, sdir) = Cube(state, maxWidth, maxHeight);
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
            case East:
                nx = state.X + 1;
                ny = state.Y;
                dir = state.Dir;
                if (nx < maxWidth && grid[ny][nx] == '#')
                {
                    return (state, null);
                }

                if (nx >= maxWidth || grid[ny][nx] == ' ')
                {
                    var (dx, dy, sx, sy, sdir) = Cube(state, maxWidth, maxHeight);
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
    }

    // Manually determined.
    private static (int dx, int dy, int sx, int sy, Direction sdir) Cube(MapState state, int maxWidth, int maxHeight)
    {
        var x = state.X;
        var y = state.Y;
        var dir = state.Dir;

        // 1
        if (x >= 50 && x <= 99 && y >= 0 && y <= 49)
        {
            switch (dir)
            {
                case West:
                    return (1, 0, 0, 149 - y, East);
                case North: // 6
                    return (1, 0, 0, x + 100, East);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        // 2
        if (x >= 100 && x <= 149 && y >= 0 && y <= 49)
        {
            switch (dir)
            {
                case East: // 5
                    return (-1, 0, 149, 149 - y, West);
                case South: // 3
                    return (-1, 0, 99, x - 50, West);
                case North: // 6
                    return (0, 1, x - 100, 199, North);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        // 3
        if (x >= 50 && x <= 99 && y >= 50 && y <= 99)
        {
            switch (dir)
            {
                case West: // 4
                    return (0, 1, y - 50, 100, South);
                case East:
                    return (0, -1, y + 50, 50, North);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        // 4
        if (x >= 0 && x <= 49 && y >= 100 && y <= 149)
        {
            switch (dir)
            {
                case West:
                    return (1, 0, 50, 149 - y, East);
                case North:
                    return (1, 0, 50, 50 + x, East);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        // 5
        if (x >= 50 && x <= 99 && y >= 100 && y <= 149)
        {
            switch (dir)
            {
                case East:
                    return (-1, 0, 149, 149 - y, West);
                case South: // 6
                    return (-1, 0, 49, x + 100, West);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        // 6
        if (x >= 0 && x <= 49 && y >= 150 && y <= 199)
        {
            switch (dir)
            {
                case South:
                    return (0, 1, x + 100, 0, South);
                case West: // 1
                    return (0, 1, y - 100, 0, South);
                case East: // 5
                    return (0, -1, y - 100, 149, North);
                default:
                    throw new InvalidProgramException($"{state}");
            }
        }

        throw new InvalidProgramException($"{state}");
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
