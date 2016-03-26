package com.example.qjm3662.bmobpushtext;

import cn.bmob.v3.BmobObject;

/**
 * Created by qjm3662 on 2016/3/12 0012.
 */
public class Friend extends BmobObject {
    String user1;
    String user2;
    String changeId;
    String person_note;
    String user2_id;
    Integer flag;

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {

        this.changeId = changeId;
    }

    public String getPerson_note() {
        return person_note;
    }

    public void setPerson_note(String person_note) {
        this.person_note = person_note;
    }

    public String getUser2_id() {
        return user2_id;
    }

    public void setUser2_id(String user2_id) {
        this.user2_id = user2_id;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
