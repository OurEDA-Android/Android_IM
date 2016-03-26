package com.example.qjm3662.bmobpushtext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class add_friend extends AppCompatActivity {

    EditText et_addFriend;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        et_addFriend = (EditText)this.findViewById(R.id.et_addFriend);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.findViewById(R.id.btn_addFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(et_addFriend.getText().toString())){
                    toast("用户名不能为空");
                }
                else if(et_addFriend.getText().toString().equals(App.getUsername())) {
                    toast("不能加自己为好友");
                }
                else{
                    BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                    query.addWhereEqualTo("username", et_addFriend.getText().toString());
                    query.findObjects(add_friend.this, new FindListener<MyUser>() {
                        @Override
                        public void onSuccess(List<MyUser> list) {
                            if(list.size() == 0){
                                toast("用户名不存在，请重新输入");
                            }
                            else{
                                BmobQuery<Friend> query1 = new BmobQuery<Friend>();
                                query1.addWhereEqualTo("user1",App.getUsername());
                                query1.addWhereEqualTo("user2",et_addFriend.getText().toString());
                                query1.findObjects(add_friend.this, new FindListener<Friend>() {
                                    @Override
                                    public void onSuccess(final List<Friend> list) {
                                        if(list.size() == 0){
                                            Intent intent = new Intent(add_friend.this,AddFriendRequire.class);
                                            intent.putExtra("user2",et_addFriend.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            toast("该好友已存在");
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            toast(s);
                        }
                    });
                }
            }
        });
    }
    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
