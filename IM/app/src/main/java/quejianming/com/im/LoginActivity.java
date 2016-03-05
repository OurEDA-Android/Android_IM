package quejianming.com.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    EditText et1;
    EditText et2;
    UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bmob.initialize(this, App.getApplicationId());
        et1 = (EditText)findViewById(R.id.et1);
        et2 = (EditText)findViewById(R.id.et2);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.tvToregister).setOnClickListener(this);
        System.out.println(App.getIsLogin());
    }
    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogin:
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
                    }
                });
                break;
            case R.id.tvToregister:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
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
                    if (!App.user2.contains(list.get(i).user2)) {
                        App.datas_List.add(list.get(i));
                        App.user2.add(list.get(i).user2);
                    }
                }
                MainActivity.adapter.notifyDataSetChanged();
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
                    mib.update(LoginActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("bmob","设备更新成功");
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
