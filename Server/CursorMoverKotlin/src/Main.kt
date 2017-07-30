import java.awt.Robot
import java.awt.AWTException
import java.awt.MouseInfo
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.charset.Charset

fun main(args: Array<String>) {
    val port = 7000
    try {
        val socket = DatagramSocket(port)
        val robot = Robot()

        val buf = ByteArray(256)
        while(true) {
            val packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            val s = String(buf, Charset.forName("UTF-8")).replace("\u0000", "").split(" ")

            var x = s[0].toInt()
            var y = s[1].toInt()


            x += MouseInfo.getPointerInfo().location.x
            y += MouseInfo.getPointerInfo().location.y
            robot.mouseMove(x, y)
            println("$x $y")
        }
    } catch (e: AWTException) { }

}