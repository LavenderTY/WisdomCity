package com.ty.wisdomcity.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ty.wisdomcity.base.BasePager;

/**
 * Created by Lavender on 2016/8/25.
 */
public class WisdomServicePager extends BasePager {
    public WisdomServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        TextView textView = new TextView(mActivity);
        textView.setText("智慧服务");
        textView.setTextSize(23);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        //帧布局添加布局
        fl_content_message.addView(textView);

        //设置标题栏
        tv_content_title.setText("智慧服务");
        //显示菜单按钮
        iv_sliding_menu.setVisibility(View.VISIBLE);
    }
}
