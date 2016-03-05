package quejianming.com.im;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class AddFriend extends ActionBarActivity {

    EditText et_addFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        et_addFriend = (EditText)this.findViewById(R.id.et_addFriend);
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
                    query.findObjects(AddFriend.this, new FindListener<MyUser>() {
                        @Override
                        public void onSuccess(List<MyUser> list) {
                            if(list.size() == 0){
                                toast("用户名不存在，请重新输入");
                            }
                            else{
                                BmobQuery<Friend> query1 = new BmobQuery<Friend>();
                                query1.addWhereEqualTo("user1",App.getUsername());
                                query1.addWhereEqualTo("user2",et_addFriend.getText().toString());
                                query1.findObjects(AddFriend.this, new FindListener<Friend>() {
                                    @Override
                                    public void onSuccess(List<Friend> list) {
                                        if(list.size() == 0){
                                            System.out.println("Success");
                                            final ChangeNumber cn = new ChangeNumber();
                                            cn.setChange(0);
                                            cn.save(AddFriend.this, new SaveListener() {
                                                @Override
                                                public void onSuccess() {
                                                    final Friend f = new Friend();
                                                    f.setUser1(App.getUsername());
                                                    f.setUser2(et_addFriend.getText().toString());
                                                    f.setChangeId(cn.getObjectId());
                                                    f.save(AddFriend.this, new SaveListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Friend friend = new Friend();
                                                            friend.setUser2(et_addFriend.getText().toString());
                                                            App.datas_List.add(friend);
                                                            App.user2.add(friend.user2);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            System.out.println("Fail");
                                                            toast("Add fail:" + s);
                                                        }
                                                    });
                                                    final Friend f1 = new Friend();
                                                    f1.setUser1(et_addFriend.getText().toString());
                                                    f1.setUser2(App.getUsername());
                                                    f1.setChangeId(cn.getObjectId());
                                                    f1.save(AddFriend.this, new SaveListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            System.out.println("Fail");
                                                            toast("Add fail:" + s);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {

                                                }
                                            });
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
