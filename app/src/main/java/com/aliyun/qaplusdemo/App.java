package com.aliyun.qaplusdemo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aliyun.qaplusdemo.utils.SpHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    private static final String TAG = "App";

    private static final String SP_KEY_HOST = "host";
    private static final String SP_KEY_APPKEY = "appkey";
    private static final String SP_KEY_CHANNEL = "channel";

    private static final String SP_FILE_NAME = "aplus_config";

    @Override
    public void onCreate() {
        super.onCreate();
       // 定制App版本号
        UMConfigure.setAppVersion("9.0", 9);

        String host = Constant.DEFAULT_HOST;
        String appkey = Constant.DEFAULT_APPKEY;
        String channel = Constant.DEFAULT_CHANNEL;

        String hostInSp = SpHelper.get(this, SP_FILE_NAME, SP_KEY_HOST);
        String appkeyInSp = SpHelper.get(this, SP_FILE_NAME, SP_KEY_APPKEY);
        String channelInSp = SpHelper.get(this, SP_FILE_NAME, SP_KEY_CHANNEL);

        if (!TextUtils.isEmpty(hostInSp)) {
            host = hostInSp;
        }
        if (!TextUtils.isEmpty(appkeyInSp)) {
            appkey = appkeyInSp;
        }
        if (!TextUtils.isEmpty(channelInSp)) {
            channel = channelInSp;
        }

        //设置收数地址
        UMConfigure.setCustomDomain(host, null);
        // 打开SDK调试日志
        UMConfigure.setLogEnabled(true);

        // 预初始化
        UMConfigure.preInit(this, appkey, channel);

        MobclickAgent.setAutoEventEnabled(true);
        MobclickAgent.enableFragmentPageCollection(true);
        
	// AUTO模式下，且不希望type为0的页面路径信息干扰，才需要调用此函数
        //MobclickAgent.disableActivityPageCollection();

        //UMConfigure.setProcessEvent(true); //放开此行注释，即支持子进程事件统计。

        final Context appContext = this.getApplicationContext();
        final String useAppkey = appkey;
        final String useChannel = channel;
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "--->>> 延迟5秒调用初始化接口：UMConfigure.init() ");
                UMConfigure.init(appContext, useAppkey, useChannel, UMConfigure.DEVICE_TYPE_PHONE, null);
            }
        }, 5, TimeUnit.SECONDS);
    }

    
}
