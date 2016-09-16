package com.ty.wisdomcity.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 头条新闻自定义ViewPager
 * Created by Lavender on 2016/8/26.
 */
public class TopNewsViewPager extends ViewPager {

    private int mStartX;
    private int mStartY;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 1.上下滑动,需要拦截
     * 2.向右滑动并且当前是第一个页面，需要拦截
     * 3.向左滑动并且当前是最后一个界面，需要拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //不拦截，这句话的作用就是保证后面的语句可以执行
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) ev.getX();
                mStartY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();

                int dx = endX - mStartX;
                int dy = endY - mStartY;
                if (Math.abs(dy) < Math.abs(dx)) {
                    int currentItem = getCurrentItem();
                    //左右滑
                    if (dx > 0) {
                        //向右滑
                        if (currentItem == 0) {
                            //第一个界面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        //向左滑
                        int count = getAdapter().getCount();  //item总数
                        if (currentItem == count - 1) {
                            //最后一个界面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    //上下滑动，需要拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
