using System.Text;

namespace Lesniak.AdventOfCode2022;

public static class Day20
{
    public static void Run()
    {
        List<int> source = File.ReadLines("20.txt").Select(line => Int32.Parse(line)).ToList();
        var numbers = CircularListArray.From(source);
        Console.WriteLine(numbers);

        foreach (int num in source)
        {
            Console.WriteLine($"\nfor {num}");
            numbers.Shift(num, num);
            Console.WriteLine(numbers);
        }

        Console.WriteLine("\nResult");
        Console.WriteLine(numbers);
        int n1 = numbers.Nth(1000);
        int n2 = numbers.Nth(2000);
        int n3 = numbers.Nth(3000);
        Console.WriteLine($"{n1} {n2} {n3}");
        var result = n1 + n2 + n3;
        Console.WriteLine($"{result}");
    }
}

public class CircularListArray
{
    private List<int> List;

    public static CircularListArray From(List<int> source)
    {
        return new CircularListArray {List = new List<int>(source)};
    }

    private int Find(int value)
    {
        return List.IndexOf(value);
    }

    public int Nth(int delta)
    {
        var zero = Find(0);
        return List[(zero + delta) % List.Count];
    }

    public void Shift(int value, int delta)
    {
        var cur = Find(value);
        Console.WriteLine($"cur={cur}");
        var newPos = (cur + delta) % List.Count;
        if (newPos == 0)
        {
            // newPos++;
        }
        if (cur + delta > List.Count)
        {
            // newPos = newPos % List.Count + 1;
            newPos++;
        }
        if (newPos < 0)
        {
            newPos = List.Count + newPos - 1;
        }
        if (newPos == cur)
        {
            return;
        }
        Console.WriteLine($"newPos = {newPos}");
        List.RemoveAt(cur);
        List.Insert(newPos, value);
    }

    public override string ToString() => string.Join(",", List);
}

public class CircularList
{
    Node Head { get; set; }
    private int Count { get; set; }

    private Node Find(int value)
    {
        var cur = Head;
        while (cur.Value != value)
        {
            cur = cur.Next;
        }
        return cur;
    }

    public int Distance(int n)
    {
        var cur = Find(0);
        int d = 0;
        while (cur.Value != n)
        {
            cur = cur.Next;
            d++;
        }
        return d;
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
        if (true)
        {
            return "nope";
        }

        var head = Find(1);

        sb.Append(head.Value);
        sb.Append(' ');

        var cur = head.Next;
        while (cur.Value != 1)
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
        while (cur.Value != 1)
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
        numbers.Count = source.Count;
        return numbers;
    }
}

public class Node
{
    public int Value { get; set; }
    public Node Next { get; set; }
    public Node Prev { get; set; }

    public override string ToString() => $"{Value}";
}
