package com.wellsun.bjst_zj_new.utils;

import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * date     : 2022-08-12
 * author   : ZhaoZheng
 * describe :
 */
public class L {
    static boolean show = true;

    public static void v(String name, String result) {
        if (show) {
            Log.v(name, result);
        }
    }

}
