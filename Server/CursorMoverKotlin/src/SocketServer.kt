
import java.awt.Robot
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.ServerSocket

fun main(args: Array<String>) {
    val port = 7000 //Integer.parseInt(args[0])
    try {
        val t = GreetingServer(port)
        t.start()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

class GreetingServer(port: Int) : Thread() {
    private val serverSocket: ServerSocket = ServerSocket(port)

    init {
        serverSocket.soTimeout = 10000
    }

    override fun run() {
        while (true) {
            try {
                println("Waiting for client on port " +
                        serverSocket.localPort + "...")
                val server = serverSocket.accept()

                println("Just connected to " + server.remoteSocketAddress)
                val input = DataInputStream(server.getInputStream())
                val out = DataOutputStream(server.getOutputStream())

                var q = false
                while (!q) {
                    val s = input.readUTF()
                    println(s)

                    val x = s.split(" ")[0].toInt()
                    val y = s.split(" ")[1].toInt()

                    mouseMove(x, y)

                    if (s == ".") q = true
                }
                out.writeUTF("Thank you for connecting to " + server.localSocketAddress
                        + "\nGoodbye!")
                server.close()

            } catch (s: SocketTimeoutException) {
                println("Socket timed out!")
                break
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }

        }
    }

    fun mouseMove(x: Int, y: Int) {
        val robot = Robot()
        robot.mouseMove(x, y)
    }
}