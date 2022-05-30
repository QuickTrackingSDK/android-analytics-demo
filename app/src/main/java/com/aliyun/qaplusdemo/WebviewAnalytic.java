package com.aliyun.qaplusdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.spm.SpmAgent;

public class WebviewAnalytic extends Activity {
    private static final String TAG = "WebviewAnalytic";

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        MobclickAgent.skipMe(this, TAG); //WebView宿主Activity不需要统计native成页面PV事件
        WebView webView = findViewById(R.id.webview);
        mWebView = webView;
        // 绑定WebView控件到SPM插件SDK，此WebView内访问H5页面中的aplus_cloud.js SDK可以和
        // SPM插件SDK配合，实现H5页面埋点统计数据统一通过Native统计SDK发送。
        SpmAgent.attach(mWebView);

        webView.loadUrl("file:///android_asset/index.html");

        //webView.loadUrl("http://g.assets.daily.taobao.net/nel/spm-cctv/1.0.12/worldCup.html");
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
