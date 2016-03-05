package quejianming.com.im;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ChatService extends Service {

    private boolean running = false;
    private String chNumId;
    private int change;
    public ChatService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("绑定服务");
        return new Binder();
    }
    public class Binder extends android.os.Binder{
        public ChatService getChatServise(){
            return ChatService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        System.out.println("启动服务");
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(running){
                    BmobQuery<ChangeNumber> query = new BmobQuery<ChangeNumber>();
                    query.addWhereEqualTo("objectId",App.datas_List.get(App.chatPosition).getChangeId());
                    query.findObjects(ChatService.this, new FindListener<ChangeNumber>() {
                        @Override
                        public void onSuccess(List<ChangeNumber> list) {
                            if(App.change != list.get(0).getChange()){
                                callback.onDataChange(list.get(0).getChange());
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        System.out.println("结束服务");
    }

    private Callback callback;

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback{
        //自定义一个接口，并用一个int作为参数传入
        //使得复写该函数时可以得到一个整型数
        void onDataChange(int change);
    }
}
