package com.aliyun.qaplusdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.spm.SpmAgent;

// X5Webview容器Activity
public class X5WebviewAnalytics extends Activity {
    private static final String TAG = "X5WebviewAnalytics";

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x5webview);
        MobclickAgent.skipMe(this, TAG); //WebView宿主Activity不需要统计native成页面PV事件
        WebView webView = findViewById(R.id.webview);
        mWebView = webView;

        // 如下设置参考X5Webview腾讯官方Demo配置，具体设置请开发者依照自己App要求配置
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // 绑定WebView控件到SPM插件SDK，此WebView内访问H5页面中的aplus_cloud.js SDK可以和
        // SPM插件SDK配合，实现H5页面埋点统计数据统一通过Native统计SDK发送。
        SpmAgent.attachX5(mWebView);

        webView.loadUrl("file:///android_asset/index.html?aplusDebug=true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        android.util.Log.i("mob", "--Webview-onPause-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        android.util.Log.i("mob", "--Webview-onResume-----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpmAgent.detach(); // 在WebView宿主Activity销毁时解除绑定
    }
}
