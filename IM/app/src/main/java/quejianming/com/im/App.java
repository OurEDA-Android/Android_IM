package quejianming.com.im;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/5 0005.
 */
public class App extends Application{
    public static String applicationId = "0157a0303696698edec21e03c0c37b4f";
    public static String username = "1";
    public static Boolean  isLogin = false;
    public static List<Friend> datas_List = new ArrayList<Friend>(){};
    public static List<String> user2 = new ArrayList<String>();
    public static List<Message> message = new ArrayList<Message>();
    public static Integer change = 0;
    public static Integer chatPosition = 0;
    public static Boolean getIsLogin() {

        return isLogin;}

    public static String getApplicationId() {
        return applicationId;
    }

    public static String getUsername() {
        return username;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*初始化操作*/
        Message msg =  new Message();
        init();
//        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
//        BmobChat.DEBUG_MODE = true;
//        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
//        BmobChat.getInstance(this).init(applicationId);
//        //省略其他代码
    }

    private void init() {
        System.out.println(username);
        //初始化BmobSDK
        Bmob.initialize(this,getApplicationId());
        //使用推送服务的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        //启动推送服务
        BmobPush.startWork(this, App.getApplicationId());
        if(BmobUser.getCurrentUser(this)!=null){
            BmobQuery<Friend> query = new BmobQuery<Friend>();
            query.addWhereEqualTo("user1", App.getUsername());
            query.findObjects(App.this, new FindListener<Friend>() {
                @Override
                public void onSuccess(List<Friend> list) {
                    System.out.println("success");
                    for (int i = 0; i < list.size(); i++) {
                        if (!App.user2.contains(list.get(i).user2)) {
                            App.datas_List.add(list.get(i));
                            App.user2.add(list.get(i).user2);
                        }
                        System.out.println(App.datas_List);
                        System.out.println(App.user2);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    System.out.println(s);
                }
            });
            System.out.println("App OnCreat Stop");
        }

    }

}
