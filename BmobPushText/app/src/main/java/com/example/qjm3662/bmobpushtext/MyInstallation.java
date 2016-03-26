package com.example.qjm3662.bmobpushtext;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by qjm3662 on 2016/3/12 0012.
 */
public class MyInstallation extends BmobInstallation {
    /**
     * 用户id-这样可以将设备与用户之间进行绑定
     */
    public MyInstallation(Context context) {
        super(context);
    }
    private String uid;
    private String username;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}