package com.example.qjm3662.bmobpushtext;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/9 0009.
 */
public class ViewHolder {
    private ImageView img;
    private TextView tv_name;
    private TextView tv_note;
    private ImageView img_message_remind;

    public ImageView getImg() {
        return img;
    }

    public TextView getTv_name() {
        return tv_name;
    }


    public void setImg(ImageView img) {
        this.img = img;
    }

    public void setTv_name(TextView tv_name) {
        this.tv_name = tv_name;
    }

    public TextView getTv_note() {
        return tv_note;
    }

    public void setTv_note(TextView tv_note) {
        this.tv_note = tv_note;
    }

    public ImageView getImg_message_remind() {
        return img_message_remind;
    }

    public void setImg_message_remind(ImageView img_message_remind) {
        this.img_message_remind = img_message_remind;
    }
}
