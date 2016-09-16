package com.ty.wisdomcity.domain;

import java.util.ArrayList;

/**
 * Created by Lavender on 2016/8/30.
 */
public class PhotosDetail {
    public PhotosData data;

    public class PhotosData {
        public ArrayList<PhotosNews> news;
    }

    public class PhotosNews {
        public int id;
        public String listimage;
        public String title;
    }
}
