using System.Text;

namespace Lesniak.AdventOfCode2022;

public class Day25
{
    public static void Run()
    {
        string[] lines = File.ReadAllLines("25.txt");
        long sum = 0;
        for (var i = 0; i < lines.Length; i++)
        {
            sum += From(lines[i]);
        }
        Console.WriteLine(sum);
        Console.WriteLine(To(sum));
    }

    public static string To(long n)
    {
        var sb = new StringBuilder();

        var q = n / 5;
        var m = n % 5;
        while (q > 0 || (q == 0 && m > 0))
        {
            char c = m switch
            {
                0 => '0',
                1 => '1',
                2 => '2',
                3 => '=',
                4 => '-',
                _ => throw new InvalidProgramException() 
            };
            sb.Insert(0, c);
            if (m >= 3)
            {
                q++;
            }
            m = q % 5;
            q = q / 5;
        }

        return sb.ToString();
    }

    public static long From(string snafu)
    {
        long factor = 1;
        long sum = 0;

        for (int i = snafu.Length - 1; i >= 0; i--)
        {
            char c = snafu[i];
            switch (c)
            {
                case '2':
                    sum += (c - '0') * factor;
                    break;
                case '1':
                    sum += (c - '0') * factor;
                    break;
                case '0':
                    break;
                case '-':
                    sum += -1 * factor;
                    break;
                case '=':
                    sum += -2 * factor;
                    break;
            }
            factor *= 5;
        }

        return sum;
    }
}
