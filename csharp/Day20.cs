using System.Text;

namespace Lesniak.AdventOfCode2022;

public static class Day20
{
    public static void Run()
    {
        List<int> source = File.ReadLines("20.txt").Select(line => Int32.Parse(line)).ToList();
        var numbers = CircularList.From(source);
        Console.WriteLine(numbers);
        Console.WriteLine(811589153L * -3L);
        // Console.WriteLine("");

        for (int o = 0; o < 10; o++)
        {
            Console.WriteLine($"\nRun {o}");
            foreach (Node n in numbers.Nodes)
            {
                if (n.Value == 0)
                {
                    continue;
                }
                // Console.WriteLine($"{n}");
                numbers.Shift(n, n.Value);
                // Console.WriteLine($"{n}:  {numbers}");
            }
            Console.WriteLine(numbers);
        }

        Console.WriteLine("Result");
        Console.WriteLine(numbers);

        long nth = numbers.Nth(1000);
        long i = numbers.Nth(2000);
        long l = numbers.Nth(3000);
        Console.WriteLine($"{nth}, {i}, {l}");
        var sum = nth + i + l;
        Console.WriteLine(sum);

        var r = -13 % 10;
        if (r < 0)
        {
            r += 10;
        }
        Console.WriteLine(r);
    }
}

public class CircularList
{
    Node Head { get; set; }
    private int Count { get; set; }
    public List<Node> Nodes { get; set; }

    private Node Find(int value)
    {
        var cur = Head;
        while (cur.Value != value)
        {
            cur = cur.Right;
        }
        return cur;
    }

    public long Nth(int n)
    {
        var cur = Find(0);
        while (n-- > 0)
        {
            cur = cur.Right;
        }

        return cur.Value;
    }

    public void Shift(Node k, long kn)
    {
        var m = Count - 1;

        if (kn == 0)
        {
            return;
        }

        // Console.WriteLine("Loop");
        var p = k;
        if (kn > 0)
        {
            var t = kn % m;
            var steps = 0;
            while (t-- > 0)
            {
                steps++;
                // Console.WriteLine($"current p={p.Value}");
                p = p.Right;
            }
            // Console.WriteLine($"k={k}, p={p}");
            // Console.WriteLine($"steps={steps}");
            if (k == p)
            {
                return;
            }
            k.Right.Left = k.Left;
            k.Left.Right = k.Right;
            p.Right.Left = k;
            k.Right = p.Right;
            p.Right = k;
            k.Left = p;
            // Console.WriteLine($"k.Left={k.Left}, k.Right={k.Right}");
            // Console.ReadLine();
        }
        else
        {
            var t = -kn % m;
            while (t-- > 0)
            {
                p = p.Left;
            }
            // Console.WriteLine($"k={k}, p={p}");
            // Console.ReadLine();
            if (k == p)
            {
                return;
            }
            k.Left.Right = k.Right;
            k.Right.Left = k.Left;
            p.Left.Right = k;
            k.Left = p.Left;
            p.Left = k;
            k.Right = p;
        }
    }

    public override string ToString()
    {
        var sb = new StringBuilder();


        var head = Nodes[0];
        sb.Append(head.Value);
        sb.Append(' ');

        var cur = head.Right;
        while (cur != Nodes[0])
        {
            sb.Append(cur.Value);
            sb.Append(' ');
            cur = cur.Right;
        }
        sb.Remove(sb.Length - 1, 1);
        return sb.ToString();
    }

    public static CircularList From(List<int> source)
    {
        var numbers = new CircularList();

        long key = 811589153;
        var ks = source.Select(n => new Node {Value = n * key}).ToList();
        for (int i = 1; i < ks.Count - 1; i++)
        {
            ks[i].Left = ks[i - 1];
            ks[i].Right = ks[i + 1];
        }
        ks[0].Right = ks[1];
        ks[0].Left = ks[^1];
        ks[^1].Left = ks[^2];
        ks[^1].Right = ks[0];

        numbers.Count = source.Count;
        numbers.Head = ks[0];
        numbers.Nodes = ks;
        return numbers;
    }
}

public class Node
{
    public long Value { get; init; }
    public Node Right { get; set; }
    public Node Left { get; set; }

    public override string ToString() => $"{Value}/{base.GetHashCode()}";
}
