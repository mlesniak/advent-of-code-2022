using System.ComponentModel.DataAnnotations.Schema;

namespace Lesniak.AdventOfCode2022;

public static class Day20
{
    public static void Run()
    {
        List<int> source = File.ReadLines("20.txt").Select(line => Int32.Parse(line)).ToList();
        var numbers = new List<int>(source);

        foreach (var cur in source)
        {
            Console.WriteLine("cur = {0}", cur);
            // Find position of cur in numbers
            var curIndex = numbers.IndexOf(cur);
            numbers.RemoveAt(curIndex);
            var newIndex = (curIndex + cur);
            if (newIndex == 0)
            {
                newIndex = numbers.Count;
            }
            if (newIndex < 0)
            {
                newIndex += source.Count - 1;
            }
            if (newIndex > numbers.Count)
            {
                newIndex -= numbers.Count;
            }
            numbers.Insert(newIndex, cur);
            Console.WriteLine("numbers = {0}", String.Join(", ", numbers));
        }

        Console.WriteLine("Final result");
        foreach (var cur in numbers)
        {
            Console.WriteLine(cur);
        }

        var zi = numbers.IndexOf(0);
        var n1 = numbers[(zi + 1000) % numbers.Count];
        var n2 = numbers[(zi + 2000) % numbers.Count];
        var n3 = numbers[(zi + 3000) % numbers.Count];
        Console.WriteLine("n1 = {0}, n2 = {1}, n3 = {2}", n1, n2, n3);
        var res = n1 + n2 + n3;
        Console.WriteLine("res = {0}", res);
    }
}
