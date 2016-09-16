package com.ty.wisdomcity.utils;

import android.content.Context;

/**
 * Created by Lavender on 2016/8/31.
 */
public class DensityUtils {
    /**
     * 由 dip 转换为 px
     *
     * @param dip
     * @param context
     * @return
     */
    public static int dip2px(float dip, Context context) {
        //获取屏幕密度
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);  //加的0.5f是为了四舍五入
        return px;
    }

    /**
     * 由 px 转换为 dip
     *
     * @param px
     * @param context
     * @return
     */
    public static float px2dip(float px, Context context) {
        //获取屏幕密度
        float density = context.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }
}
