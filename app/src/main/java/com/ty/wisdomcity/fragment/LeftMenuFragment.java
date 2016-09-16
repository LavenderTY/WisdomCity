package com.ty.wisdomcity.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.activity.MainActivity;
import com.ty.wisdomcity.base.BasePager;
import com.ty.wisdomcity.base.impl.NewsCenterPager;
import com.ty.wisdomcity.domain.NewsMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧边栏的Fragment
 * Created by Lavender on 2016/8/24.
 */
public class LeftMenuFragment extends BaseFragment {
    private ListView lv_left_fragment;

    private ArrayList<NewsMenu.NewsMenuData> mNewsMenuList;// 侧边栏网络数据对象
    private int mCurrentItem;  //被选中的item的位置
    private LeftMenuAdapter mAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        lv_left_fragment = (ListView) view.findViewById(R.id.lv_left_fragment);
        return view;
    }

    @Override
    public void initData() {

    }

    /**
     * 侧边栏设置数据
     */
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        //当前选中的位置归0
        mCurrentItem = 0;
        mNewsMenuList = data;
        mAdapter = new LeftMenuAdapter();
        lv_left_fragment.setAdapter(mAdapter);

        lv_left_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //改变当前点击的位置
                mCurrentItem = i;
                //刷新ListView
                mAdapter.notifyDataSetChanged();

                //设置侧滑栏的状态(显示与弹回的状态)
                showSliding();
                //侧滑栏点击后，要修改新闻中心的FragmentLayout中的内容
                setCurrentDetailPager(i);
            }
        });
    }

    /**
     * 设置当前菜单详情页
     *
     * @param position
     */
    private void setCurrentDetailPager(int position) {
        //获取新闻中心的对象
        MainActivity mUI = (MainActivity) mActivity;
        //获取ContentFragment
        ContentFragment contentFragment = mUI.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();

        //修改新闻中心的FrameLayout的布局
        newsCenterPager.setCurrentDetailPager(position);
    }

    /**
     * 打开或者关闭侧滑栏
     */
    protected void showSliding() {
        MainActivity mUI = (MainActivity) mActivity;
        SlidingMenu menu = mUI.getSlidingMenu();
        menu.toggle();  //如果状态是开，调用后就是关
    }

    class LeftMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNewsMenuList.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int i) {
            return mNewsMenuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(mActivity, R.layout.list_item_menu, null);
            TextView tv_menu_title = (TextView) v.findViewById(R.id.tv_menu_title);
            NewsMenu.NewsMenuData data = getItem(i);
            tv_menu_title.setText(data.title);
            if (mCurrentItem == i) {
                //被选中
                tv_menu_title.setEnabled(true);
            } else {
                tv_menu_title.setEnabled(false);
            }
            return v;
        }
    }
}
