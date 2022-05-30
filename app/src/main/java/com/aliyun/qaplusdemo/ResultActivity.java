package com.aliyun.qaplusdemo;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.spm.SpmAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ResultActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "Result";

    private static final String PAGE_NAME = "PageSearch";

    private Context AppContext;
    private Button search;
    private EditText searchValue;
    private Handler H;

    private final String[] globalPostfix = {"结果1","结果2","结果3","结果4","结果5"};

    private String targetValue = "";

    private static final int MSG_LIST_EXPOSURE = 0x1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        AppContext = this.getApplicationContext();
        Intent intent = getIntent();
        String searchTaget = intent.getStringExtra("searchTarget");
        targetValue = searchTaget;
        search = (Button) findViewById(R.id.btn_search2);
        searchValue = (EditText) findViewById(R.id.et_search_value2);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputValue = searchValue.getText().toString();
                if (!TextUtils.isEmpty(inputValue)) {
                    targetValue = inputValue;
                    hideSoftInput();
                    Map<String, Object> params = new HashMap<>();
                    String eventId = "SearchBtn"; // 自定义事件id
                    params.put("keyword", inputValue); // 事件参数
                    MobclickAgent.onEventObject(AppContext, eventId, params);
                    Log.i(TAG, "input search target: " + inputValue);
                    initListView(inputValue);

                }

            }
        });

        if (H == null) {
            H = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_LIST_EXPOSURE: {
                            Log.i(TAG, "列表曝光事件触发!");

                            JSONArray array = new JSONArray();
                            String index = "";
                            String keyword = "";
                            for (int i = 0; i < 5; i++) {
                                index = String.valueOf(i+1);
                                keyword = targetValue + globalPostfix[i];
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.put("keyword", keyword);
                                    obj.put("index", index);
                                    array.put(obj);
                                } catch (Throwable e) {

                                }
                            }
                            if (array.length() > 0) {
                                Map<String, Object> params = new HashMap<>();
                                String eventId = "ShowSearchItem"; // 自定义事件id
                                String arg = array.toString();
                                params.put("itemlist", arg); // 事件参数
                                MobclickAgent.onEventObject(AppContext, eventId, params);
                            }
                        }break;
                        default: {

                        }break;
                    }
                }
            };
        }

        if (!TextUtils.isEmpty(targetValue)) {
            initListView(targetValue);
        }
    }

    private void initListView(String prefix) {

        ListView listView = (ListView)findViewById(R.id.resultList);

        String[] data = {"结果1","结果2","结果3","结果4","结果5"};
        for (int i = 0; i < data.length; i ++) {
            data[i] = "<font color=\"#FF0000\">" + prefix + "</font>" + data[i];
        }
        MyArrayAdapter arrayAdapter = new MyArrayAdapter(this,android.R.layout.simple_expandable_list_item_1, data);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "Item " + position + " clicked");
        Adapter adpter=parent.getAdapter();
        String item = (String)adpter.getItem(position);
        Log.i(TAG, "item is: " + item);

        String refIndex = String.valueOf(position+1);
        String refKeyword = targetValue + globalPostfix[position];
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ref_index", refIndex);
        params.put("ref_keyword", refKeyword);
        SpmAgent.updateNextPageProperties(params);
        // 跳转到活动内容页
        Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
        startActivity(intent);

    }

    private void hideSoftInput(){
        if (searchValue != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(searchValue.getWindowToken(), 0);
        }
    }

    class MyArrayAdapter extends ArrayAdapter {
        private String[] data;
        private Context mContext;
        private LayoutInflater inflater;

        @SuppressWarnings("unchecked")
        public MyArrayAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
            super(context, resource, objects);
            mContext = context;
            data = (String[])objects;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int maxDataPosition = 4;
            View view = convertView;
            if(view == null) {
                View newView = inflater.inflate(R.layout.adapter_item, parent, false);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 150);

                view = newView.findViewById(R.id.array_adapter_text);

                view.setLayoutParams(layoutParams);
            }
            //((TextView)view).setText(data[position]);
            ((TextView)view).setText(Html.fromHtml(data[position]));

            if (position == maxDataPosition) {
                if (H != null) {
                    Message m = H.obtainMessage();
                    m.what = MSG_LIST_EXPOSURE;
                    H.sendMessage(m);
                }
            }
            return view;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onPageStart(PAGE_NAME);
        // 测试指定绑定页面名的不带参数自定义事件API，如果最后一个参数传入非空字符串，此事件的page_name字段值
        // 就是传入的第三个参数值，反之，第三个参数传入null或者空字符串""，就是当前页面名"PageSearch"。
        MobclickAgent.onEvent(this, "BindingEkv1", "BindingPage1");
        //MobclickAgent.onEvent(this, "BindingEkv1", (String)null); // 最后一个参数为null
        // MobclickAgent.onEvent(this, "BindingEkv1", ""); // 最后一个参数为空字符串

        // 测试指定绑定页面名的带参数自定义事件API，如果最后一个参数传入非空字符串，此事件的page_name字段值
        // 就是传入的第三个参数值，反之，第三个参数传入null或者空字符串""，就是当前页面名"PageSearch"。
        Map<String, Object> args = new HashMap<>();
        args.put("arg1", "value1");
        MobclickAgent.onEventObject(this, "BindingEkv2", args, "BindingPage2");
        //MobclickAgent.onEventObject(this, "BindingEkv2", args, null); // 最后一个参数为null
        //MobclickAgent.onEventObject(this, "BindingEkv2", args, ""); // 最后一个参数为空字符串
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(PAGE_NAME);
    }
}
