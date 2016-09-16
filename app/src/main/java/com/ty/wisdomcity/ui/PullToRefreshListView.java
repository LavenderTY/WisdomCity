package com.ty.wisdomcity.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ty.wisdomcity.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 下拉刷新的ListView
 * Created by Lavender on 2016/8/29.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private static final int STATE_PULL_TO_REFRESH = 1;  //下拉刷新
    private static final int STATE_RELEASE_TO_REFRESH = 2; //释放刷新
    private static final int STATE_REFRESHING = 3;  //正在刷新
    private int mCurrentSate = STATE_PULL_TO_REFRESH;  //当前状态
    private View mHeaderView;
    private int mHeight;
    private int startY;
    private ImageView iv_refresh_pic;
    private ProgressBar pb_loading;
    private TextView tv_refresh_text;
    private TextView tv_refresh_time;
    private ProgressBar pb_loading_foot;
    private TextView tv_refresh_foot;
    private RotateAnimation mRotateUP;
    private RotateAnimation mRotateDown;
    //3、定义成员变量，接收监听对象
    private OnRefreshListener mListener;
    private View mFootView;
    private boolean isLoading;  //用于判断是否在加载
    private int mHeightFoot;

    public PullToRefreshListView(Context context) {
        this(context, null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListView();
        initFootView();
    }

    private void initListView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        iv_refresh_pic = (ImageView) mHeaderView.findViewById(R.id.iv_refresh_pic);
        pb_loading = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);
        tv_refresh_text = (TextView) mHeaderView.findViewById(R.id.tv_refresh_text);
        tv_refresh_time = (TextView) mHeaderView.findViewById(R.id.tv_refresh_time);
        this.addHeaderView(mHeaderView);
        //测量
        mHeaderView.measure(0, 0);
        //得到mHeaderView的高度
        mHeight = mHeaderView.getMeasuredHeight();
        //设置mHeaderView的位置
        mHeaderView.setPadding(0, -mHeight, 0, 0);

        //初始化动画
        initAnimation();
        setCurrentTime();
    }

    /**
     * 初始化脚布局
     */
    private void initFootView() {
        mFootView = View.inflate(getContext(), R.layout.pull_to_refresh_foot, null);
        pb_loading_foot = (ProgressBar) mFootView.findViewById(R.id.pb_loading_foot);
        tv_refresh_foot = (TextView) mFootView.findViewById(R.id.tv_refresh_foot);
        this.addFooterView(mFootView);
        //测量
        mFootView.measure(0, 0);
        //得到mFootView的高度
        mHeightFoot = mFootView.getMeasuredHeight();
        //设置mFootView的位置
        mFootView.setPadding(0, -mHeightFoot, 0, 0);

        //给listView设置滑动监听
        this.setOnScrollListener(this);
    }

    private void initAnimation() {
        mRotateUP = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUP.setDuration(200);
        mRotateUP.setFillAfter(true);
        mRotateDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDown.setDuration(200);
        mRotateDown.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //如果点击的是ViewPager的话，onTouchEvent事件就会被消费掉，所以需要重新获得值
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                //如果是正在刷新，跳出循环
                if (mCurrentSate == STATE_REFRESHING) {
                    break;
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;
                //得到第一个可见的位置
                int firstVisiblePosition = getFirstVisiblePosition();

                //下拉,当前的位置是第一个可见的位置时，下拉刷新
                if (dy > 0 && firstVisiblePosition == 0) {
                    //计算当前下拉控件可见的高度
                    int padding = dy - mHeight;
                    mHeaderView.setPadding(0, padding, 0, 0);
                    if (padding > 0 && mCurrentSate != STATE_RELEASE_TO_REFRESH) {
                        //改为释放刷新
                        mCurrentSate = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrentSate != STATE_PULL_TO_REFRESH) {
                        //改为下拉刷新
                        mCurrentSate = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //松手时，将开始的Y方向上的值重置位默认值，这样下次保证下次刷新的初始值
                startY = -1;
                if (mCurrentSate == STATE_RELEASE_TO_REFRESH) {
                    mCurrentSate = STATE_REFRESHING;
                    refreshState();
                    //完整展示头布局
                    mHeaderView.setPadding(0, 0, 0, 0);

                    //4、进行回调
                    if (mListener != null) {
                        mListener.OnRefresh();
                    }
                } else if (mCurrentSate == STATE_PULL_TO_REFRESH) {
                    //隐藏头布局
                    mHeaderView.setPadding(0, -mHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据当前静态刷新界面
     */
    private void refreshState() {
        switch (mCurrentSate) {
            case STATE_PULL_TO_REFRESH:
                tv_refresh_text.setText("下拉刷新");
                pb_loading.setVisibility(INVISIBLE);
                iv_refresh_pic.setVisibility(VISIBLE);
                iv_refresh_pic.startAnimation(mRotateDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tv_refresh_text.setText("释放刷新");
                pb_loading.setVisibility(INVISIBLE);
                iv_refresh_pic.setVisibility(VISIBLE);
                iv_refresh_pic.startAnimation(mRotateUP);
                break;
            case STATE_REFRESHING:
                tv_refresh_text.setText("正在刷新...");
                //清除箭头动画，否则无法隐藏
                iv_refresh_pic.clearAnimation();
                pb_loading.setVisibility(VISIBLE);
                iv_refresh_pic.setVisibility(INVISIBLE);
                break;
        }
    }

    /**
     * 2、暴露接口，设置监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }

    //滑动状态发生改变时回调
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == SCROLL_STATE_IDLE) {  //空闲状态
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == getCount() - 1 && !isLoading) {
                //当前状态是最后一个，且没有加
                isLoading = true;
                mFootView.setPadding(0, 0, 0, 0);  //显示加载更多
                //将ListView显示在最后一个item上，这样不用手滑就可以显示出来了
                setSelection(getCount() - 1);
                //通知主界面，加载下一页
                if (mListener != null) {
                    mListener.OnLoadMore();
                }
            }
        }
    }

    //活动过程中回调
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    /**
     * 1、下拉刷新的回调接口
     */
    public interface OnRefreshListener {
        void OnRefresh();

        void OnLoadMore();
    }

    /**
     * 刷新结束，收起控件
     */
    public void onRefreshComplete(boolean success) {
        if (!isLoading) {
            //隐藏头布局
            mHeaderView.setPadding(0, -mHeight, 0, 0);
            mCurrentSate = STATE_PULL_TO_REFRESH;
            tv_refresh_text.setText("下拉刷新");
            pb_loading.setVisibility(INVISIBLE);
            iv_refresh_pic.setVisibility(VISIBLE);
            if (success) {
                setCurrentTime();
            }
        } else {
            //加载更多
            mFootView.setPadding(0, -mHeightFoot, 0, 0);  //隐藏加载更多
            isLoading = false;
        }
    }

    //设置刷新时间
    private void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        tv_refresh_time.setText(time);
    }
}
