package com.ty.wisdomcity.base.impl.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.activity.NewsDetailActivity;
import com.ty.wisdomcity.base.BaseMenuDetailPager;
import com.ty.wisdomcity.domain.NewsMenu;
import com.ty.wisdomcity.domain.NewsTabBean;
import com.ty.wisdomcity.global.GlobalContants;
import com.ty.wisdomcity.ui.PullToRefreshListView;
import com.ty.wisdomcity.ui.TopNewsViewPager;
import com.ty.wisdomcity.utils.CacheUtils;
import com.ty.wisdomcity.utils.SpUtils;
import com.ty.wisdomcity.utils.ToastUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * 页签详情页
 * Created by Lavender on 2016/8/26.
 */
public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData mTabData;   //单个页签的网络数据
    private TopNewsViewPager vp_tab_pager;
    private TextView tv_tab_title;
    private CirclePageIndicator cpi_circle;
    private PullToRefreshListView mTopListView;
    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private String mUrl;
    private ArrayList<NewsTabBean.NewsData> mNewData;
    private TopNewsAdapter mNewsAdapter;
    private String mMoreUrl;  //下一页的连接

    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalContants.SERVICE_URL + mTabData.url;
    }

    @Override
    public View initView() {
//        mTextView = new TextView(mActivity);
//        //textView.setText(mTabData.title);  这里会空指针
//        mTextView.setTextSize(25);
//        mTextView.setGravity(Gravity.CENTER);
        View view = View.inflate(mActivity, R.layout.pager_tab_data, null);
        mTopListView = (PullToRefreshListView) view.findViewById(R.id.lv_top_list);

        //改ListView添加头布局
        View viewHeader = View.inflate(mActivity, R.layout.list_item_header, null);
        vp_tab_pager = (TopNewsViewPager) viewHeader.findViewById(R.id.vp_tab_pager);
        tv_tab_title = (TextView) viewHeader.findViewById(R.id.tv_tab_title);
        cpi_circle = (CirclePageIndicator) viewHeader.findViewById(R.id.cpi_circle);
        mTopListView.addHeaderView(viewHeader);

        //5、设置回调
        mTopListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void OnRefresh() {
                //刷新数据
                getDataFromService();
            }

            @Override
            public void OnLoadMore() {
                //判断是否有下一页
                if (mMoreUrl != null) {
                    getMoreDataFromService();
                } else {
                    //没有下一页
                    ToastUtils.show(mActivity, "没有更多数据...");
                    //没有更多数据的时候也要收起加载更多
                    mTopListView.onRefreshComplete(true);
                }
            }
        });

        mTopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int headerCount = mTopListView.getHeaderViewsCount();  //获取头布局的数量
                i = i - headerCount;  //需要减去头布局的位置
                NewsTabBean.NewsData news = mNewData.get(i);
                String readIds = SpUtils.getString(mActivity, GlobalContants.IS_READ_IDS, "");
                //不包含当前Id的时候就添加
                if (!readIds.contains(String.valueOf(news.id))) {
                    readIds = readIds + news.id + ",";
                    SpUtils.setString(mActivity, GlobalContants.IS_READ_IDS, readIds);
                }
                //点击后，改变文字的颜色，局部刷新，这个view就是Adapter中返回的View
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_news_title);
                tvTitle.setTextColor(Color.GRAY);
                //mNewsAdapter.notifyDataSetChanged();  //全局刷新，性能不好
                //跳到新闻详情页面
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);
            }
        });
        return view;
    }

    /**
     * 请求服务器，获取更多数据
     */
    private void getMoreDataFromService() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求成功
                String result = responseInfo.result;
                //解析数据
                processData(result, true);
                //收起下拉刷新控件
                mTopListView.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                ToastUtils.show(mActivity, s);
                //收起下拉刷新控件
                mTopListView.onRefreshComplete(false);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
//        mTextView.setText(mTabData.title);
        //判断是否有缓存(读缓存)
        //这里判断缓存的目的是为了当网络阻塞时，能显示缓存的数据
        String isTrue = CacheUtils.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(isTrue)) {
            //有缓存
            processData(isTrue, false);
        }
        //请求服务器，获取数据
        getDataFromService();
    }

    /**
     * 请求服务器，获取数据
     */
    private void getDataFromService() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求成功
                String result = responseInfo.result;
                //解析数据
                processData(result, false);
                //写缓存
