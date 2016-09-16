package com.ty.wisdomcity.global;

/**
 * Created by Lavender on 2016/8/25.
 */
public class GlobalContants {
    /**
     * 服务器主域名
     */
    public static final String SERVICE_URL = "http://192.168.56.1:8080/zhbj";
    /**
     * 分类信息域名
     */
    public static final String CATEGORY_URL = SERVICE_URL + "/categories.json";
    /**
     * 组图信息接口
     */
    public static final String PHOTOS_URL = SERVICE_URL + "/photos/photos_1.json";
    /**
     * 是否是第一次进入应用的key的值
     */
    public static final String IS_FIRST_ENTER = "is_first_enter";
    /**
     * 表示是否已读的key的值
     */
    public static final String IS_READ_IDS = "is_read_ids";
    /**
     * 记录当前id的key的值
     */
    public static final String CURRENT_ID = "current_id";
}
