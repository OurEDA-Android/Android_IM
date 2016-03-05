package com.quejianming.sockettext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends ActionBarActivity {

    EditText editText;
    EditText edit_connect;
    EditText edit_connect_duan;
    Button btn_connect;
    TextView text;
    Button btn_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_connect = (EditText)findViewById(R.id.edit_connect);
        edit_connect_duan = (EditText)findViewById(R.id.edit_connect_duan);
        text = (TextView)findViewById(R.id.text);
        editText = (EditText) findViewById(R.id.edit);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new ConnectListener());
        btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new SendListener());
    }

    private class ConnectListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            connect();
        }
    }

    private class SendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            send();
        }
    }

    //----------------------------------------
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    private void connect() {
        final String ip = edit_connect.getText().toString();
        final String duan = edit_connect_duan.getText().toString();
        final AsyncTask<Void,String,Void> read = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    System.out.println(ip+duan);
                    socket = new Socket(ip, Integer.parseInt(duan));
                    System.out.println("asdasd");
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("连接成功");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("连接失败");
                }
                try {
                    String line;
                    while((line = reader.readLine())!=null){
                        System.out.println("lalala");
                        publishProgress(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if(values[0].equals("@success")){
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }
                text.append("别人说："+values[0]+"\n");
            }
        };
        read.execute();
    }

    private void send() {
        try {
            text.append("我说："+editText.getText().toString()+"\n");
            writer.write(editText.getText().toString() + "\n");
            writer.flush();
            editText.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}