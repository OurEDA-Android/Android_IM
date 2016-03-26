package com.example.qjm3662.bmobpushtext;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by qjm3662 on 2016/3/12 0012.
 */
public class App extends Application {
    //Bmob的应用Key
    public static String applicationId = "0157a0303696698edec21e03c0c37b4f";
    //本机的InstallationId
    public static String installtionId = "";
    public static int width;
    public static int height;

    //
    public static List<Integer> list_change = new ArrayList<>();

    //用一个双层链表储存临时储存消息，使其呈现在RecycleView当中（消息记录存放在SQLite数据库当中）
    public static List<List<Message>> list_message = new ArrayList<List<Message>>();
    //当前用户的用户名
    public static String username = "1";
    //判断当前设备是否有用户登录
    public static Boolean  isLogin = false;
    public static String PERSON_NOT = "";
    //用来储存好友信息
    public static List<List<Friend>> friends_List = new ArrayList<List<Friend>>();

    //用来统计好友数目
    public static Integer FriendNumber = 0;

    //好友分组
    public static List<String> frend_group = new ArrayList<>();
    //user2的集合
    public static List<String> user2name_list = new ArrayList<String>();
    //聊天时当前聊天对象在MainActivity中对应的位置
    public static Integer chatPosition = 0;
    public static int change = 0;
    //Sqlite相关变量
    private Db db;
    private SQLiteDatabase dbs;
    private SQLiteDatabase dbRead,dbWrite;
    private Cursor c;
    //用来判断应用程序是否处于后台
    public static boolean isAppRuning = false;
    //用来标记是否有新消息
    public static List<List<Integer>> list_message_remind = new ArrayList<List<Integer>>();
    //用来标记聊天窗口是否打开
    public static boolean isChat_running = false;
    //用来标记好友列表是否加载成功
    public static boolean is_friend_success = false;

    public static String getApplicationId() {
        return applicationId;
    }

    public static Boolean getIsLogin() {
        return isLogin;
    }

    public static String getUsername() {
        return username;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取SQLite操作所需的各个对象
        db = new Db(App.this);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();
        init();
        //新建一个分组（我的好友）
        List<Friend> fl = new ArrayList<>();
        friends_List.add(fl);
        frend_group.add("我的好友");
        //新建一个list_message_remind_group_member
        List<Integer> l = new ArrayList<Integer>();
        list_message_remind.add(l);
        isAppRuning = true;
    }

    private void init() {
        installtionId = BmobInstallation.getInstallationId(this);
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, getApplicationId());
        //使用推送服务的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        //启动推送服务
        BmobPush.startWork(this, App.getApplicationId());
        // 初始化 Bmob SDK
        if(BmobUser.getCurrentUser(this)!=null){
            updateINSTALL();
            gainFriendList();
            System.out.println("App OnCreat Stop");
        }
    }

    private void gainFriendList() {
        BmobQuery<Friend> query1 = new BmobQuery<Friend>();
        query1.addWhereEqualTo("user1", BmobUser.getCurrentUser(this).getUsername());
        System.out.println(App.getUsername() + "name");
        query1.findObjects(App.this, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                System.out.println("success啦");
                System.out.println("Frend_list size:" + list.size());
                for (int i = 0; i < list.size(); i++) {
                    if (!App.user2name_list.contains(list.get(i).user2)) {
                        App.friends_List.get(0).add(list.get(i));
                        App.list_message_remind.get(0).add(0);
                        App.user2name_list.add(list.get(i).user2);
                        FriendNumber++;
                        System.out.println("App_FriendNumber--->"+FriendNumber);
                        List<Message> ls = new ArrayList<Message>();
                        list_message.add(ls);
                        System.out.println("listMessage+" + list_message.size());
                    }
                }
                is_friend_success = true;
                gainMessage();
            }

            @Override
            public void onError(int i, String s) {
                System.out.println(s);
            }
        });
    }

    private void gainMessage() {
        c = dbRead.query("message",new String[]{"username","name","contain","flag"},"username = "+"\'"+BmobUser.getCurrentUser(App.this).getUsername()+"\'",null,null,null,"\'flag\'");
        System.out.println("FLAG___");
        List<Message> list = null;
        while(c.moveToNext()){
            while(c.moveToNext()){
                Message message = new Message();
                message.setUsername(c.getString(c.getColumnIndex("username")));
                message.setTarget(c.getString(c.getColumnIndex("name")));
                message.setContain(c.getString(c.getColumnIndex("contain")));
                message.print();
                list_message.get(c.getInt(c.getColumnIndex("flag"))).add(message);
            }
        }
    }

    private void updateINSTALL() {
        BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(this, new FindListener<MyInstallation>() {
            @Override
            public void onSuccess(final List<MyInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    MyInstallation mbi = object.get(0);
                    mbi.setUid(BmobUser.getCurrentUser(App.this).getObjectId());
                    mbi.setUsername(BmobUser.getCurrentUser(App.this).getUsername());
                    mbi.update(App.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新成功");
                            BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();
                            query.addWhereEqualTo("uid", BmobUser.getCurrentUser(App.this).getObjectId());
                            query.addWhereNotEqualTo("installationId", BmobInstallation.getCurrentInstallation(App.this).getInstallationId());
                            System.out.println("this->"+BmobInstallation.getCurrentInstallation(App.this));
                            query.findObjects(App.this, new FindListener<MyInstallation>() {
                                @Override
                                public void onSuccess(List<MyInstallation> list) {
                                    MyInstallation installation;
                                    for(int i = 0;i<list.size();i++){
                                        System.out.println(list.size());
                                        System.out.println(list);
                                        System.out.println(list.get(0).getUid());
                                        System.out.println(list.get(0).getInstallationId());
                                        installation = new MyInstallation(App.this);
                                        installation.setObjectId(list.get(i).getObjectId());
                                        installation.delete(App.this, new DeleteListener() {

                                            @Override
                                            public void onSuccess() {
                                                // TODO Auto-generated method stub
                                                Log.i("bmob","删除成功");
                                            }

                                            @Override
                                            public void onFailure(int code, String msg) {
                                                // TODO Auto-generated method stub
                                                Log.i("bmob","删除失败："+msg);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });

                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                } else {
                    System.out.println("没有设备信息");
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

}
