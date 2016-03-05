package quejianming.com.im;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class MyUser extends BmobUser {
    private String name;
    private Boolean sex;
    private Integer age;
    private String changeid;


    public String getChangeid() {
        return changeid;
    }

    public void setChangeid(String changeid) {
        this.changeid = changeid;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public Boolean getSex() {
        return sex;
    }
}
