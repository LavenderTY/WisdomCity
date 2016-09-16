package com.ty.wisdomcity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lavender on 2016/8/24.
 */
public class SpUtils {
    /**
     * 获取SharedPreferences中boolean类型的值
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param devalue 默认值
     * @return 返回存储的boolean值
     */
    public static boolean getBoolean(Context context, String key, boolean devalue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key, devalue);
    }

    /**
     * 设置boolean类型的值到SharedPreferences中
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param value   存储的值
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 获取SharedPreferences中String类型的值
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param devalue 默认值
     * @return 返回存储的String值
     */
    public static String getString(Context context, String key, String devalue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, devalue);
    }

    /**
     * 设置String类型的值到SharedPreferences中
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param value   存储的值
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取SharedPreferences中int类型的值
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param devalue 默认值
     * @return 返回存储的int值
     */
    public static int getInt(Context context, String key, int devalue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key, devalue);
    }

    /**
     * 设置int类型的值到SharedPreferences中
     *
     * @param context 上下文对象
     * @param key     存储的key的值
     * @param value   存储的值
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }
}
