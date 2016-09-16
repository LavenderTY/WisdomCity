package com.ty.wisdomcity.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.activity.MainActivity;
import com.ty.wisdomcity.base.BasePager;
import com.ty.wisdomcity.base.impl.GovaffairsPager;
import com.ty.wisdomcity.base.impl.HomePager;
import com.ty.wisdomcity.base.impl.NewsCenterPager;
import com.ty.wisdomcity.base.impl.SettingPager;
import com.ty.wisdomcity.base.impl.WisdomServicePager;
import com.ty.wisdomcity.ui.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lavender on 2016/8/24.
 */
public class ContentFragment extends BaseFragment {
    private NoScrollViewPager mNvp_scroll;
    private List<BasePager> mListPager;
    private RadioGroup rg_bottom_title;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mNvp_scroll = (NoScrollViewPager) view.findViewById(R.id.nvp_scroll);
        rg_bottom_title = (RadioGroup) view.findViewById(R.id.rg_bottom_title);
        return view;
    }

    @Override
    public void initData() {
        //创建一个BasePager的集合
        mListPager = new ArrayList<BasePager>();
        //向集合中添加子pager对象
        mListPager.add(new HomePager(mActivity));
        mListPager.add(new NewsCenterPager(mActivity));
        mListPager.add(new WisdomServicePager(mActivity));
        mListPager.add(new GovaffairsPager(mActivity));
        mListPager.add(new SettingPager(mActivity));

        //给NoScrollViewPager设置Adapter
        mNvp_scroll.setAdapter(new MyPagerAdapter());
        //给RadioGroup设置点击改变监听事件
        rg_bottom_title.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rad_home:
                        //第二个参数表示是否具有滑动动画
                        mNvp_scroll.setCurrentItem(0, false);
                        break;
                    case R.id.rad_news_center:
                        mNvp_scroll.setCurrentItem(1, false);
                        break;
                    case R.id.rad_wisdom_service:
                        mNvp_scroll.setCurrentItem(2, false);
                        break;
                    case R.id.rad_govaffairs:
                        mNvp_scroll.setCurrentItem(3, false);
                        break;
                    case R.id.rad_setting:
                        mNvp_scroll.setCurrentItem(4, false);
                        break;
                }
            }
        });

        //给NoScrollViewPager设置页面改变监听
        mNvp_scroll.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当页面被选中时，初始化数据，这样做是为了节约流量和提高性能
                BasePager pager = mListPager.get(position);
                pager.initData();

                //第一个和最后一个界面禁用侧边栏
                if (position == 0 || position == mListPager.size() - 1) {
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

        //手动初始化第一个界面的数据
        mListPager.get(0).initData();
        //首页禁用侧边栏
        setSlidingMenuEnable(false);
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

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mListPager.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取pager对象
            BasePager pager = mListPager.get(position);
            View view = pager.mView;   //获取当前页面对象的布局
            //调用pager的initData()方法，初始化数据，ViewPager默认会加载下一个页面，
            //为了节省流量和保证性能，不要在此处调用initData()方法
            //pager.initData();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 获取新闻中心的页面
     */
    public NewsCenterPager getNewsCenterPager() {
        NewsCenterPager newPager = (NewsCenterPager) mListPager.get(1);
        return newPager;
    }
}
