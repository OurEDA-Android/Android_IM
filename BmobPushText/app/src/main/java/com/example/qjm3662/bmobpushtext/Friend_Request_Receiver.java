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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by qjm3662 on 2016/3/24 0024.
 */
public class Friend_Request_Receiver extends BroadcastReceiver {

    private NotificationManager manager;
    public static final int NOTIFICATION_ID = 1100;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private Intent intent;
    private PendingIntent pendIntent;
    private Db db;
    private SQLiteDatabase dbRead, dbWrite;
    private Cursor c;
    private ContentValues cv;
    private ReceiverCallBack receiverCallBack;
    private JSONObject jsonObject;

    public Friend_Request_Receiver(ReceiverCallBack receiverCallBack) {
        this.receiverCallBack = receiverCallBack;
    }

    public Friend_Request_Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
        System.out.println("Receive + friend");
        // TODO Auto-generated method stub
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            //Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String s = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                jsonObject = new JSONObject(s);
                if (jsonObject.getInt("flag") < 0) {
                    if (jsonObject.getInt("flag") == -1) {
                        intent.setClass(context, ReceiveRequest.class);
                        System.out.println(jsonObject.getString("name"));
                        intent.putExtra("require_name", jsonObject.getString("name"));
                        System.out.println(intent.getStringExtra("require_name"));
                        System.out.println("REQUIRE  +  " + jsonObject);
                        pendIntent = PendingIntent.getActivity(context, 0, intent, pendIntent.FLAG_CANCEL_CURRENT);
//                        intent.setFlags(5);
                        builder.setSmallIcon(R.drawable.logoquan);
                        builder.setContentTitle("好友请求");
                        builder.setContentText(jsonObject.getString("contain"));
                        notification = builder.build();
                        notification.defaults = Notification.DEFAULT_VIBRATE;
                        notification.defaults = Notification.DEFAULT_SOUND;
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        notification.contentIntent = pendIntent;
                        manager.notify(NOTIFICATION_ID, notification);
                    } else if (jsonObject.getInt("flag") == -2) {
                        Friend f1 = new Friend();
                        f1.setUser1(BmobUser.getCurrentUser(context).getUsername());
                        f1.setUser2(jsonObject.getString("user2"));
                        f1.setPerson_note(App.PERSON_NOT);
                        f1.setFlag(App.friends_List.get(0).size());
                        f1.save(context, new SaveListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                System.out.println("Fail");
                            }
                        });
                        builder.setSmallIcon(R.drawable.logoquan);
                        builder.setContentTitle("好友请求");
                        builder.setContentText(jsonObject.getString("name") + "已接受你的好友请求");
                        notification = builder.build();
                        notification.defaults = Notification.DEFAULT_VIBRATE;
                        notification.defaults = Notification.DEFAULT_SOUND;
                        manager.notify(NOTIFICATION_ID, notification);
                        App.friends_List.get(0).add(f1);
                        App.user2name_list.add(f1.getUser2());
                        App.list_message_remind.get(0).add(0);
                        System.out.println("MEE-->"+App.user2name_list.get(0));
                        List<Message> ls = new ArrayList<Message>();
                        App.list_message.add(ls);
                        App.FriendNumber++;
                    } else {
                        builder.setSmallIcon(R.drawable.logoquan);
                        builder.setContentTitle("好友请求");
                        builder.setContentText(jsonObject.getString("user2") + "已拒绝你的好友请求");
                        notification = builder.build();
                        notification.defaults = Notification.DEFAULT_VIBRATE;
                        notification.defaults = Notification.DEFAULT_SOUND;
                        manager.notify(NOTIFICATION_ID, notification);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("ANOTHER   +  " + s);
        }
    }

    public interface ReceiverCallBack {
        public void change();
    }

    public void setReceiverCallBack(ReceiverCallBack receiverCallBack) {
        this.receiverCallBack = receiverCallBack;
    }

}
