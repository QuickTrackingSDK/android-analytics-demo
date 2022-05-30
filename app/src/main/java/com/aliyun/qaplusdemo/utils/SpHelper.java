package com.aliyun.qaplusdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpHelper {

    public static String get(Context context, String spFileName, String key) {
        if (context == null) {
            return "";
        }
        Context appContext = context.getApplicationContext();
        SharedPreferences sp = appContext.getSharedPreferences(spFileName, context.MODE_PRIVATE);
        if (sp != null) {
            return sp.getString(key, "");
        }
        return "";
    }

    public static void put(Context context, String spFileName, String key, String value) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        SharedPreferences sp = appContext.getSharedPreferences(spFileName, context.MODE_PRIVATE);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.apply();
            }
        }
    }
}
