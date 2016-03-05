package quejianming.com.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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

public class RegisterActivity extends ActionBarActivity {

    EditText et1;
    EditText et2;
    EditText et3;
    UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bmob.initialize(this, "0157a0303696698edec21e03c0c37b4f");

        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);


        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册
                if(TextUtils.isEmpty(et1.getText().toString())){
                    toast("请输入账号");
                }
                else if((TextUtils.isEmpty(et2.getText().toString())) || (TextUtils.isEmpty(et3.getText().toString()))){
                    toast("密码不能为空");
                }
                else if(!et2.getText().toString().equals(et3.getText().toString())){
                    toast("两次输入的密码不一致，请认真检查后再次输入");
                }
                else {
                    MyUser bu = new MyUser();
                    bu.setUsername(et1.getText().toString());
                    bu.setPassword(et2.getText().toString());
                    bu.signUp(RegisterActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toast("注册成功");
                            App.isLogin = true;
                            App.username = et1.getText().toString();
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            App.username = et1.getText().toString();

                            BmobQuery<MyInstallation> query1 = new BmobQuery<MyInstallation>();
                            query1.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(RegisterActivity.this));
                            query1.findObjects(RegisterActivity.this, new FindListener<MyInstallation>() {
                                @Override
                                public void onSuccess(List<MyInstallation> list) {
                                    if (list != null) {
                                        MyInstallation mib = list.get(0);
                                        mib.setUid(BmobUser.getCurrentUser(RegisterActivity.this).getObjectId());
                                        mib.update(RegisterActivity.this, new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.i("bmob", "设备更新成功");
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Log.i("bmob", "设备更新失败");
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {
                                    toast(s);
                                }
                            });
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast("注册失败" + s);
                        }
                    });
                }
            }
        });
    }

    public void toast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}
