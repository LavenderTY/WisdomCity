package com.ty.wisdomcity.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by Lavender on 2016/8/25.
 */
public abstract class BaseMenuDetailPager {
    public Activity mActivity;
    public View mView;
    public BaseMenuDetailPager(Activity activity) {
        mActivity = activity;
        mView = initView();
    }

    /**
     * 初始化View
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化数据
     */
    public void initData() {

    }
}
