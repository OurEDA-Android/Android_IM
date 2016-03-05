package quejianming.com.im;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2016/2/13 0013.
 */
public class MyInstallation extends BmobInstallation {
    public MyInstallation(Context context) {
        super(context);
    }
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