//                CacheUtils.setCache(mUrl, result, mActivity);
                //收起下拉刷新控件
                mTopListView.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                ToastUtils.show(mActivity, s);
                //收起下拉刷新控件
                mTopListView.onRefreshComplete(false);
            }
        });
    }

    /**
     * 解析Json数据
     *
     * @param result
     */
    private void processData(String result, boolean isMore) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);
        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalContants.SERVICE_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }
        if (!isMore) {
            //头条新闻
            mTopNews = newsTabBean.data.topnews;
            //设置适配器
            if (mTopNews != null) {
                vp_tab_pager.setAdapter(new TabPagerAdapter());
                cpi_circle.setViewPager(vp_tab_pager);
                cpi_circle.setSnap(true);

                //要给Indicator设置页面监听
                cpi_circle.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        //设置标题
                        tv_tab_title.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //第一个page的标题
                tv_tab_title.setText(mTopNews.get(0).title);
                //默认第一个选中(解决重新初始化时，Indicator仍保留上一次的位置的bug)
                cpi_circle.onPageSelected(0);
            }

            //列表新闻
            mNewData = newsTabBean.data.news;
            if (mNewData != null) {
                mNewsAdapter = new TopNewsAdapter();
                mTopListView.setAdapter(mNewsAdapter);
            }

            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = vp_tab_pager.getCurrentItem();
                        currentItem++;
                        if (currentItem > mTopNews.size() - 1) {
                            currentItem = 0;  //如果是最后一个页面，则跳到第一个页面
                        }
                        vp_tab_pager.setCurrentItem(currentItem);
                        //继续发送延时3秒的消息，形成内循环
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                    }
                };
                //保证启动自动轮播只执行一次
                mHandler.sendEmptyMessageDelayed(0, 3000);
                /*mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //在主线程中运行，这个方法就不用重写handleMessage方法了
                    }
                });*/
                vp_tab_pager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //停止广播自动轮播，删除消息
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                //取消事件，当按下ViewPager后，直接滑动ListView,导致抬起事件无响应，会走此事件
                                //保证启动自动轮播只执行一次
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            case MotionEvent.ACTION_UP:
                                //启动广告
                                //保证启动自动轮播只执行一次
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                        }
                        return false;
                    }
                });
            }
        } else {
            //加载更多
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewData.addAll(moreNews);  //将数据追加原来的集合中
            //刷新ListView
            mNewsAdapter.notifyDataSetChanged();
        }
    }

    class TopNewsAdapter extends BaseAdapter {
        private final BitmapUtils mBitmapUtils;

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            //设置加载中的默认图片
            mBitmapUtils.configDefaultLoadingImage(R.mipmap.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewData.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int i) {
            return mNewData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            ViewHolder mViewHolder = null;
            if (view == null) {
                mViewHolder = new ViewHolder();
                v = View.inflate(mActivity, R.layout.list_item_news, null);
                mViewHolder.iv_news = (ImageView) v.findViewById(R.id.iv_news);
                mViewHolder.tv_news_title = (TextView) v.findViewById(R.id.tv_news_title);
                mViewHolder.tv_news_time = (TextView) v.findViewById(R.id.tv_news_time);
                v.setTag(mViewHolder);
            } else {
                v = view;
                mViewHolder = (ViewHolder) v.getTag();
            }

            NewsTabBean.NewsData news = mNewData.get(i);
            mBitmapUtils.display(mViewHolder.iv_news, news.listimage);
            mViewHolder.tv_news_title.setText(news.title);
            mViewHolder.tv_news_time.setText(news.pubdate);

            //根据本地记录标记已读状态
            String readIds = SpUtils.getString(mActivity, GlobalContants.IS_READ_IDS, "");
            if (!readIds.contains(news.id + "")) {
                mViewHolder.tv_news_title.setTextColor(Color.BLACK);
            } else {
                mViewHolder.tv_news_title.setTextColor(Color.GRAY);
            }
            return v;
        }
    }

    class ViewHolder {
        private ImageView iv_news;
        private TextView tv_news_title;
        private TextView tv_news_time;
    }

    class TabPagerAdapter extends PagerAdapter {
        private final BitmapUtils mBitmapUtils;

        public TabPagerAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            //设置加载中的默认图片
            mBitmapUtils.configDefaultLoadingImage(R.mipmap.topnews_item_default);
        }

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            view.setScaleType(ImageView.ScaleType.FIT_XY);  //X与Y方向都匹配
            String imageUrl = mTopNews.get(position).topimage;
            //下载图片-->将图片设置给imageView-->避免内存溢出-->缓存
            mBitmapUtils.display(view, imageUrl);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
