package quejianming.com.im;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    RecyclerView rv;
    SwipeRefreshLayout swipeRefreshLayout;
    public static RecyclerView.Adapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        if(BmobUser.getCurrentUser(this) != null){
            App.isLogin = true;
            App.username = BmobUser.getCurrentUser(this).getUsername();
            System.out.println(App.username);
        }

        System.out.println("On Start");
        System.out.println(App.datas_List);
        init();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On Creat");
        if(BmobUser.getCurrentUser(MainActivity.this) == null){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_main);
            rv = (RecyclerView)findViewById(R.id.rv);
            swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);

            findViewById(R.id.btn_addFriend).setOnClickListener(this);
            findViewById(R.id.btn_logout).setOnClickListener(this);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            rv.setLayoutManager(layoutManager);
            adapter = new MyAdapter();
            rv.setAdapter(adapter);
            rv.addItemDecoration(new ItemDivider(MainActivity.this, R.drawable.shap));
            rv.getAdapter();
            rv.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    Toast.makeText(MainActivity.this, "位置为" + position, Toast.LENGTH_LONG).show();
                    App.chatPosition = position;
                    System.out.println(position);
                    TextView tv = (TextView) view.findViewById(R.id.textView);
                    Intent intent = new Intent();
                    intent.putExtra("uName",tv.getText().toString());
                    intent.setClass(MainActivity.this,Chat.class);
                    startActivity(intent);
                }
            }));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    System.out.println("success");
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            init();
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });
            adapter.notifyDataSetChanged();
        }

    }

    private MyHandler handler = new MyHandler();
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addFriend:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddFriend.class);
                startActivity(intent);
                break;
            case R.id.btn_logout:
                BmobUser.logOut(MainActivity.this);
                App.datas_List.clear();
                App.user2.clear();
                App.isLogin = false;
                App.username = "";
                App.message.clear();
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder vh = (ViewHolder)holder;
            Friend friend = App.datas_List.get(position);
            vh.getTv().setText(friend.getUser2());
        }

        @Override
        public int getItemCount() {
            return App.datas_List.size();
        }
    }
    private void init() {
        BmobQuery<Friend> query = new BmobQuery<Friend>();
        query.addWhereEqualTo("user1", App.getUsername());
        query.findObjects(MainActivity.this, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                System.out.println("success");
                System.out.println(list);
                for (int i = 0; i < list.size(); i++) {
                    if (!App.user2.contains(list.get(i).user2)) {
                        App.datas_List.add(list.get(i));
                        App.user2.add(list.get(i).user2);
                    }
                }
                adapter.notifyDataSetChanged();
                System.out.println("init over");
                System.out.println(App.datas_List);
                System.out.println(App.user2);
            }

            @Override
            public void onError(int i, String s) {
                System.out.println(s);
            }
        });
    }
    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
