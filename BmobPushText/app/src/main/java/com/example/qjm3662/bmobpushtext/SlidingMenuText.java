package com.example.qjm3662.bmobpushtext;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SlidingMenuText extends AppCompatActivity {

    private SlidingMenu slidingMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu_text);

        slidingMenu = new SlidingMenu(this);
        //将SlidingMenu附加到当前Activity
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);
        //设置SlidingMenu的模式(设置SlidingMenu呈现在左边 )
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //指定菜单资源
        slidingMenu.setMenu(R.layout.sliding_menu);
        //设置菜单拖出来的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_dimen);
    }
}
