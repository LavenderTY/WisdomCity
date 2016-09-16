package com.ty.wisdomcity.base.impl;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ty.wisdomcity.activity.MainActivity;
import com.ty.wisdomcity.base.BaseMenuDetailPager;
import com.ty.wisdomcity.base.BasePager;
import com.ty.wisdomcity.base.impl.menu.InteractMenuDetailPager;
import com.ty.wisdomcity.base.impl.menu.NewsMenuDetailPager;
import com.ty.wisdomcity.base.impl.menu.PhotosMenuDetailPager;
import com.ty.wisdomcity.base.impl.menu.TopicMenuDetailPager;
import com.ty.wisdomcity.domain.NewsMenu;
import com.ty.wisdomcity.fragment.LeftMenuFragment;
import com.ty.wisdomcity.global.GlobalContants;
import com.ty.wisdomcity.utils.CacheUtils;
import com.ty.wisdomcity.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Lavender on 2016/8/25.
 */
public class NewsCenterPager extends BasePager {
    private NewsMenu mMenuData;  //分类信息网络数据
    private ArrayList<BaseMenuDetailPager> mMenuDetailList;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //显示菜单按钮
        iv_sliding_menu.setVisibility(View.VISIBLE);

        //判断是否有缓存(读缓存)
        //这里判断缓存的目的是为了当网络阻塞时，能显示缓存的数据
        String isTrue = CacheUtils.getCache(GlobalContants.CATEGORY_URL, mActivity);
        if (!TextUtils.isEmpty(isTrue)) {
            //有缓存
            processData(isTrue);
        }

        //请求服务器，获取数据
        getDataFromService();
    }

    /**
     * 从服务器获取数据
     */
    private void getDataFromService() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalContants.CATEGORY_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求成功
                String result = responseInfo.result;
                //解析数据
                processData(result);

                //写缓存
                CacheUtils.setCache(GlobalContants.CATEGORY_URL, result, mActivity);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                ToastUtils.show(mActivity, s);
            }
        });
    }

    /**
     * 解析数据
     */
    private void processData(String json) {
        Gson gson = new Gson();
        mMenuData = gson.fromJson(json, NewsMenu.class);

        //获取侧边栏对象
        MainActivity mUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mUI.getLeftMenuFragment();
        //给侧边栏设置数据
        fragment.setMenuData(mMenuData.data);

        //初始化四个菜单
        mMenuDetailList = new ArrayList<>();
        mMenuDetailList.add(new NewsMenuDetailPager(mActivity, mMenuData.data.get(0).children));
        mMenuDetailList.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailList.add(new PhotosMenuDetailPager(mActivity,iv_sliding_grid));
        mMenuDetailList.add(new InteractMenuDetailPager(mActivity));

        //将新闻详情页设置为默认界面
        setCurrentDetailPager(0);
    }

    /**
     * 设置菜单详情页
     *
     * @param position
     */
    public void setCurrentDetailPager(int position) {
        //重新给FrameLayout添加内容
        BaseMenuDetailPager pager = mMenuDetailList.get(position);   //获取当前应该显示的界面
        View view = pager.mView;  //当前页面的布局
        //清除旧的布局
        fl_content_message.removeAllViews();
        fl_content_message.addView(view);  //给帧布局添加View

        //初始化数据
        pager.initData();

        //设置标题栏
        tv_content_title.setText(mMenuData.data.get(position).title);

        //如果是组图页面，需要切换显示按钮
        if(pager instanceof  PhotosMenuDetailPager){
            //显示按钮
            iv_sliding_grid.setVisibility(View.VISIBLE);
        }else{
            //隐藏按钮
            iv_sliding_grid.setVisibility(View.GONE);
        }
    }
}
