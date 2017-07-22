package org.nevack.cursormover;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private DataOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String serverName = "192.168.1.4";
        int port = 7000;
        try {
            Log.d("Tag", "Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            Log.d("Tag","Just connected to " + client.getRemoteSocketAddress());
            out = new DataOutputStream(client.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

        View view = findViewById(R.id.touch);
        view.setOnTouchListener((v, event) -> {

            v.performClick();
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    String s = Math.round(x) + " " + Math.round(y);
                    Log.d("Tag", s);
//                    int c = s.length();
//                    try {
//                        out.writeByte(c);
//                        out.writeBytes(s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    byte[] buf = s.getBytes(Charset.forName("UTF-8"));
                    try {
                        InetAddress inetAddress = InetAddress.getByName(serverName);
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                                inetAddress, port);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }

            return true;
        });


    }
}
