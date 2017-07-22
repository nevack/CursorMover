package org.nevack.cursormover;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String serverName = "192.168.1.4";
        int port = 7000;

        final float[] lastx = {0};
        final float[] lasty = {0};

        View view = findViewById(R.id.touch);
        view.setOnTouchListener((v, event) -> {

            v.performClick();
            float x = event.getX();
            float y = event.getY();

            String s = "";
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    s += "0 0";
                    lastx[0] = x;
                    lasty[0] = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    s += Math.round(x - lastx[0]) + " " + Math.round(y - lasty[0]) + " ";
                    lastx[0] = x;
                    lasty[0] = y;
                    break;
                case MotionEvent.ACTION_UP:
                    s += "0 0";
                case MotionEvent.ACTION_CANCEL:
                    break;
            }

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

            return true;
        });


    }
}
