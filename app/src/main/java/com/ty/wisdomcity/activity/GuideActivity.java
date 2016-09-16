package com.ty.wisdomcity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ty.wisdomcity.R;
import com.ty.wisdomcity.global.GlobalContants;
import com.ty.wisdomcity.utils.DensityUtils;
import com.ty.wisdomcity.utils.SpUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GuideActivity extends Activity {
    @InjectView(R.id.vp_guide)
    ViewPager vpGuide;
    @InjectView(R.id.but_start_play)
    Button butStartPlay;
    @InjectView(R.id.ll_container)
    LinearLayout llContainer;
    @InjectView(R.id.iv_red_point)
    ImageView ivRedPoint;

    //ImageView集合
    private List<ImageView> imageViewList;
    //图片id数组
    private int[] pidId = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};
    //小红点移动的距离
    private int mPointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        //初始化数据
        initData();
        //初始化按钮事件
        initBut();
        //设置Adapter
        vpGuide.setAdapter(new GuideAdapter());
        vpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当页面滑动时回调(滑动的时候才调用)
                //更新小红点的距离
                //计算小红点的左边距
                int leftMargin = (int) (mPointDis * positionOffset) + position * mPointDis;
                //得到布局参数
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                //设置小红点的左边距
                params.leftMargin = leftMargin;

                //重新设置布局参数
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中
                if (position == imageViewList.size() - 1) {
                    butStartPlay.setVisibility(View.VISIBLE);
                } else {
                    butStartPlay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //页面状态发生改变时调用
            }
        });

        //监听Layout方法结束的事件，位置去定好后再获取圆点间距
        //视图树
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除监听，避免重复回调
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                //layout方法执行结束时回调
                //计算两个圆点之间的距离
                //移动距离 = 第二个圆点left的值 - 第一个圆点left的值
                //measure --> layout(确定位置) --> draw(activity的onCreate执行结束后才走这个流程)
                //所以这里要监听Layout(因为位置确定了，就可以得到距离了)方法结束的事件，位置去定好后再获取圆点间距
                mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
            }
        });
    }

    /**
     * 初始化按钮事件
     */
    private void initBut() {
        butStartPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新sp, 已经不是第一次进入了
                SpUtils.setBoolean(getApplicationContext(), GlobalContants.IS_FIRST_ENTER, false);
                //跳到主页面
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        imageViewList = new ArrayList<>();
        for (int i = 0; i < pidId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(pidId[i]);
            imageViewList.add(imageView);

            //初始化小圆点
            ImageView imagePoint = new ImageView(this);
            imagePoint.setBackgroundResource(R.drawable.shape_point_gray);

            //初始化布局参数（父控件）
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //设置小圆点之间的间距
            if (i > 0) {
                //从第二个开始设置
                params.leftMargin = DensityUtils.dip2px(10, this);
            }
            //设置布局参数
            imagePoint.setLayoutParams(params);// 设置布局参数
            llContainer.addView(imagePoint);
        }
    }

    class GuideAdapter extends PagerAdapter {

        //条目总数
        @Override
        public int getCount() {
            return imageViewList.size();
        }

        //Object对象是否是View对象
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = imageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
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
