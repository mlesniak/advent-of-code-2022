using System.Diagnostics;

namespace Lesniak.AdventOfCode2022;

record Blueprint(int Number, int[][] Costs, int[] MaxCost)
{
    public static Blueprint From(string line)
    {
        var parts = line
            .Replace(":", "")
            .Split(" ")
            .Select(elem =>
            {
                if (Int32.TryParse(elem, out int result))
                {
                    return result;
                }
                return -1;
            })
            .Where(i => i != -1)
            .ToArray();

        int[][] costs = new int [4][];
        // ore      0
        // clay     1
        // obsidian 2
        // geode    3
        costs[0] = new[] {parts[1]};
        costs[1] = new[] {parts[2]};
        costs[2] = new[] {parts[3], parts[4]};
        costs[3] = new[] {parts[5], 0, parts[6]};

        int[] maxCost = {0, 0, 0, 0};
        maxCost[0] = new List<int> {parts[1], parts[2], parts[3], parts[5]}.Max();
        maxCost[1] = parts[4];
        maxCost[2] = parts[6];

        return new Blueprint(parts[0], costs, maxCost);
    }

    public override string ToString()
    {
        var costsContent = Costs.Select(innerArray =>
            "[" + string.Join(", ", innerArray) + "]");
        var maxCosts = string.Join(", ", MaxCost);
        return $"costs: [{string.Join(", ", costsContent)}], maxCosts: [{maxCosts}]";
    }
}

static class Day19
{
    private static int Depth = 32;

    public static void Run()
    {
        var blueprints = File
            .ReadLines("19.txt")
            .Select(Blueprint.From) // To Arrays.
            .ToArray();

        var sum = 1;
        for (int i = 0; i < blueprints.Length; i++)
        {
            maxRes = 0;
            cache.Clear();
            var stopwatch = new Stopwatch();
            stopwatch.Start();
            var bp = blueprints[i];
            Console.WriteLine($"{bp.Number} {blueprints[i]}");
            var maxResult = dfs(bp, new[] {1, 0, 0, 0}, new[] {0, 0, 0, 0}, Depth, "");
            Console.WriteLine($"{maxResult.Item1}");
            foreach (string s in maxResult.Item2.Split(";"))
            {
                Console.WriteLine(s);
            }
            stopwatch.Stop();
            Console.WriteLine($"Duration {stopwatch.Elapsed}");

            // sum += (bp.Number) * maxResult.Item1;
            sum *= maxResult.Item1;
        }
        Console.WriteLine(sum);
    }

    private static int maxRes = 0;
    private static Dictionary<string, int> cache = new();

