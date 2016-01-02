using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Fusion
{
    class Program
    {
        static void Main(string[] args)
        {
            PublicSQL publicsql = new PublicSQL("snort");
            while (true)
            {
                publicsql.fusion();
                Thread.Sleep(1000);
            }
        }
    }
}
