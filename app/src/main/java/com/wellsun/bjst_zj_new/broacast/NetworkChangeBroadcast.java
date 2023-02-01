package com.wellsun.bjst_zj_new.broacast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



import org.greenrobot.eventbus.EventBus;

/**
 * date     : 2022-08-09
 * author   : ZhaoZheng
 * describe :
 */
public class NetworkChangeBroadcast extends BroadcastReceiver {
    int network = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            if (network != 0) {
//                EventBus.getDefault().post(new ChangeStateBean(ReceiverTypeEm.showNetwork, true, "有网络"));  //网络连接
            }
        } else {
//            EventBus.getDefault().post(new ChangeStateBean(ReceiverTypeEm.showNetwork, false, "无网络"));      //网络断开
        }
        network = network + 1;
    }
}
