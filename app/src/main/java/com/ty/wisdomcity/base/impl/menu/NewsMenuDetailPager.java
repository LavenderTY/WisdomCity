package com.ty.wisdomcity.base.impl.menu;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ViewUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.activity.MainActivity;
import com.ty.wisdomcity.base.BaseMenuDetailPager;
import com.ty.wisdomcity.domain.NewsMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * 菜单详情 - 新闻
 * ViewPagerIndicator使用流程
 * 1、引入库
 * 2、解决v4包冲突
 * 3、从例子程序中拷贝布局文件
 * 4、从例子文件中拷贝相关代码（指示器和ViewPager绑定；重写getPageTitle返回标题）
 * 5、在清单文件中增加样式
 * 6、背景改为白色
 * 7、修改样式-背景样式&文字样式
 * Created by Lavender on 2016/8/25.
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {
    private ViewPager vp_news_menu_detail;
    private TabPageIndicator tp_Indicator;
    private ImageButton iv_but_next;
    private ArrayList<NewsMenu.NewsTabData> mNewTabData;  //页签网络数据
    private ArrayList<TabDetailPager> mTabDetailList;    //页签页面集合

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> childen) {
        super(activity);
        mNewTabData = childen;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        vp_news_menu_detail = (ViewPager) view.findViewById(R.id.vp_news_menu_detail);
        tp_Indicator = (TabPageIndicator) view.findViewById(R.id.tp_Indicator);
        iv_but_next = (ImageButton) view.findViewById(R.id.iv_but_next);
        //给ImageButton设置点击事件
        iv_but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳到下个页面
                int currentItem = vp_news_menu_detail.getCurrentItem();
                currentItem++;
                vp_news_menu_detail.setCurrentItem(currentItem);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        //初始化页签
        mTabDetailList = new ArrayList<>();
        for (int i = 0; i < mNewTabData.size(); i++) {
            TabDetailPager pager = new TabDetailPager(mActivity, mNewTabData.get(i));
            mTabDetailList.add(pager);
        }
        vp_news_menu_detail.setAdapter(new NewsMenuDetailAdapter());
        //将ViewPager和指示器绑定到一起，但是必须要vp_news_menu_detail设置完Adapter后绑定
        tp_Indicator.setViewPager(vp_news_menu_detail);

        //设置页面滑动监听
        //这里只能给tp_Indicator设置监听，而不能给vp_news_menu_detail设置
        tp_Indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //第一个和最后一个界面禁用侧边栏
                if (position != 0) {
                    //禁用侧边栏
                    setSlidingMenuEnable(false);
                } else {
                    //启用侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setSlidingMenuEnable(boolean enable) {
        //得到侧边栏
        MainActivity mUI = (MainActivity) mActivity;
        SlidingMenu menu = mUI.getSlidingMenu();
        if (enable) {
            //启用侧边栏
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏触摸
        } else {
            //禁用侧边栏
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);  //禁用侧边栏
        }
    }

    class NewsMenuDetailAdapter extends PagerAdapter {
        //指定指示器的标题

        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData tabData = mNewTabData.get(position);
            return tabData.title;
        }

        @Override
        public int getCount() {
            return mTabDetailList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mTabDetailList.get(position);
            View view = pager.mView;
            container.addView(view);
            pager.initData();
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
