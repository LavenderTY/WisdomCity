package com.ty.wisdomcity.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ty.wisdomcity.R;
import com.ty.wisdomcity.global.GlobalContants;
import com.ty.wisdomcity.utils.SpUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * 新闻详情页面
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.tv_content_title)
    TextView tvContentTitle;
    @InjectView(R.id.iv_sliding_menu)
    ImageView ivSlidingMenu;
    @InjectView(R.id.iv_sliding_back)
    ImageButton ivSlidingBack;
    @InjectView(R.id.iv_text_size)
    ImageButton ivTextSize;
    @InjectView(R.id.iv_sliding_share)
    ImageButton ivSlidingShare;
    @InjectView(R.id.ll_control)
    LinearLayout llControl;
    @InjectView(R.id.wv_news_detail)
    WebView wvNewsDetail;
    @InjectView(R.id.pb_news_detail)
    ProgressBar pbNewsDetail;
    private String mUrl;
    private int mTempWitch;  //保存临时的选项id
    private int mCurrentWitch = 2;  //保存最终的选项id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.inject(this);
        llControl.setVisibility(View.VISIBLE);
        ivSlidingBack.setVisibility(View.VISIBLE);
        ivSlidingMenu.setVisibility(View.GONE);
        mUrl = getIntent().getStringExtra("url");

        ivSlidingBack.setOnClickListener(this);
        ivTextSize.setOnClickListener(this);
        ivSlidingShare.setOnClickListener(this);

        //初始化设置文字大小
        initTextSize();
        WebSettings settings = wvNewsDetail.getSettings();
        settings.setBuiltInZoomControls(true);  //显示缩放按钮
        settings.setUseWideViewPort(true);      //支持双击缩放
        settings.setJavaScriptEnabled(true);    //打开js的功能
        wvNewsDetail.loadUrl(mUrl);
        //从Sp中获取当前的item值
        mCurrentWitch = SpUtils.getInt(this, GlobalContants.CURRENT_ID, -1);

        //给wvNewsDetail设置监听
        wvNewsDetail.setWebViewClient(new WebViewClient() {
            //页面加载开始
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //System.out.println("页面加载开始");
                pbNewsDetail.setVisibility(View.VISIBLE);
            }

            //页面加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //System.out.println("页面加载结束");
                pbNewsDetail.setVisibility(View.INVISIBLE);
            }

            //所有链接跳转都会调用此方法

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //System.out.println("跳转链接：" + url);
                view.loadUrl(url);  //在跳转链接是，强制在该WebView中加载
                return true;
            }
        });
        //wvNewsDetail.goBack();  //跳转至上一页
        //wvNewsDetail.goForward();  //跳转至下一页

        wvNewsDetail.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //进度发生改变
                //System.out.println("进度：" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //网页标题
                //System.out.println("网页标题：" + title);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sliding_back:
                //回退按钮
                finish();
                break;
            case R.id.iv_text_size:
                //设置字体大小
                showTextSizeDialog();
                break;
            case R.id.iv_sliding_share:
                //分享
                showShare();
                break;
        }
    }

    /**
     * 设置字体大小
     */
    private void showTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体设置");
        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        //设置单选监听
        builder.setSingleChoiceItems(items, mCurrentWitch, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTempWitch = i;
            }
        });

        //设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //根据选中的字体来修改网页字体大小
                WebSettings settings = wvNewsDetail.getSettings();
                switch (mTempWitch) {
                    case 0:
                        //超大号字体
                        settings.setTextZoom(180);
                        break;
                    case 1:
                        //大号字体
                        settings.setTextZoom(150);
                        break;
                    case 2:
                        //正常字体
                        settings.setTextZoom(120);
                        break;
                    case 3:
                        //小号字体
                        settings.setTextZoom(90);
                        break;
                    case 4:
                        //超小号字体
                        settings.setTextZoom(60);
                        break;
                }
                mCurrentWitch = mTempWitch;
                SpUtils.setInt(getApplicationContext(), GlobalContants.CURRENT_ID, mCurrentWitch);
            }
        });

        //设置取消按钮
        builder.setNegativeButton("取消", null);
        //显示对话框
        builder.show();
    }

    //初始化设置字体大小
    private void initTextSize() {
        WebSettings settings = wvNewsDetail.getSettings();
        switch (mCurrentWitch) {
            case 0:
                //超大号字体
                settings.setTextZoom(180);
                break;
            case 1:
                //大号字体
                settings.setTextZoom(150);
                break;
            case 2:
                //正常字体
                settings.setTextZoom(120);
                break;
            case 3:
                //小号字体
                settings.setTextZoom(90);
                break;
            case 4:
                //超小号字体
                settings.setTextZoom(60);
                break;
        }
    }

    // 确保SDcard下面存在此张图片test.jpg
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.setTheme(OnekeyShareTheme.SKYBLUE); //修改主题样式

        oks.setTheme(OnekeyShareTheme.SKYBLUE);//修改主题样式

        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
