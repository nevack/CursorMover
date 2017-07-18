package org.nevack.cursormover;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            OutputStream outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
        } catch(IOException e) {
            e.printStackTrace();
        }

        View view = findViewById(R.id.touch);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.performClick();
                float x = event.getX();
                float y = event.getY();

//                if(event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                    String s = Math.round(x) + " " + Math.round(y);
//                    Log.d("Tag", s);
//                    int c = s.length();
//                    try {
//                        out.writeByte(c);
//                        out.writeBytes(s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                    case MotionEvent.ACTION_MOVE: // движение
                        String s = Math.round(x) + " " + Math.round(y);
                        Log.d("Tag", s);
                        int c = s.length();
                        try {
                            out.writeByte(c);
                            out.writeBytes(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                    case MotionEvent.ACTION_CANCEL:
                        // ничего не делаем
                        break;
                }

                return true;
            }
        });
    }
}
