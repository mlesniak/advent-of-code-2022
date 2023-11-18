using System.Text;

namespace Lesniak.AdventOfCode2022;

public static class Day20
{
    public static void Run()
    {
        List<int> source = File.ReadLines("20.txt").Select(line => Int32.Parse(line)).ToList();
        var numbers = CircularList.From(source);
        Console.WriteLine(numbers);

        foreach (int num in source)
        {
            // Console.WriteLine($"\nfor {num}");
            numbers.Shift(num, num);
            // Console.WriteLine(numbers);
            // Console.WriteLine(numbers.ToStringRev());
        }

        var result = numbers.Nth(1000) + numbers.Nth(2000) + numbers.Nth(3000);
        Console.WriteLine($"{result}");

    }
}

public class CircularList
{
    Node Head { get; set; }

    public Node Find(int value)
    {
        var cur = Head;
        while (cur.Value != value)
        {
            cur = cur.Next;
        }
        return cur;
    }

    public int Nth(int n)
    {
        var cur = Find(0);
        while (n-- > 0)
        {
            cur = cur.Next;
        }

        return cur.Value;
    }

    public void Shift(int value, int delta)
    {
        if (delta == 0)
        {
            return;
        }

        var cur = Find(value);
        cur.Prev.Next = cur.Next;

        Node tmp = cur;
        if (delta > 0)
        {
            while (delta > 0)
            {
                tmp = tmp.Next;
                delta--;
            }
            cur.Next.Prev = cur.Prev;
            cur.Next = tmp.Next;
            tmp.Next.Prev = cur;
            tmp.Next = cur;
            cur.Prev = tmp;
            return;
        }

        while (delta <= 0)
        {
            tmp = tmp.Prev;
            delta++;
        }
        cur.Next.Prev = cur.Prev;
        cur.Next = tmp.Next;
        tmp.Next.Prev = cur;
        tmp.Next = cur;
        cur.Prev = tmp;
    }

    public override string ToString()
    {
        var sb = new StringBuilder();

        var head = Find(1);

        sb.Append(head.Value);
        sb.Append(' ');

        var cur = head.Next;
        while (cur != Head)
        {
            sb.Append(cur.Value);
            sb.Append(' ');
            cur = cur.Next;
        }
        sb.Remove(sb.Length - 1, 1);
        return sb.ToString();
    }

    public string ToStringRev()
    {
        var sb = new StringBuilder();

        var head = Find(1);

        sb.Append(head.Value);
        sb.Append(' ');

        var cur = head.Prev;
        while (cur != Head)
        {
            sb.Append(cur.Value);
            sb.Append(' ');
            cur = cur.Prev;
        }
        sb.Remove(sb.Length - 1, 1);
        return sb.ToString();
    }

    public static CircularList From(List<int> source)
    {
        var numbers = new CircularList();
        var prev = new Node {Value = source[0]};
        numbers.Head = prev;
        for (int i = 1; i < source.Count; i++)
        {
            var cur = new Node {Value = source[i]};
            prev.Next = cur;
            cur.Prev = prev;
            prev = cur;
        }
        prev.Next = numbers.Head;
        numbers.Head.Prev = prev;
        return numbers;
    }
}

public class Node
{
    public int Value { get; set; }
    public Node Next { get; set; }
    public Node Prev { get; set; }
}
