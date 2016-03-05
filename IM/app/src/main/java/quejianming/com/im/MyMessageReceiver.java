package quejianming.com.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import cn.bmob.push.PushReceiver;

/**
 * Created by Administrator on 2016/2/6 0006.
 */
public class MyMessageReceiver extends BroadcastReceiver {
    public MyMessageReceiver() {
        super();
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("msg")){
            Log.d("bmob", "客户端收到的消息：" + intent.getStringExtra("msg"));
            Message message = new Message();
            message.setUsername(App.getUsername());
            message.setContain(intent.getStringExtra("msg"));
            App.message.add(message);
        }
    }
}
