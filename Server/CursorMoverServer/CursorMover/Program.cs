using AudioSwitcher.AudioApi.CoreAudio;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace CursorMover
{
    class Program
    {
        static void Main(string[] args)
        {
            var port = 7000;
            StartUdpServer(port);
        }

        public static void StartUdpServer(int port)
        {
            UdpClient reciever = new UdpClient(port);
            IPEndPoint RemoteIpEndPoint = new IPEndPoint(IPAddress.Any, port);
            
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
                
                Console.WriteLine($"Deltas: X={x} Y={y}");
            }
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

        public static int GetVolume()
        {
            var defaultPlaybackDevice = new CoreAudioController().DefaultPlaybackDevice;
            return Convert.ToInt32(defaultPlaybackDevice.Volume);
        }

        public static void SetVolume(int volume)
        {
            if (volume < 0 || volume > 100) throw new ArgumentOutOfRangeException("volume");
            var defaultPlaybackDevice = new CoreAudioController().DefaultPlaybackDevice;
            defaultPlaybackDevice.Volume = volume;
        }
    }
}
