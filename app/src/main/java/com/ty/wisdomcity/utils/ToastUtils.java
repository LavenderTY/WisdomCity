package com.ty.wisdomcity.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lavender on 2016/8/25.
 */
public class ToastUtils {
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
