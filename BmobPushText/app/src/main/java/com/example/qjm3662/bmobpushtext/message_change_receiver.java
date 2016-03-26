package com.example.qjm3662.bmobpushtext;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by qjm3662 on 2016/3/26 0026.
 */
public class message_change_receiver extends BroadcastReceiver{
    private NotificationManager manager;
    private JSONObject jsonObject;
    private ReceiverCallback receiverCallback;
    private Db db;
    private SQLiteDatabase dbRead,dbWrite;
    private Cursor c;
    private ContentValues cv;

    public message_change_receiver(ReceiverCallback receiverCallback) {
        this.receiverCallback = receiverCallback;
    }

    public message_change_receiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        db = new Db(context);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            //Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String s = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                jsonObject = new JSONObject(s);
                int i;
                if (jsonObject.getInt("flag") >= 0&&App.is_friend_success) {
                    for(i = 0;i<App.user2name_list.size();i++){
                        System.out.println("user2name--->"+App.user2name_list.get(i));
                        System.out.println(jsonObject.getString("username"));
                        if(App.user2name_list.get(i).equals(jsonObject.getString("username"))){
                            System.out.println(i+"----->");
                            System.out.println(App.user2name_list.get(i));
                            break;
                        }
                    }
                    System.out.println(i+"----->");
                    System.out.println(App.user2name_list.get(i));
                    App.list_message_remind.get(0).set(i, 1);
                    if(!App.isChat_running){
                        cv = new ContentValues();
                        cv.put("username",App.getUsername());
                        cv.put("name",jsonObject.getString("username"));
                        cv.put("contain",jsonObject.getString("contain"));
                        cv.put("flag", jsonObject.getString("flag"));
                        dbWrite.insert("message", null, cv);
                        System.out.println("addCV--->" + cv);
                        Message message = new Message();
                        message.setUsername(App.getUsername());
                        message.setTarget(jsonObject.getString("username"));
                        message.setContain(jsonObject.getString("contain"));
                        message.print();
                        System.out.println("ADDCV-->"+jsonObject.getInt("flag"));
                        App.list_message.get(i).add(message);
                    }
                    if(receiverCallback != null){
                        receiverCallback.onChange();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public interface ReceiverCallback{
        public void onChange();
    }

    public ReceiverCallback getReceiverCallback() {
        return receiverCallback;
    }

    public void setReceiverCallback(ReceiverCallback receiverCallback) {
        this.receiverCallback = receiverCallback;
    }
}
