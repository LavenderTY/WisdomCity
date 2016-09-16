package com.ty.wisdomcity.domain;

import java.util.ArrayList;

/**
 * 页签详情数据
 * Created by Lavender on 2016/8/26.
 */
public class NewsTabBean {
    public NewsTab data;

    public class NewsTab {
        public String more;
        public ArrayList<NewsData> news;
        public ArrayList<TopNews> topnews;
    }

    /**
     * 新闻列表对象
     */
    public class NewsData {
        public int id;   //id
        public String listimage;   //新闻图片的连接
        public String pubdate;     //新闻日期
        public String title;
        public String type;
        public String url;
    }

    /**
     * 头条新闻
     */
    public class TopNews {
        public int id;   //id
        public String topimage;   //头条新闻图片的连接
        public String pubdata;     //头条新闻日期
        public String title;
        public String type;
        public String url;
    }
}
