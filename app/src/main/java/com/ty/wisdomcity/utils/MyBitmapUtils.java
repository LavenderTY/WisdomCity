package com.ty.wisdomcity.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ty.wisdomcity.R;

/**
 * 自定义三级缓存图片加载工具
 * Created by Lavender on 2016/8/30.
 */
public class MyBitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private LocationCacheUtils mLocationCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils() {
        mLocationCacheUtils = new LocationCacheUtils();
        mMemoryCacheUtils = new MemoryCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocationCacheUtils, mMemoryCacheUtils);
    }

    public void display(ImageView imageView, String url) {
        //设置默认图片
        imageView.setImageResource(R.drawable.pic_item_list_default);

        //优先从内存加载图片
        Bitmap bitMap = mMemoryCacheUtils.getMemoryCache(url);
        if (bitMap != null) {
            imageView.setImageBitmap(bitMap);
            System.out.println("从内存加载图片");
        }
        //从本地加载图片
        Bitmap bitmap = mLocationCacheUtils.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从本地加载图片");
            //写内存缓存
            mMemoryCacheUtils.setMemoryCache(url, bitmap);
        } else {
            //加载图片顺序：内存缓存 -- 本地缓存(SD卡) -- 网络缓存
            mNetCacheUtils.getBitmapFromNet(imageView, url);
        }

    }
}
