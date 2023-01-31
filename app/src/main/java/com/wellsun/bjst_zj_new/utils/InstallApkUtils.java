package com.wellsun.bjst_zj_new.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * date     : 2022-10-18
 * author   : ZhaoZheng
 * describe :
 */
public class InstallApkUtils {
    /**
     * 安装应用
     */
    /**
     * 安装apk
     */
    public static void installApk(Activity mContent, String path) {
        File apkfile = new File(path);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //兼容android7.0以上版本
        Uri uri = Uri.fromFile(apkfile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            uri = FileProvider.getUriForFile(mContent, "com.wellsun.bjst_gateticket.fileprovider", apkfile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContent.startActivity(intent);
    }

}