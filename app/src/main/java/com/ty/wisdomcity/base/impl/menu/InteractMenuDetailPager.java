package com.ty.wisdomcity.base.impl.menu;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ty.wisdomcity.base.BaseMenuDetailPager;

/**
 * 菜单详情 - 互动
 * Created by Lavender on 2016/8/25.
 */
public class InteractMenuDetailPager extends BaseMenuDetailPager{

    public InteractMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        TextView textView = new TextView(mActivity);
        textView.setText("菜单详情 - 互动");
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
