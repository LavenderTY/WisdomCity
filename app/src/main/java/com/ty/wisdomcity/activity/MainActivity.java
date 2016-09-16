package com.ty.wisdomcity.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.fragment.ContentFragment;
import com.ty.wisdomcity.fragment.LeftMenuFragment;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends SlidingFragmentActivity {
    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
    private static final String TAG_CONTENT = "TAG_CONTENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置左侧边栏
        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏触摸

        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset(width * 200 / 320);//屏幕预留200像素宽度
        initFragment();
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        //开始事物
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);
        // 用fragment替换帧布局;参1:帧布局容器的id;参2:是要替换的fragment;参3:标记
        ft.replace(R.id.fl_main, new ContentFragment(), TAG_CONTENT);
        //提交事物
        ft.commit();
    }

    /**
     * 通过Tag得到LeftMenuFragment对象
     *
     * @return 返回LeftMenuFragment对象
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getFragmentManager();
        //通过Tag获得LeftMenuFragment对象
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
        return fragment;
    }

    /**
     * 通过Tag得到LeftMenuFragment对象
     *
     * @return 返回LeftMenuFragment对象
     */
    public ContentFragment getContentFragment() {
        FragmentManager fm = getFragmentManager();
        //通过Tag获得LeftMenuFragment对象
        ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
        return fragment;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
