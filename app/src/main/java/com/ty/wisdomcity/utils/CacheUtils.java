package com.ty.wisdomcity.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 * Created by Lavender on 2016/8/25.
 */
public class CacheUtils {
    /**
     * 以url为key,json为值，设置缓存
     *
     * @param url     key
     * @param json    值
     * @param context 上下文对象
     */
    public static void setCache(String url, String json, Context context) {
        //也可以用文件缓存：以url为文件名，以json为文件内容
        SpUtils.setString(context, url, json);
    }

    /**
     * 获得缓存中的数据
     *
     * @param url     key
     * @param context 上下文对象
     * @return 返回缓存的值
     */
    public static String getCache(String url, Context context) {
        //文件缓存：查找有没有一个名字为url的文件，若有，则说明有缓存
        return SpUtils.getString(context, url, null);
    }
}
