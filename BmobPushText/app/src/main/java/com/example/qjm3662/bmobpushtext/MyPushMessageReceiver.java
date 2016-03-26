package com.example.qjm3662.bmobpushtext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by qjm3662 on 2016/3/14 0014.
 */
public class MyPushMessageReceiver extends BroadcastReceiver{

    private ReceiverCallBack receiverCallBack;
    private NotificationManager manager;
    public static  final int NOTIFICATION_ID = 1200;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private Intent intent;
    private PendingIntent pendIntent;
    private Db db;
    private SQLiteDatabase dbRead,dbWrite;
    private Cursor c;
    private ContentValues cv;

    public MyPushMessageReceiver(ReceiverCallBack receiverCallBack) {
        this.receiverCallBack = receiverCallBack;
    }
    public MyPushMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new Db(context);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
        System.out.println("Receive");
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            //Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String s = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                JSONObject jsonObject = new JSONObject(s);
                System.out.println(jsonObject + "dfsvs");
                if(receiverCallBack != null){
                    receiverCallBack.setText(s);
                }else{
                    System.out.println("null");
                }
                if(!App.isAppRuning){
                    intent.setClass(context, Chat.class);
                    pendIntent = PendingIntent.getActivity(context,0,intent,0);
                    builder.setSmallIcon(R.drawable.logoquan);
                    builder.setContentTitle(jsonObject.getString("username"));
                    builder.setContentText(jsonObject.getString("contain"));
                    notification = builder.build();
                    notification.defaults = Notification.DEFAULT_VIBRATE;
                    notification.defaults = Notification.DEFAULT_SOUND;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.contentIntent = pendIntent;
                    manager.notify(NOTIFICATION_ID, notification);
                    cv = new ContentValues();
                    cv.put("username",App.getUsername());
                    cv.put("name",jsonObject.getString("username"));
                    cv.put("contain",jsonObject.getString("contain"));
                    cv.put("flag", jsonObject.getString("flag"));
                    dbWrite.insert("message",null,cv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public interface ReceiverCallBack{
        public void setText(String s);
    }

    public void setReceiverCallBack(ReceiverCallBack receiverCallBack) {
        this.receiverCallBack = receiverCallBack;
    }

}
