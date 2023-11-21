namespace Lesniak.AdventOfCode2022;

public static class Day21
{
    public static void Run()
    {
        var lines = File.ReadLines("21.txt");
        var formulas = new Dictionary<string, MonkeyNode>();
        foreach (string line in lines)
        {
            var parts = line.Split(": ");
            var rest = parts[1].Split(" ");
            if (rest.Length == 1)
            {
                formulas[parts[0]] = MonkeyNode.FromNumber(parts[1]);
            }
            else
            {
                formulas[parts[0]] = MonkeyNode.FromExpression(rest[0], rest[1], rest[2]);
            }
        }

        foreach (var form in formulas)
        {
            Console.WriteLine(form);
        }

        var result = Compute(formulas, "root");
        Console.WriteLine(result);

    }

    private static long Compute(Dictionary<string,MonkeyNode> formulas, string name)
    {
        var node = formulas[name];
        long? value = node.Value;
        if (value.HasValue)
        {
            return value.Value;
        }

        var left = Compute(formulas, node.a);
        var right = Compute(formulas, node.b);
        switch (node.op)
        {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                return left / right;
            default:
                throw new InvalidProgramException();
        }
    }
}

class MonkeyNode
{
    public long? Value = null;
    public String a;
    public String b;
    public char op;

    public static MonkeyNode FromNumber(string num)
    {
        var node = new MonkeyNode();
        node.Value = Int64.Parse(num);
        return node;
    }

    public override string ToString()
    {
        if (Value != null)
        {
            return $"{Value}";
        }

        return $"{a} {op} {b}";
    }

    public static MonkeyNode FromExpression(string a, string op, string b)
    {
        var node = new MonkeyNode();
        node.a = a;
        node.op = op[0];
        node.b = b;
        return node;
    }
}
