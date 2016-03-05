package quejianming.com.im;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/2/13 0013.
 */
public class Message extends BmobObject {
    String username;
    String contain;
    String target;
    public String getContain() {
        return contain;
    }

    public String getUsername() {
        return username;
    }

    public void setContain(String contain) {
        this.contain = contain;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
