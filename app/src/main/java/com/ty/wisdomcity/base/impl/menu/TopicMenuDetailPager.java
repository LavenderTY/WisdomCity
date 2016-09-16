package com.ty.wisdomcity.base.impl.menu;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ty.wisdomcity.base.BaseMenuDetailPager;

/**
 * 菜单相详情页 - 专题
 * Created by Lavender on 2016/8/25.
 */
public class TopicMenuDetailPager extends BaseMenuDetailPager {
    public TopicMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        TextView textView = new TextView(mActivity);
        textView.setText("菜单相详情页 - 专题");
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}