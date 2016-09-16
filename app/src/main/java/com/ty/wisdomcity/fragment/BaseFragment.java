package com.ty.wisdomcity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基本Fragment，抽取的相同部分
 * Created by Lavender on 2016/8/24.
 */
public abstract class BaseFragment extends Fragment {
    //这个Activity就是MainActivity，因为这个Fragment是放在MainActivity中的
    public Activity mActivity;

    //Fragment 创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前Fragment所依赖的Activity
        mActivity = getActivity();
    }

    //初始化Fragment布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        return view;
    }

    //Fragment所依赖的Activity的onCreate方法执行结束
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData();
    }

    /**
     * 初始化View，子类必须实现
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化数据
     */
    public abstract void initData();
}
