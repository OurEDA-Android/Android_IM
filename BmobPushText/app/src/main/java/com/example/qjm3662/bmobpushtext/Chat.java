package com.example.qjm3662.bmobpushtext;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Chat extends AppCompatActivity implements View.OnClickListener, MyPushMessageReceiver.ReceiverCallBack {

    String uName;
    RecyclerView rv_chat;
    EditText et_chat;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.Adapter adapter_chat;
    LinearLayoutManager linearLayoutManager;
    MyPushMessageReceiver pushMessageReceiver = new MyPushMessageReceiver();
    BmobPushManager<MyInstallation> bmobPushManager;
    private Db db;
    private SQLiteDatabase dbRead,dbWrite;
    private Cursor c;
    private ContentValues cv;
    @Override
    protected void onResume() {
        super.onResume();
        App.isAppRuning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.isAppRuning = false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        App.isChat_running = true;

        //获取SQLite操作所需的工具类对象
        db = new Db(this);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();

        Intent intent = this.getIntent();
        uName = intent.getStringExtra("uName");
        adapter_chat = new MyAdapter_chat();
        rv_chat = (RecyclerView)findViewById(R.id.rv_chat);
        et_chat = (EditText)findViewById(R.id.et_chat);
        linearLayoutManager = new LinearLayoutManager(Chat.this);
        linearLayoutManager.setStackFromEnd(true);
        rv_chat.setLayoutManager(linearLayoutManager);
        rv_chat.setAdapter(adapter_chat);
        findViewById(R.id.btn_chatSend).setOnClickListener(this);


        bmobPushManager = new BmobPushManager<MyInstallation>(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_chat);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessage(0);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(PushConstants.ACTION_MESSAGE);
        registerReceiver(pushMessageReceiver, filter);
        pushMessageReceiver.setReceiverCallBack(this);
    }

    private MyHandler handler = new MyHandler();

    @Override
    public void setText(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            Message message = new Message();
            message.setUsername(jsonObject.getString("username"));
            message.setContain(jsonObject.getString("contain"));
            message.setTarget(App.getUsername());
            if(!App.list_message.contains(message)){
                App.list_message.get(App.chatPosition).add(message);
                System.out.println("SB"+App.list_message.get(App.chatPosition));
            }
            cv = new ContentValues();
            cv.put("username",App.getUsername());
            cv.put("name", jsonObject.getString("username"));
            cv.put("contain", jsonObject.getString("contain"));
            cv.put("flag",jsonObject.getString("flag"));
            dbWrite.insert("message", null, cv);
            System.out.println("chat__+");
            message.print();
            System.out.println(message);
            adapter_chat.notifyDataSetChanged();
            handler.sendEmptyMessage(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    System.out.println("what");
                    swipeRefreshLayout.setRefreshing(false);
                    adapter_chat.notifyDataSetChanged();
                    break;
                case 1:
                    System.out.println("case1");
                    linearLayoutManager.scrollToPosition(App.list_message.get(App.chatPosition).size()- 1);
                    adapter_chat.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pushMessageReceiver);
        App.isChat_running = false;
        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chatSend:
                Message message = new Message();
                message.setUsername(App.getUsername());
                message.setTarget(uName);
                message.setContain(et_chat.getText().toString());
                App.list_message.get(App.chatPosition).add(message);
                adapter_chat.notifyDataSetChanged();
                System.out.println("contain--->"+et_chat.getText().toString());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username",App.getUsername());
                    jsonObject.put("contain", et_chat.getText().toString());
                    jsonObject.put("flag",App.chatPosition);
                    cv = new ContentValues();
                    cv.put("username",App.getUsername());
                    cv.put("name", jsonObject.getString("username"));
                    cv.put("contain", jsonObject.getString("contain"));
                    cv.put("flag",App.chatPosition);
                    dbWrite.insert("message", null, cv);
                    BmobQuery<MyInstallation> query = MyInstallation.getQuery();
                    query.addWhereEqualTo("uid", App.friends_List.get(0).get(App.chatPosition).getUser2_id());
                    query.addWhereNotEqualTo("installationId", BmobInstallation.getInstallationId(Chat.this));
                    System.out.println("user2_id---->"+App.friends_List.get(0).get(App.chatPosition).getUser2_id());
                    bmobPushManager.setQuery(query);
                    bmobPushManager.pushMessage(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                message.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("update");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("发送失败");
                    }
                });
                linearLayoutManager.scrollToPosition(App.list_message.get(App.chatPosition).size()-1);
                et_chat.setText("");
                break;
        }
    }

    private class MyAdapter_chat extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder_chat(LayoutInflater.from(Chat.this).inflate(R.layout.list_cell_chat,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder_chat vh = (ViewHolder_chat) holder;
            Message message = App.list_message.get(App.chatPosition).get(position);
            vh.getTv_title().setText(message.getUsername()+":");
            vh.getTv_contain().setText(message.getContain());
        }

        @Override
        public int getItemCount() {
            int a = 0;
            if(App.list_message.get(App.chatPosition).size() != 0){
                a = App.list_message.get(App.chatPosition).size();
                System.out.println(a);
            }
            return a;
        }
    }
}
