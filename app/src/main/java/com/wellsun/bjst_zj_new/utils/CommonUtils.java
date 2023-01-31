package com.wellsun.bjst_zj_new.utils;

/**
 * date     : 2022-08-17
 * author   : ZhaoZheng
 * describe :
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class CommonUtils {

    /**
     * 获取版本号
     *
     * @return
     */

    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //判断唯一地址 mac
    public static String getMacAddress() {
        try {
            // 把当前机器上访问网络的接口存入 List集合中
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!"wlan0".equalsIgnoreCase(nif.getName())) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null || macBytes.length == 0) {
                    continue;
                }
                StringBuilder result = new StringBuilder();
                for (byte b : macBytes) {
                    //每隔两个字符加一个:
                    result.append(String.format("%02X:", b));
                }
                if (result.length() > 0) {
                    //删除最后一个:
                    result.deleteCharAt(result.length() - 1);
                }

//                return result.toString();
                return result.toString().replace(":","");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //判断是否有网络
    public static  boolean getNetWorkStart(Context context){
        // String str = "00:db:3d:c3:18:8b";
        //将获取到的acitvity中的服务给连接管理器
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //调用服务管理类中的网络环境
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //判断其中的值是否为空和是否连接网络
        if(activeNetworkInfo!=null && activeNetworkInfo.isConnected()  ){
            //连接成功
            return true;
        }else{
            //网络无连接
            return false;
        }
    }


}