    private static (int, string) dfs(Blueprint bp, int[] robots, int[] minerals, int depth, string steps)
    {
        string key = depth + "-" + string.Join(",", robots) + "-" + string.Join(",", minerals);
        if (cache.TryGetValue(key, out int res))
        {
            return (res, "");
        }

        // Assuming we are creating only geode robots from now on, ignoring
        // minerals, check if we would be able to bypass the current maximum.
        // If not, we can abort anyway.
        var timeLeft = depth;
        var currentGeodeProducers = robots[3] * timeLeft;
        var restProducing = (timeLeft * timeLeft) / 2;
        if (minerals[3] + currentGeodeProducers + restProducing < maxRes)
        {
            return (-1, "");
        }

        if (depth == 0)
        {
            if (minerals[3] > maxRes)
            {
                Console.WriteLine($"New maximal value {minerals[3]}");
                maxRes = minerals[3];
            }
            cache[key] = minerals[3];
            return (minerals[3], steps);
        }

        // ore      0
        // clay     1
        // obsidian 2
        // geode    3
        // options:

        var results = new List<(int, string)>();

        // Build a geode robot if possible (as many as possible).
        if (minerals[0] >= bp.Costs[3][0] && minerals[2] >= bp.Costs[3][2])
        {
            var rbs = (int[])robots.Clone();
            rbs[3]++;
            var mrs = (int[])minerals.Clone();
            mrs[0] -= bp.Costs[3][0];
            mrs[2] -= bp.Costs[3][2];
            // Collect minerals from existing robots.
            mrs[0] += robots[0];
            mrs[1] += robots[1];
            mrs[2] += robots[2];
            mrs[3] += robots[3];

            var s = Steps(depth, steps, rbs, mrs, "geode");
            results.Add(dfs(bp, rbs, mrs, depth - 1, s));
        }

        // Build a obsidian robot if possible and makes sense.
        if (minerals[0] >= bp.Costs[2][0] && minerals[1] >= bp.Costs[2][1] && robots[2] < bp.MaxCost[2])
        // if (minerals[0] >= bp.Costs[2][0] && minerals[1] >= bp.Costs[2][1])
        {
            var rbs = (int[])robots.Clone();
            rbs[2]++;
            var mrs = (int[])minerals.Clone();
            mrs[0] -= bp.Costs[2][0];
            mrs[1] -= bp.Costs[2][1];
            // Collect minerals from existing robots.
            mrs[0] += robots[0];
            mrs[1] += robots[1];
            mrs[2] += robots[2];
            mrs[3] += robots[3];

            var s = Steps(depth, steps, rbs, mrs, "obsidian");
            results.Add(dfs(bp, rbs, mrs, depth - 1, s));
        }

        // Build a clay robot if possible and makes sense.
        if (minerals[0] >= bp.Costs[1][0] && robots[1] < bp.MaxCost[1])
        // if (minerals[0] >= bp.Costs[1][0])
        {
            var rbs = (int[])robots.Clone();
            rbs[1]++;

            var mrs = (int[])minerals.Clone();
            mrs[0] -= bp.Costs[1][0];

            // Collect minerals from existing robots.
            mrs[0] += robots[0];
            mrs[1] += robots[1];
            mrs[2] += robots[2];
            mrs[3] += robots[3];

            var s = Steps(depth, steps, rbs, mrs, "clay");
            results.Add(dfs(bp, rbs, mrs, depth - 1, s));
        }

        // Build an ore robot if possible and makes sense.
        if (minerals[0] >= bp.Costs[0][0] && robots[0] < bp.MaxCost[0])
        // if (minerals[0] >= bp.Costs[0][0])
        {
            var rbs = (int[])robots.Clone();
            rbs[0]++;
            var mrs = (int[])minerals.Clone();
            mrs[0] -= bp.Costs[0][0];
            // Collect minerals from existing robots.
            mrs[0] += robots[0];
            mrs[1] += robots[1];
            mrs[2] += robots[2];
            mrs[3] += robots[3];
            var s = Steps(depth, steps, rbs, mrs, "ore");
            results.Add(dfs(bp, rbs, mrs, depth - 1, s));
        }

        var colmrs = (int[])minerals.Clone();
        colmrs[0] += robots[0];
        colmrs[1] += robots[1];
        colmrs[2] += robots[2];
        colmrs[3] += robots[3];
        var ss = Steps(depth, steps, robots, colmrs, "collect");
        results.Add(dfs(bp, robots, colmrs, depth - 1, ss));

        // Evaluate different options.
        // Console.WriteLine($"{depth}: {string.Join(", ", results.Select(i => i.Item1))}");
        var valueTuple = results.MaxBy(t => t.Item1);
        cache[key] = valueTuple.Item1;
        return valueTuple;
    }

    private static string Steps(int depth, string steps, int[] robots, int[] minerals, string action)
    {
        // var rs = string.Join(",", robots);
        // var ms = string.Join(",", minerals);
        // var ac = action.PadRight(10);
        // var s = $"{Depth - depth + 1}".PadRight(2);
        // return $"{steps};  [{s}] {ac} robots: {rs}\tminerals:{ms}";
        return "";
    }
}
