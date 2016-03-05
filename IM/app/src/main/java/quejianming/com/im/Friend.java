package quejianming.com.im;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/2/6 0006.
 */
public class Friend extends BmobObject {
    String user1;
    String user2;
    String changeId;

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
}
