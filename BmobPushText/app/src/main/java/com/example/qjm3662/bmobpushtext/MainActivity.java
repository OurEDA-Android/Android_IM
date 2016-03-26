package com.example.qjm3662.bmobpushtext;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnChildClickListener, com.example.qjm3662.bmobpushtext.message_change_receiver.ReceiverCallback /*implements MyPushMessageReceiver.ReceiverCallBack*/{

    SwipeRefreshLayout swipeRefreshLayout;
    ExpandableListView listView;
    MyAdapter adapter;
    private Db db;
    private SQLiteDatabase dbRead,dbWrite;
    Cursor c;
    private SlidingMenu slidingMenu;
    private message_change_receiver message_change_receiver = new message_change_receiver();
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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        unregisterReceiver(message_change_receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Db(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        //SlidingMenu相关初始化操作
        sliding_init();
        //输出一些SQLite数据库中的内容，便于调试
        SqliteTextData();


        if(BmobUser.getCurrentUser(MainActivity.this) == null){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_main);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipview);
            listView = (ExpandableListView) findViewById(R.id.list);
            swipeRefreshLayout.setOnRefreshListener(this);
            adapter = new MyAdapter();
            listView.setAdapter(adapter);
            listView.setOnChildClickListener(this);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(PushConstants.ACTION_MESSAGE);
        registerReceiver(message_change_receiver, filter);
        message_change_receiver.setReceiverCallback(this);
    }

    private void SqliteTextData() {
        c = dbRead.query("message",new String[]{"username","contain","flag"},null,null,null,null,null,null);
        System.out.println("c = " + c);
        while(c.moveToNext()){
            //指定索引
            System.out.println("out TABLE");
            String name = c.getString(c.getColumnIndex("username"));
            String contain = c.getString(c.getColumnIndex("contain"));
            String i = c.getString(c.getColumnIndex("flag"));
            System.out.println(String.format("name = %s,note = %s",name,contain));
        }
    }

    private void sliding_init() {
        slidingMenu = new SlidingMenu(this);
        //将SlidingMenu附加到当前Activity
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_WINDOW);
        //设置SlidingMenu的模式(设置SlidingMenu呈现在左边 )
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //指定菜单资源
        slidingMenu.setMenu(R.layout.sliding_menu);
        //设置菜单拖出来的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_dimen);
        //设置渐入渐出效果
        slidingMenu.setFadeDegree(0.35f);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, add_friend.class);
                startActivity(intent);
                break;
            case R.id.item_log_out:
                BmobUser.logOut(MainActivity.this);
                App.friends_List.get(0).clear();
                App.user2name_list.clear();
                App.isLogin = false;
                App.username = "";
                App.list_message.clear();
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.item_delete_message:
                dbWrite.delete("message",null,null);
                for(int i = 0;i<App.list_message.get(0).size();i++){
                    App.list_message.get(i).clear();
                }
//                Intent intent2 = new Intent(this,SlidingMenuText.class);
//                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(BmobUser.getCurrentUser(this) != null){
            App.isLogin = true;
            App.username = BmobUser.getCurrentUser(this).getUsername();
            System.out.println(App.username);
        }
        System.out.println("On Start");
        System.out.println(App.friends_List);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        System.out.println("MainActivity begin refresh!");
        new Thread() {
            @Override
            public void run() {
                super.run();
                init();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private MyHandler handler = new MyHandler();

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        App.chatPosition = childPosition;
        TextView tv = (TextView) v.findViewById(R.id.tv_name);
        Intent intent = new Intent();

        System.out.println(App.list_message_remind.get(0).get(0));
        App.list_message_remind.get(groupPosition).set(childPosition,0);
        System.out.println("On Click-->set__>" + "0" + "result:" + App.list_message_remind.get(groupPosition).get(childPosition));
        System.out.println(App.list_message_remind.get(0).get(0));
        handler.sendEmptyMessage(3);
        intent.putExtra("uName",tv.getText().toString());
        intent.setClass(MainActivity.this,Chat.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onChange() {
        handler.sendEmptyMessage(3);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    System.out.println(App.friends_List.get(0).size());
                    break;
                case 3:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    private class MyAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return App.frend_group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            System.out.println("friend_list--->"+App.friends_List.get(0).size());
            return App.friends_List.get(0).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return App.frend_group.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return App.friends_List.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //表示孩子是否和组ID是跨基础数据的更改稳定
        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list,null);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_group);

            tv.setText(App.frend_group.get(groupPosition));
            tv.setPadding(56,0, 0, 0);
            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            // 首先新建viewHolder实例，由于getView这个函数会在每个Item生成的时候都运行一次，
            // 所以我们用了这种写法
            ViewHolder viewHolder = null;
            // view还是null时，此时为第一次创建
            if(convertView == null){
                // 新建实例
                viewHolder = new ViewHolder();
                // 为View添加布局，此处View是Item的View
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell,null);
                // 为ViewHolder填充
                viewHolder.setTv_name((TextView) convertView.findViewById(R.id.tv_name));
                viewHolder.setImg((ImageView) convertView.findViewById(R.id.head_img));
                viewHolder.setTv_note((TextView) convertView.findViewById(R.id.tv_note));
                viewHolder.setImg_message_remind((ImageView) convertView.findViewById(R.id.img_message_remind));
                // 给View设定额外的标签，可存储一个数据，我们把ViewHolder存进去
                convertView.setTag(viewHolder);
            }else {
                // 此处已经是>=2 创建Item了
                // 把view从tag中拿出来
                viewHolder = (ViewHolder)convertView.getTag();
            }
            // 从ArrayList找到当前项的数据
            Friend friend = App.friends_List.get(groupPosition).get(childPosition);
            // 逐个填充
            viewHolder.getTv_name().setText(friend.getUser2());
            viewHolder.getTv_note().setText(friend.getPerson_note());
            viewHolder.getTv_note().setText("这个人很懒，什么也没有留下~");
            if(App.is_friend_success&&(App.list_message_remind.get(groupPosition).get(childPosition) == 1)){
                viewHolder.getImg_message_remind().setBackgroundResource(R.drawable.circle_receive);
            }else{
                viewHolder.getImg_message_remind().setBackgroundResource(R.drawable.circle_);
            }
            return convertView;
        }

        //孩子在指定的位置是可选的，即：arms中的元素是可点击的
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    private void init() {
        BmobQuery<Friend> query = new BmobQuery<Friend>();
        query.addWhereEqualTo("user1", App.getUsername());
        System.out.println("init");
        query.findObjects(MainActivity.this, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                System.out.println("success");
                System.out.println(list);
                for (int i = App.FriendNumber; i < list.size(); i++) {
                    App.friends_List.get(0).add(list.get(i));
                    App.FriendNumber++;
                    App.user2name_list.add(list.get(i).user2);
                    System.out.println("friend_list--->" + App.friends_List.get(0).size());
                    System.out.println("FriendNumber---->"+App.FriendNumber);
                    List<Message> ls = new ArrayList<Message>();
                    App.list_message.add(ls);
                }
                adapter.notifyDataSetChanged();
                System.out.println("init over");
            }

            @Override
            public void onError(int i, String s) {
                System.out.println(s);
            }
        });
    }

}
