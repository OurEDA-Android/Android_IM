package com.example.qjm3662.bmobpushtext;

import android.media.Image;

import cn.bmob.v3.BmobUser;

/**
 * Created by qjm3662 on 2016/3/12 0012.
 */
public class MyUser extends BmobUser {
    private String name;
    private Boolean sex;
    private Integer age;
    private Image head_image;
    private String person_note;


    public Image getHead_image() {
        return head_image;
    }

    public void setHead_image(Image head_image) {
        this.head_image = head_image;
    }

    public String getPerson_note() {
        return person_note;
    }

    public void setPerson_note(String person_note) {
        this.person_note = person_note;
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
