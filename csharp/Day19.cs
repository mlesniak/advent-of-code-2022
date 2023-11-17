namespace Lesniak.AdventOfCode2022;

record Blueprint(int[][] costs)
{
    public static Blueprint From(string line)
    {
        var parts = line
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
        costs[0] = new[] {parts[0]};
        costs[1] = new[] {parts[1]};
        costs[2] = new[] {parts[2], parts[3]};
        costs[3] = new[] {parts[4], 0, parts[5]};

        return new Blueprint(costs);
    }

    public override string ToString()
    {
        var costsContent = costs.Select(innerArray =>
            "[" + string.Join(", ", innerArray) + "]");
        return
            $"{nameof(costs)}: [{string.Join(", ", costsContent)}]";
    }
}

static class Day19
{
    public static void Run()
    {
        var blueprints = File
            .ReadLines("19.txt")
            .Select(Blueprint.From) // To Arrays.
            .ToArray();

        var sum = 0;
        for (int i = 0; i < blueprints.Length; i++)
        {
            var bp = blueprints[i];
            Console.WriteLine($"{i + 1} {blueprints[i]}");
            var maxResult = dfs(bp, new[] {1, 0, 0, 0}, new[] {0, 0, 0, 0}, 24);
            sum += (i + 1) * maxResult;
        }
        Console.WriteLine(sum);
    }

    private static int dfs(Blueprint bp, int[] robots, int[] minerals, int depth)
    {
        if (depth == 0)
        {
            return -1;
        }

        // evaluate different options.
        return dfs(bp, robots, minerals, depth - 1);
    }
}
