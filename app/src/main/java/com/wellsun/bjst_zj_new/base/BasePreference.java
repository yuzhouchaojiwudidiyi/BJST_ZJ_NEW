package com.wellsun.bjst_zj_new.base;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Author: zz
 * @Description:
 * @CreateDate: 2020/6/28 0028$
 */
public class BasePreference {
    public SharedPreferences sp;
    private String FILE_NAME = "userinfo";

    protected BasePreference() {
        sp = ((Context) App.appContext).getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    protected void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    protected String getString(String key) {
        return sp.getString(key, "");
    }

    protected void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    protected boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    protected void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    protected int getInt(String key) {
        return sp.getInt(key, 0);
    }
}
