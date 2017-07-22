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
            StartUdpServer(port);
            
            Console.WriteLine("Session is over!");
            Console.ReadKey(true);
        }

        public static void StartUdpServer(int port)
        {
            UdpClient reciever = new UdpClient(port);
            IPEndPoint RemoteIpEndPoint = new IPEndPoint(IPAddress.Any, port);

            var watch = System.Diagnostics.Stopwatch.StartNew();
            var q = false;

            while (!q)
            {
                var receiveBytes = reciever.Receive(ref RemoteIpEndPoint);
                var s = Encoding.UTF8.GetString(receiveBytes);

                var array = s.Split(' ');
                var x = int.Parse(array[0]);
                var y = int.Parse(array[1]);

                MoveCursorBy(x, y);
                if (s.Equals(".")) q = true;

                var elapsedMs = watch.ElapsedMilliseconds;
                elapsedMs += elapsedMs == 0 ? 1 : 0;
                var fps = 1000 / elapsedMs;
                Console.WriteLine($"X:{x} Y:{y} Time taken: {elapsedMs}ms FPS:{fps}");
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
            Win32.SetCursorPos(p.x + deltax, p.y + deltay);
        }
    }
}
