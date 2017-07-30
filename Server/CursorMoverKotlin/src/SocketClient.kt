import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.*

fun main(args: Array<String>) {
    val serverName = "127.0.0.1" //args[0]
    val port = 7000 //Integer.parseInt(args[1])

    try {
        println("Connecting to $serverName on port $port")
        val client = Socket(serverName, port)

        println("Just connected to " + client.remoteSocketAddress)
        val outToServer = client.getOutputStream()
        val out = DataOutputStream(outToServer)
        val inFromServer = client.getInputStream()
        val input = DataInputStream(inFromServer)

        //while (true) out.writeUTF(readLine())

        while (true) {
            val s = readLine()
            val c = s!!.length
            out.writeByte(c)
            out.writeBytes(s)
        }

        System.out.println("Server says " + input.readUTF())
        client.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}