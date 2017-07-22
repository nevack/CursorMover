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
            var port = 7000;
            //StartTcpServer(port);
            StartUdpServer(port);
            

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


        public static void StartTcpServer(int port)
        {
            TcpListener listener = new TcpListener(IPAddress.Any, port);
            listener.Start();

            TcpClient client = listener.AcceptTcpClient();

            NetworkStream stream = client.GetStream();
            var w = new BinaryWriter(stream);
            var r = new BinaryReader(stream);

            var watch = System.Diagnostics.Stopwatch.StartNew();
            var q = false;
            var count = 0;
            while (!q)
            {
                count++;
                var c = r.ReadByte();
                char[] array = new char[c];
                for (var i = 0; i < c; i++)
                {
                    array[i] = r.ReadChar();
                }
                var s = new string(array);

                var x = int.Parse(s.Split(' ')[0]);
                var y = int.Parse(s.Split(' ')[1]);

                Win32.SetCursorPos(x, y);
                if (s.Equals(".")) q = true;

                var elapsedMs = watch.ElapsedMilliseconds;
                var fps = 5; //1000 / elapsedMs;
                Console.WriteLine($"X:{x} Y:{y} Count:{count} Time taken: {elapsedMs}ms FPS:{fps}");
                watch.Restart();
            }
            watch.Stop();
        }

        public static void StartUdpServer(int port)
        {
            UdpClient reciever = new UdpClient(port);
            IPEndPoint RemoteIpEndPoint = new IPEndPoint(IPAddress.Any, port);

            var watch = System.Diagnostics.Stopwatch.StartNew();
            var q = false;
            var count = 0;
            while (!q)
            {
                count++;
                var receiveBytes = reciever.Receive(ref RemoteIpEndPoint);
                var s = Encoding.UTF8.GetString(receiveBytes);

                var x = int.Parse(s.Split(' ')[0]);
                var y = int.Parse(s.Split(' ')[1]);

                Win32.SetCursorPos(x, y);
                if (s.Equals(".")) q = true;

                var elapsedMs = watch.ElapsedMilliseconds;
                var fps = 5; //1000 / elapsedMs;
                Console.WriteLine($"X:{x} Y:{y} Count:{count} Time taken: {elapsedMs}ms FPS:{fps}");
                watch.Restart();
            }
            watch.Stop();
        }

        public static void SetCursorPosition(int x, int y)
        {
            Win32.SetCursorPos(x, y);
        }

        public static void MoveCursorBy(int deltax, int deltay)
        {
            Win32.POINT p = new Win32.POINT();
            Win32.GetCursorPos(out p);

            System.Threading.Thread.Sleep(1000 / 24);
            Win32.SetCursorPos(p.x + deltax, p.y + deltay);
        }
    }
}
