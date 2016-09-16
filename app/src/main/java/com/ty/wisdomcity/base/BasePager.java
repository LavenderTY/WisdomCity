package com.ty.wisdomcity.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.activity.MainActivity;

/**
 * Created by Lavender on 2016/8/25.
 */
public class BasePager {
    public Activity mActivity;
    public View mView;
    public TextView tv_content_title;
    public ImageButton iv_sliding_grid;
    public ImageView iv_sliding_menu;
    public FrameLayout fl_content_message;  //空的帧布局，需要填充

    public BasePager(Activity activity) {
        mActivity = activity;
        mView = initView();
    }

    /**
     * 初始化View
     *
     * @return
     */
    public View initView() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        tv_content_title = (TextView) view.findViewById(R.id.tv_content_title);
        iv_sliding_menu = (ImageView) view.findViewById(R.id.iv_sliding_menu);
        fl_content_message = (FrameLayout) view.findViewById(R.id.fl_content_message);
        iv_sliding_grid = (ImageButton) view.findViewById(R.id.iv_sliding_grid);

        iv_sliding_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSliding();
            }
        });
        return view;
    }

    /**
     * 打开或者关闭侧滑栏
     */
    protected void showSliding() {
        MainActivity mUI = (MainActivity) mActivity;
        SlidingMenu menu = mUI.getSlidingMenu();
        menu.toggle();  //如果状态是开，调用后就是关
    }

    /**
     * 初始化数据
     */
    public void initData() {

    }
}
