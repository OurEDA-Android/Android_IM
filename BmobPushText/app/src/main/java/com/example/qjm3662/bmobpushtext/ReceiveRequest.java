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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.SaveListener;

public class ReceiveRequest extends AppCompatActivity implements View.OnClickListener, Friend_Request_Receiver.ReceiverCallBack {

    private TextView tv_name;
    private TextView tv_contain;
    private Button btn_accept;
    private Button btn_refuse;
    private BmobPushManager<MyInstallation> bmobPushManager;
    private Friend_Request_Receiver friend_request_receiver = new Friend_Request_Receiver();
    private String user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_request);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_contain = (TextView) this.findViewById(R.id.tv_note);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);
        btn_refuse = (Button) findViewById(R.id.btn_refuse);
        btn_refuse.setOnClickListener(this);

        Intent intent = getIntent();
        user2 = intent.getStringExtra("require_name");

        bmobPushManager = new BmobPushManager<MyInstallation>(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushConstants.ACTION_MESSAGE);
        registerReceiver(friend_request_receiver, filter);
        friend_request_receiver.setReceiverCallBack(ReceiveRequest.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(friend_request_receiver);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_accept:
                result(true,-2);
                Friend f1 = new Friend();
                f1.setUser1(App.getUsername());
                f1.setUser2(user2);
                f1.setPerson_note(App.PERSON_NOT);
                f1.setFlag(App.friends_List.get(0).size());
                f1.save(ReceiveRequest.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        System.out.println("Fail");
                    }
                });
                App.friends_List.get(0).add(f1);
                App.user2name_list.add(f1.getUser2());
                App.list_message_remind.get(0).add(0);
                List<Message> ls = new ArrayList<Message>();
                App.list_message.add(ls);
                App.FriendNumber++;
                finish();
                break;
            case R.id.btn_refuse:
                result(false,-3);
                finish();
                break;
        }
    }

    private void result(boolean b,int i) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("flag",i);
            jsonObject.put("name",b);
            jsonObject.put("user2",App.getUsername());
            BmobQuery<MyInstallation> query = MyInstallation.getQuery();
            query.addWhereEqualTo("username", user2);
            query.addWhereNotEqualTo("installationId", BmobInstallation.getInstallationId(ReceiveRequest.this));
            System.out.println("user2_id(addFriend)---->"+user2);
            bmobPushManager.setQuery(query);
            bmobPushManager.pushMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void change() {

    }
}
