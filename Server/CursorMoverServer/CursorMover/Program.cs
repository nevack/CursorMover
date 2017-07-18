using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace CursorMover
{
    class Program
    {
        static void Main(string[] args)
        {
            TcpListener listener = new TcpListener(IPAddress.Any, 7000);
            listener.Start();

            TcpClient client = listener.AcceptTcpClient();

            NetworkStream stream = client.GetStream();
            var w = new BinaryWriter(stream);
            var r = new BinaryReader(stream);

            var q = false;
            while (!q)
            {
                var c = r.ReadByte();
                char[] array = new char[c];
                for (var i = 0; i < c; i++)
                {
                    array[i] = r.ReadChar();
                }
                var s = new string(array);
                Console.WriteLine(s);

                var x = int.Parse(s.Split(' ')[0]);
                var y = int.Parse(s.Split(' ')[1]);

                Win32.SetCursorPos(x, y);
                if (s.Equals(".")) q = true;
            }

            Console.WriteLine("Session is over!");
            Console.ReadKey(true);

            //Win32.POINT p = new Win32.POINT();

            //for (var i = 0; i < 200; i++)
            //{
            //    Win32.GetCursorPos(out p);
            //    System.Threading.Thread.Sleep(1000 / 24);
            //    Win32.SetCursorPos(p.x + 1, p.y + 1);
            //}
        }
    }
}
