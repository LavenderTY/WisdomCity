package com.ty.wisdomcity.base.impl.menu;

import android.app.Activity;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ty.wisdomcity.R;
import com.ty.wisdomcity.base.BaseMenuDetailPager;
import com.ty.wisdomcity.domain.NewsTabBean;
import com.ty.wisdomcity.domain.PhotosDetail;
import com.ty.wisdomcity.global.GlobalContants;
import com.ty.wisdomcity.utils.CacheUtils;
import com.ty.wisdomcity.utils.MyBitmapUtils;
import com.ty.wisdomcity.utils.ToastUtils;

import java.util.ArrayList;

/**
 * 菜单详情 - 组图
 * Created by Lavender on 2016/8/25.
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {
    private ListView mLvPhotos;
    private GridView mGvPhotos;
    private ArrayList<PhotosDetail.PhotosNews> mNewsList;
    private ImageButton mButPhoto;
    private boolean isListView = true;  //用于标记是否是ListView

    public PhotosMenuDetailPager(Activity activity, ImageButton iv_sliding_grid) {
        super(activity);
        this.mButPhoto = iv_sliding_grid;
        iv_sliding_grid.setOnClickListener(this);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        mLvPhotos = (ListView) view.findViewById(R.id.lv_photos);
        mGvPhotos = (GridView) view.findViewById(R.id.gv_photos);

        return view;
    }

    @Override
    public void initData() {
        //判断是否有缓存(读缓存)
        //这里判断缓存的目的是为了当网络阻塞时，能显示缓存的数据
        String isTrue = CacheUtils.getCache(GlobalContants.PHOTOS_URL, mActivity);
        if (!TextUtils.isEmpty(isTrue)) {
            //有缓存
            processData(isTrue);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalContants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求成功
                String result = responseInfo.result;
                //解析数据
                processData(result);
                //写缓存
                CacheUtils.setCache(GlobalContants.PHOTOS_URL, result, mActivity);
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
     * 解析Json数据
     *
     * @param result
     */
    private void processData(String result) {
        Gson gson = new Gson();
        PhotosDetail photoDetail = gson.fromJson(result, PhotosDetail.class);
        mNewsList = photoDetail.data.news;

        mLvPhotos.setAdapter(new PhotoAdapter());
        mGvPhotos.setAdapter(new PhotoAdapter());
    }

    @Override
    public void onClick(View view) {
        //如果是ListView,则切换至GridView
        if (isListView) {
            mLvPhotos.setVisibility(View.GONE);
            mGvPhotos.setVisibility(View.VISIBLE);
            mButPhoto.setImageResource(R.mipmap.icon_pic_list_type);
            isListView = false;
        } else {
            //切换至ListView
            mLvPhotos.setVisibility(View.VISIBLE);
            mGvPhotos.setVisibility(View.GONE);
            mButPhoto.setImageResource(R.mipmap.icon_pic_grid_type);
            isListView = true;
        }
    }

    class PhotoAdapter extends BaseAdapter {

        private final MyBitmapUtils bitmapUtils;

        public PhotoAdapter() {
            //bitmapUtils = new BitmapUtils(mActivity);
            //bitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
            bitmapUtils = new MyBitmapUtils();
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosDetail.PhotosNews getItem(int i) {
            return mNewsList.get(i);
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
                v = View.inflate(mActivity, R.layout.list_item_photos, null);
                mViewHolder = new ViewHolder();
                mViewHolder.iv_photo_pic = (ImageView) v.findViewById(R.id.iv_photo_pic);
                mViewHolder.tv_photo_title = (TextView) v.findViewById(R.id.tv_photo_title);
                v.setTag(mViewHolder);
            } else {
                v = view;
                mViewHolder = (ViewHolder) v.getTag();
            }

            bitmapUtils.display(mViewHolder.iv_photo_pic, getItem(i).listimage);
            mViewHolder.tv_photo_title.setText(getItem(i).title);
            return v;
        }
    }

    class ViewHolder {
        private ImageView iv_photo_pic;
        private TextView tv_photo_title;
    }
}
