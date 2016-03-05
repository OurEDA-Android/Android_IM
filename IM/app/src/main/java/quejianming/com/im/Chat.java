package quejianming.com.im;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Chat extends ActionBarActivity implements View.OnClickListener, ServiceConnection {

    String uName;
    RecyclerView rv_chat;
    EditText et_chat;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.Adapter adapter_chat;
    LinearLayoutManager linearLayoutManager;
    ChatService.Binder binder;
    public void re(){
        BmobQuery<Message> query1 = new BmobQuery<Message>();
        query1.addWhereEqualTo("target",App.getUsername());
        query1.addWhereEqualTo("username",uName);
        BmobQuery<Message> query2 = new BmobQuery<Message>();
        query2.addWhereEqualTo("username", App.getUsername());
        query2.addWhereEqualTo("target",uName);
        List<BmobQuery<Message>> queries = new ArrayList<BmobQuery<Message>>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Message> queryMain = new BmobQuery<Message>();
        queryMain.or(queries);
        queryMain.findObjects(Chat.this, new FindListener<Message>() {
            @Override
            public void onSuccess(List<Message> list) {
                Message message;
                System.out.println("大小"+list.size());
                System.out.println(list + "" + list.size());
                for(int i = App.message.size();i<list.size();i++){
                    message = list.get(i);
                    System.out.println(message.getUsername());
                    System.out.println(message.getTarget());
                    App.message.add(message);
                }
                adapter_chat.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                toast(s);
            }
        });
        adapter_chat.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_chat);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("send");
                re();
                handler.sendEmptyMessage(0);
            }
        });
        re();
        adapter_chat.notifyDataSetChanged();
        bindSe();
    }

    private void bindSe() {
        BmobQuery<ChangeNumber> query = new BmobQuery<ChangeNumber>();
        System.out.println("aaa" + App.datas_List.get(App.chatPosition).getChangeId() + "sadcklsanc"+"" + App.change);
        query.addWhereEqualTo("objectId",App.datas_List.get(App.chatPosition).getChangeId());
        query.findObjects(this, new FindListener<ChangeNumber>() {
            @Override
            public void onSuccess(List<ChangeNumber> list) {
                System.out.println(list);
                if(list.size() != 0){
                    App.change = list.get(0).getChange();
                    System.out.println("success!!!"+App.change);
                }
                else{
                    System.out.println("success,但没找到");
                }
            }

            @Override
            public void onError(int i, String s) {
                System.out.println("失败啦"+s);
            }
        });
        Intent intent = new Intent(Chat.this,ChatService.class);
        startService(intent);
        bindService(intent, this,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(Chat.this,ChatService.class);
        unbindService(this);
        stopService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chatSend:
                Message message = new Message();
                message.setUsername(App.getUsername());
                message.setTarget(uName);
                message.setContain(et_chat.getText().toString());
                App.message.add(message);
                adapter_chat.notifyDataSetChanged();
                message.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast("发送成功");
                        System.out.println("update");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("发送失败");
                    }
                });
                ChangeNumber changeNumber = new ChangeNumber();
                changeNumber.setChange(App.change+1);
                changeNumber.update(this, App.datas_List.get(App.chatPosition).getChangeId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("update success");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        System.out.println("update fail  "+s);
                    }
                });
                linearLayoutManager.scrollToPosition(App.message.size()-1);
                et_chat.setText("");
                break;
        }
    }

    private MyHandler handler = new MyHandler();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (ChatService.Binder) service;
        binder.getChatServise().setCallback(new ChatService.Callback() {
            @Override
            public void onDataChange(int change) {
                System.out.println(change);
                App.change = change;
                System.out.println(App.change);
                System.out.println("lalala");
                System.out.println(App.message.size());
                re();
                System.out.println(App.message.size());
                System.out.println(App.datas_List);
                handler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

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
                    adapter_chat.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(App.message.size() - 1);
                    break;
                default:
                    break;
            }
        }
    }

    public class MyAdapter_chat extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder_chat(LayoutInflater.from(Chat.this).inflate(R.layout.list_cell_chat,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder_chat vh = (ViewHolder_chat) holder;
            Message message = App.message.get(position);
            vh.getTv_title().setText(message.getUsername());
            vh.getTv_contain().setText(message.getContain());
        }

        @Override
        public int getItemCount() {
            return App.message.size();
        }
    }
    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
