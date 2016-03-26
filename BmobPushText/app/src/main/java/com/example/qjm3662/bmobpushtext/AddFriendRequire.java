package com.example.qjm3662.bmobpushtext;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;

public class AddFriendRequire extends AppCompatActivity implements Friend_Request_Receiver.ReceiverCallBack, View.OnClickListener {

    BmobPushManager<MyInstallation> bmobPushManager;
    Friend_Request_Receiver friend_request_receiver = new Friend_Request_Receiver();
    private EditText et_Request_information;
    private Button btn_send_request;
    private String user2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_require);
        et_Request_information = (EditText) findViewById(R.id.et_reruestInformation);
        btn_send_request = (Button) findViewById(R.id.btn_sendRequest);
        btn_send_request.setOnClickListener(this);
        Intent intent = getIntent();
        user2 = intent.getStringExtra("user2");

        bmobPushManager = new BmobPushManager<MyInstallation>(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushConstants.ACTION_MESSAGE);
        try{
            registerReceiver(friend_request_receiver, filter);
        }catch (Exception e){
            System.out.println(e);
        }
        friend_request_receiver.setReceiverCallBack(AddFriendRequire.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(friend_request_receiver);
    }



    @Override
    public void onClick(View v) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("flag",-1);
            jsonObject.put("name",App.getUsername());
            jsonObject.put("contain",et_Request_information.getText().toString());
            System.out.println(jsonObject);
            BmobQuery<MyInstallation> query = MyInstallation.getQuery();
            query.addWhereEqualTo("username", user2);
            System.out.println(user2);
            query.addWhereNotEqualTo("installationId", BmobInstallation.getInstallationId(AddFriendRequire.this));
            System.out.println("user2_id(addFriend)---->"+user2);
            bmobPushManager.setQuery(query);
            bmobPushManager.pushMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void change() {

    }
}
