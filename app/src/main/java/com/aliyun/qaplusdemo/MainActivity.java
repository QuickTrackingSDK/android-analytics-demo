package com.aliyun.qaplusdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.qaplusdemo.utils.SpHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.donkingliang.banner.CustomBanner;
import com.umeng.analytics.MobclickAgent;
import com.umeng.spm.SpmAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final String TAG = "Main";
    private static final String PAGE_NAME = "PageHome";

    private Context AppContext = null;
    private CustomBanner<String> banner;

    private static Object lock = new Object();
    private Map<String, ImageInfo> imageLoadState = new HashMap<>();

    private ArrayList<String> images = new ArrayList<>();

    private Handler H = null;

    private EditText inputUser = null;
    private Button userLogin = null;
    private Button userLogout = null;
    private TextView currentUser = null;

    private EditText searchValue = null;
    private Button search = null;

    private EditText globalArgs = null;
    private EditText currentArgs = null;
    private Button setArgs = null;
    private Button clearArgs = null;

    private Button webView = null;
    private Button x5webView = null;

    private TextView currentAppkey = null;
    private EditText newAppkey = null;
    private Button saveAppkey = null;

    private TextView currentHost = null;
    private EditText newHost = null;
    private Button saveHost = null;

    private static boolean backClicked = false;

    private static final String SP_PERM_FILE = "permissions_require_flag";

    private static final String SP_CONF_FILE = "aplus_config";

    private boolean getRequireFlag () {
        String flag = SpHelper.get(AppContext, SP_PERM_FILE, "require_flag");
        if ("true".equalsIgnoreCase(flag)) {
            return true;
        } else {
            return false;
        }
    }

    private void setRequiredFlag(boolean flag) {
        if (flag) {
            SpHelper.put(AppContext, SP_PERM_FILE, "require_flag", "true");
        } else {
            SpHelper.put(AppContext, SP_PERM_FILE, "require_flag", "false");
        }
    }

    private String getHostFromConf() {
        return SpHelper.get(AppContext, SP_CONF_FILE, "host");
    }

    private void setHost2Conf(String host) {
        SpHelper.put(AppContext, SP_CONF_FILE, "host", host);
    }

    private String getAppkeyFromConf() {
        return SpHelper.get(AppContext, SP_CONF_FILE, "appkey");
    }

    private void setAppkey2Conf(String appkey) {
        SpHelper.put(AppContext, SP_CONF_FILE, "appkey", appkey);
    }

    private String getChannelFromConf() {
        return SpHelper.get(AppContext, SP_CONF_FILE, "channel");
    }

    private void setChannel2Conf(String channel) {
        SpHelper.put(AppContext, SP_CONF_FILE, "channel", channel);
    }

    private class ImageInfo {

        public String url;
        public Boolean exposed;
        public Boolean isReady;

        public ImageInfo(String url, Boolean isReady, Boolean exposed) {
            this.url = url;
            this.isReady = isReady;
            this.exposed = exposed;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppContext = this.getApplicationContext();

        if (!getRequireFlag()) {
            if (Build.VERSION.SDK_INT >= 23) {
                String[] mPermissionList = new String[]{
                        Manifest.permission.READ_PHONE_STATE};
                this.requestPermissions(mPermissionList, 123);
            }
        }

        if (H == null) {
            H = new Handler(getMainLooper());
        }

        banner = (CustomBanner) findViewById(R.id.banner);

        synchronized (lock) {
            if (images.size() > 0) {
                images.clear();
            }
            if (imageLoadState.size() > 0) {
                imageLoadState.clear();
            }

            images.add(Constant.IMAGE_1);
            images.add(Constant.IMAGE_2);
            images.add(Constant.IMAGE_3);


            imageLoadState.put(Constant.IMAGE_1, new ImageInfo(Constant.IMAGE_1, Boolean.FALSE, Boolean.FALSE));
            imageLoadState.put(Constant.IMAGE_2, new ImageInfo(Constant.IMAGE_2, Boolean.FALSE, Boolean.FALSE));
            imageLoadState.put(Constant.IMAGE_3, new ImageInfo(Constant.IMAGE_3, Boolean.FALSE, Boolean.FALSE));
        }
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                synchronized (lock) {
                    String imageUrl = images.get(i);
                    if (imageLoadState.containsKey(imageUrl)) {
                        ImageInfo info = imageLoadState.get(imageUrl);
                        if (!info.exposed && info.isReady) {
                            Log.i(TAG, "????????? ???" + i + "??? ???????????????");
                            Map<String, Object> params = new HashMap<>();
                            String eventId = "ShowBanner"; // ???????????????id
                            params.put("index", i); // ????????????
                            MobclickAgent.onEventObject(AppContext, eventId, params);
                            info.exposed = true;
                            imageLoadState.put(imageUrl, info);
                        } else {
                            // ????????????0???????????????????????????????????????????????????
                            // ????????????????????????ready???????????????1?????????????????????
                            if (i == 0) {
                                if (!info.isReady) {
                                    if (H != null) {
                                        H.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                synchronized (lock) {
                                                    String imageUrl = images.get(0);
                                                    ImageInfo info = imageLoadState.get(imageUrl);
                                                    if (info != null) {
                                                        if (info.isReady && !info.exposed) {
                                                            Log.i(TAG, "????????? ???0??? ???????????????");
                                                            Map<String, Object> params = new HashMap<>();
                                                            String eventId = "ShowBanner"; // ???????????????id
                                                            params.put("index", "0"); // ????????????
                                                            MobclickAgent.onEventObject(AppContext, eventId, params);
                                                            info.exposed = true;
                                                            imageLoadState.put(imageUrl, info);

                                                        }
                                                    }
                                                }
                                            }
                                        }, 1000);
                                    }
                                }
                            }
                            Log.i(TAG, "?????? ????????? ??? " + i + " ??????");
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        banner.setOnPageClickListener(new CustomBanner.OnPageClickListener() {
            @Override
            public void onPageClick(int position, Object o) {
                Log.i("YYY", "--->>> index = " + position);
                Log.i("YYY", "--->>> url = " + (String)o);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("ref_index", position);
                params.put("homepage_version", "1.0");
                SpmAgent.updateNextPageProperties(params);
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                startActivity(intent);

            }
        });
        setBean(images);

        // ????????????
        inputUser = (EditText) findViewById(R.id.et_user);
        currentUser = (TextView) findViewById(R.id.tv_id_value);
        userLogin = (Button) findViewById(R.id.btn_login);
        userLogout = (Button) findViewById(R.id.btn_logout);
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = inputUser.getText().toString();
                if (!TextUtils.isEmpty(user)) {
                    MobclickAgent.onProfileSignIn(user);
                    Map<String, Object> params = new HashMap<>();
                    String eventId = "UserLogin"; // ???????????????id
                    params.put("user_id", user); // ????????????
                    MobclickAgent.onEventObject(AppContext, eventId, params);
                    currentUser.setText(user);
                } else {
                    Toast.makeText(AppContext, "??????ID?????????????????????",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onProfileSignOff();
                currentUser.setText("");
            }
        });

        // ????????????
        searchValue = (EditText) findViewById(R.id.et_search_value);
        search = (Button) findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetValue = searchValue.getText().toString();
                if (!TextUtils.isEmpty(targetValue)) {
                    Map<String, Object> params = new HashMap<>();
                    String eventId = "SearchBtn"; // ???????????????id
                    params.put("keyword", targetValue); // ????????????
                    MobclickAgent.onEventObject(AppContext, eventId, params);

                    // ??????????????????
                    Map<String, Object> pageParams = new HashMap<String, Object>();
                    pageParams.put("ref_keyword", targetValue);
                    pageParams.put("homepage_version", "1.0");
                    SpmAgent.updateNextPageProperties(pageParams);
                    
                    Intent intent= new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("searchTarget", targetValue);
                    startActivity(intent);
                } else {
                    Toast.makeText(AppContext, "?????????????????????????????????",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        // ????????????
        globalArgs = (EditText) findViewById(R.id.et_args);
        currentArgs = (EditText) findViewById(R.id.et_args_value);
        setEditTextReadOnly(currentArgs);

        setArgs = (Button) findViewById(R.id.btn_set_args);
        setArgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = globalArgs.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(AppContext, "?????????????????????????????????",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String[] args = input.split(",");
                Map<String, Object> globalPP = new HashMap<>();
                for (String arg : args) {
                    if (!TextUtils.isEmpty(arg)) {
                        String[] array = arg.split("=");
                        if (array.length == 2) {
                            String key = array[0];
                            String value = array[1];
                            globalPP.put(key, value);
                        } else {
                            Toast.makeText(AppContext, "?????????????????????????????????",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AppContext, "?????????????????????????????????",
                                Toast.LENGTH_LONG).show();
                    }
                }
                MobclickAgent.onEvent(AppContext, "SetParam");
                MobclickAgent.registerGlobalProperties(AppContext, globalPP);
                String argsValue = MobclickAgent.getGlobalProperties(AppContext);
                currentArgs.setText(argsValue);
            }
        });

        clearArgs = (Button) findViewById(R.id.btn_clear_args);
        clearArgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.clearGlobalProperties(AppContext);
                currentArgs.setText("");
            }
        });

        webView = (Button)findViewById(R.id.btn_webview);
        webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> params = new HashMap<>();
                params.put("native_transfor_arg", "native_transfor_value");
                SpmAgent.updateNextPageProperties(params);
                Intent intent= new Intent(getApplicationContext(), WebviewAnalytic.class);
                startActivity(intent);
            }
        });

        x5webView = (Button)findViewById(R.id.btn_x5webview);
        x5webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> params = new HashMap<>();
                params.put("native_transfor_arg", "native_transfor_value");
                SpmAgent.updateNextPageProperties(params);
                Intent intent= new Intent(getApplicationContext(), X5WebviewAnalytics.class);
                startActivity(intent);
            }
        });

        // ??????appkey
        currentAppkey = (TextView)findViewById(R.id.tv_current_appkey);
        String appkeyInSp = getAppkeyFromConf();
        if (TextUtils.isEmpty(appkeyInSp)) {
            currentAppkey.setText(currentAppkey.getText() + Constant.DEFAULT_APPKEY);
        } else {
            currentAppkey.setText(currentAppkey.getText() + appkeyInSp);
        }

        // ????????????
        currentHost = (TextView)findViewById(R.id.tv_current_host);
        String hostInSp = getHostFromConf();
        if (TextUtils.isEmpty(hostInSp)) {
            currentHost.setText(currentHost.getText() + Constant.DEFAULT_HOST);
        } else {
            currentHost.setText(currentHost.getText() + hostInSp);
        }

        // ??????appkey
        saveAppkey = (Button)findViewById(R.id.btn_modify_appkey);
        newAppkey = (EditText)findViewById(R.id.et_new_appkey);
        saveAppkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appkey = newAppkey.getText().toString();
                if (!TextUtils.isEmpty(appkey)) {
                    SpHelper.put(AppContext, SP_CONF_FILE, "appkey", appkey);
                    Toast.makeText(AppContext, "appkey???????????????????????????????????????????????????????????????",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // ??????host
        saveHost = (Button)findViewById(R.id.btn_modify_host);
        newHost = (EditText)findViewById(R.id.et_new_host);
        saveHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String host = newHost.getText().toString();
                if (!TextUtils.isEmpty(host)) {
                    SpHelper.put(AppContext, SP_CONF_FILE, "host", host);
                    Toast.makeText(AppContext, "?????????????????????????????????????????????????????????????????????",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setEditTextReadOnly(EditText view){
        if (view instanceof android.widget.EditText){
            view.setCursorVisible(false);             //????????????????????????????????????
            view.setFocusable(false);                 //?????????
            view.setFocusableInTouchMode(false);      //???????????????????????????
        }
    }

    //?????????????????????
    private void setBean(final ArrayList<String> beans) {
        banner.setPages(new CustomBanner.ViewCreator<String>() {
            @Override
            public View createView(Context context, final int position) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                return imageView;
            }

            @Override
            public void updateUI(Context context, View view, int position, String entity) {

                //Log.i("YYY", "index: " + position);
                Glide.with(context)
                        .load(entity)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                if (!isFromMemoryCache) {
                                    synchronized (lock) {
                                        if (imageLoadState.containsKey(s)) {
                                            ImageInfo info = imageLoadState.get(s);
                                            if (!info.isReady) {
                                                info.isReady = true;
                                                imageLoadState.put(s, info);
                                            }
                                        }
                                    }
                                }
                                return false;
                            }
                        })
                        .into((ImageView) view);
            }
        }, beans).startTurning(5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PAGE_NAME);
        Map<String, Object> params = new HashMap<>();
        params.put("page_version", "1.0"); // ????????????????????????
        SpmAgent.setPageProperty(this, PAGE_NAME, params);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(PAGE_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setRequiredFlag(true);
    }

    private void back() {
        Timer timer = null;
        if (!backClicked) {
            backClicked = true;
            Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    backClicked = false;
                }
            }, 2000);//?????????2?????????????????????back?????????????????????
        } else {
            MobclickAgent.onKillProcess(AppContext);
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            back();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
