package com.example.qjm3662.bmobpushtext;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    android.support.design.widget.TextInputEditText et1;
    android.support.design.widget.TextInputEditText et2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1 = (TextInputEditText) findViewById(R.id.et_name);
        et2 = (TextInputEditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_logup).setOnClickListener(this);
        System.out.println(App.getIsLogin());
    }

    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_login:
                final MyUser bu = new MyUser();
                bu.setUsername(et1.getText().toString());
                bu.setPassword(et2.getText().toString());
                App.username = et1.getText().toString();
                bu.login(v.getContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast("登陆成功");
                        updateUserInfo();
                        App.isLogin = true;
                        App.username = et1.getText().toString();
                        System.out.println("Login    "+App.getUsername());
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        System.out.println(App.getIsLogin());
                        startActivity(intent);
                        System.out.println(App.getIsLogin());
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("登陆失败" + s);
                        System.out.println(s);
                    }
                });
                break;
            case R.id.btn_logup:
                Intent intent = new Intent();
                intent.setClass(this, Log_up.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    private void updateUserInfo() {
        BmobQuery<Friend> query = new BmobQuery<Friend>();
        query.addWhereEqualTo("user1", App.getUsername());
        query.findObjects(LoginActivity.this, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                System.out.println("success");
                for (int i = 0; i < list.size(); i++) {
                    if (!App.user2name_list.contains(list.get(i).getUser2())) {
                        App.friends_List.get(0).add(list.get(i));
                        App.user2name_list.add(list.get(i).getUser2());
                        App.list_message_remind.get(0).add(0);
                        List<Message> ls = new ArrayList<Message>();
                        App.list_message.add(ls);
                    }
                }
              //  MainActivity.adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                System.out.println(s);
            }
        });

        BmobQuery<MyInstallation> query1 = new BmobQuery<MyInstallation>();
        query1.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query1.findObjects(this, new FindListener<MyInstallation>() {
            @Override
            public void onSuccess(List<MyInstallation> list) {
                if(list !=null){
                    MyInstallation mib = list.get(0);
                    mib.setUid(BmobUser.getCurrentUser(LoginActivity.this).getObjectId());
                    mib.setUsername(et1.getText().toString());
                    mib.update(LoginActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("bmob", "设备更新成功");
                            BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();
                            query.addWhereEqualTo("uid", BmobUser.getCurrentUser(LoginActivity.this).getObjectId());
                            query.addWhereNotEqualTo("installationId",BmobInstallation.getCurrentInstallation(LoginActivity.this).getInstallationId());
                            query.findObjects(LoginActivity.this, new FindListener<MyInstallation>() {
                                @Override
                                public void onSuccess(List<MyInstallation> list) {
                                    MyInstallation installation;
                                    for (int i = 0; i < list.size(); i++) {
                                        installation = new MyInstallation(LoginActivity.this);
                                        installation.setObjectId(list.get(i).getObjectId());
                                        installation.delete(LoginActivity.this, new DeleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                // TODO Auto-generated method stub
                                                Log.i("bmob", "删除成功");
                                            }

                                            @Override
                                            public void onFailure(int code, String msg) {
                                                // TODO Auto-generated method stub
                                                Log.i("bmob", "删除失败：" + msg);
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
                        public void onFailure(int i, String s) {
                            Log.i("bmob","设备更新失败");
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}