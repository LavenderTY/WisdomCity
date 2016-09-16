package com.ty.wisdomcity.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 本地缓存
 * Created by Lavender on 2016/8/30.
 */
public class LocationCacheUtils {
    private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ty_cache";

    //写本地缓存
    public void setLocalCache(String url, Bitmap bitmap) {
        File dir = new File(LOCAL_CACHE_PATH);
        //判断文件是否存在和是否是一个文件
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();// 创建文件夹
        }

        try {
            String fileName = MD5Encoder.encode(url);
            File cacheFile = new File(dir, fileName);
            //(图片格式，压缩比例0-100,输出流)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读本地缓存
     * 有缓存，返回bitmap，否则返回null
     */
    public Bitmap getLocalCache(String url) {
        try {
            File cacheFile = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));
            if (cacheFile.exists()) {
                //如果缓存存在
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
