package com.ty.wisdomcity.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存工具类
 * 泛型的意义：
 * 第一个泛型：doInBackground 里面的参数类型
 * 第二个泛型：onProgressUpdate 里面的参数类型
 * 第三个泛型：onPostExecute 里面的参数类型及 doInBackground 里面的返回类型
 * Created by Lavender on 2016/8/30.
 */
public class NetCacheUtils {

    private ImageView mImageView;
    private String mUrl;
    private LocationCacheUtils mLocationCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public NetCacheUtils(LocationCacheUtils locationCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocationCacheUtils = locationCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    public void getBitmapFromNet(ImageView imageView, String url) {
        //AsyncTask 异步封装工具，可以实现异步请求及主界面更新（对线程池 + handler的封装）
        //启动 AsyncTask
        new BitmapTask().execute(imageView, url);
    }

    class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {
        //1.预加载，运行在主线程
        @Override
        protected void onPreExecute() {
            //System.out.println("onPreExecute");
            super.onPreExecute();
        }

        //2.正在加载，运行在子线程（常用，这个方法必须实现）
        @Override
        protected Bitmap doInBackground(Object... voids) {
            //System.out.println("doInBackground");
            mImageView = (ImageView) voids[0];
            mUrl = (String) voids[1];

            mImageView.setTag(mUrl); //打标记，将当前imageView与url绑定在一起
            //开始下载图片
            Bitmap bitmap = download(mUrl);

            //调用此方法实现进度更新（会回调onProgressUpdate）
            //publishProgress(voids);
            return bitmap;
        }

        //3.更新进度的方法，运行在主线程
        @Override
        protected void onProgressUpdate(Integer... values) {
            //更新进度条
            //System.out.println("onProgressUpdate");
            super.onProgressUpdate(values);
        }

        //4.加载结束，运行在主线程（常用）
        @Override
        protected void onPostExecute(Bitmap aVoid) {
            //System.out.println("onPostExecute");
            if (aVoid != null) {
                //给mImageView设置图片
                //listView的重用机制可能导致imageView对象被多个item共用，从而将错误的图片设置给imageView
                //因此，这里需要检验，判断是否是正确的图片
                String url = (String) mImageView.getTag();
                if (url.equals(mUrl)) { //判断图示绑定的url师傅是当前bitmap的urll
                    mImageView.setImageBitmap(aVoid);
                    System.out.println("图片从网络下载");

                    //写本地缓存
                    mLocationCacheUtils.setLocalCache(url, aVoid);
                    //写内存缓存
                    mMemoryCacheUtils.setMemoryCache(url, aVoid);
                }
            }
            super.onPostExecute(aVoid);
        }
    }

    /**
     * 下载图片
     *
     * @param url 下载的url
     * @return 下载成功，返回bitMap，否则返回null
     */
    private Bitmap download(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(5000);  //读取超时
            conn.setConnectTimeout(5000);  //连接超时
            conn.setRequestMethod("GET");  //设置请求方式
            //连接
            conn.connect();
            int rCode = conn.getResponseCode();
            //请求成功
            if (rCode == 200) {
                //得到输入流
                InputStream inputStream = conn.getInputStream();
                //根据输入流，生成Bitmap对象
                Bitmap bitMap = BitmapFactory.decodeStream(inputStream);
                return bitMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
