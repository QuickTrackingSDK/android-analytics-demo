package com.aliyun.qaplusdemo;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;

public class ContentActivity extends Activity {

    private static final String TAG = "Content";
    private static final String PAGE_NAME = "PageAct001";

    private Context AppContext;
    private Button back;
    private Button back2Main;
    private Button back2Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        AppContext = this.getApplicationContext();

        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = "GoBack"; // 自定义事件id
                MobclickAgent.onEvent(AppContext, eventId);
                finish();
            }
        });

        back2Main = (Button) findViewById(R.id.btn_to_main);
        back2Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = "GoPageHome"; // 自定义事件id
                MobclickAgent.onEvent(AppContext, eventId);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        back2Search = (Button) findViewById(R.id.btn_to_search);
        back2Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = "GoPageSearch"; // 自定义事件id
                MobclickAgent.onEvent(AppContext, eventId);
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PAGE_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(PAGE_NAME);
    }
}
