package com.wellsun.bjst_zj_new;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cczhr.TTS;
import com.cczhr.TTSConstants;
import com.decard.NDKMethod.BasicOper;
import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.base.BaseActivity;
import com.wellsun.bjst_zj_new.broacast.USBReceiver;
import com.wellsun.bjst_zj_new.data.StaticData;
import com.wellsun.bjst_zj_new.db.DbTestBean;
import com.wellsun.bjst_zj_new.em.VoiceTipCodeEm;
import com.wellsun.bjst_zj_new.readcard.Cmd;
import com.wellsun.bjst_zj_new.service.ReadCardService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    private android.widget.ImageView ivShow;
    private TextView tvLinenumber;
    private TextView tvStationname;
    private TextView tvDirection;
    private TextView tvUpload;
    private TextView tvNetwork;
    private TextView tvMode;
    private Handler handlerShowState;

    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        ivShow = (ImageView) findViewById(R.id.iv_show);
        tvLinenumber = (TextView) findViewById(R.id.tv_linenumber);
        tvStationname = (TextView) findViewById(R.id.tv_stationname);
        tvDirection = (TextView) findViewById(R.id.tv_direction);
        tvUpload = (TextView) findViewById(R.id.tv_upload);
        tvNetwork = (TextView) findViewById(R.id.tv_network);
        tvMode = (TextView) findViewById(R.id.tv_mode);
    }

    @Override
    public void setListener() {
        initUsbInsert();  //U盘插入监听

    }

    @Override
    public void initData() {
        handlerShowState = new Handler();
        Cmd.connectD8(this);                   //初始化读卡器
        if (!StaticData.readCardState) {               //读卡器连接异常
            ivShow.setImageResource(R.drawable.error_card_reader);
            return;
        } else if (!StaticData.samCardState) {         //sam卡异常
            ivShow.setImageResource(R.drawable.error_psam);
            return;
        } else if (StaticData.blackList == null) {      //黑名单异常
            ivShow.setImageResource(R.drawable.error_black_list);
            return;
        } else if (StaticData.distanceMap == null) {    //矩阵表异常
            ivShow.setImageResource(R.drawable.error_distance_noset);
            return;
        } else if (StaticData.mapPrice == null) {       //矩阵表异常
            ivShow.setImageResource(R.drawable.error_price_notset);
            return;
        }
        //开始读卡服务
        Intent mIntentReadCard = new Intent(MainActivity.this, ReadCardService.class);
        startService(mIntentReadCard);
    }

    @Override
    public void onClick(View view) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cardConsumeState(VoiceTipCodeEm em) {
        Log.v("记录指令",em.toString());
        handlerShowState.removeCallbacksAndMessages(null);
        ivShow.setImageResource(em.getSrc());
        App.tts.speakText(em.getTip());
        switch (em){
            case warn_already_checked_in:  //已经进站

                break;
            case warn_already_checked_out: //已经出站

                break;
            case warn_card_black:         //黑名单

                break;
            case  warn_card_no_enable:    //卡片未激活

                break;
            case warn_set_date:           //设置日期

                break;
            case warn_cardLocked:         //卡片锁定

                break;
            case warn_invalid_card:       //过期失效卡

                break;
            case warn_low_balance:        //余额不足

                break;
            case warn_manageCard:         //管理卡

                break;
            case warn_stationLocked:      //闸机未启用

                break;
            case error_noset_distance:    //矩阵表未设置

                break;
            case error_read_wallet_fail:  //读取电子钱包失败

                break;
            case punish_already_checked_in:

                break;
            case punish_already_checked_out:

                break;
            case punish_travel_overtime:

                break;
            case success_checked_in:      //请进站

                break;
            case success_checked_out_free://免费出站

                break;
            case success_checked_out:     //请出站

                break;
        }
        Runnable runnableState = new Runnable() {
            @Override
            public void run() {
                ivShow.setImageResource(R.drawable.welcome);
//                flConsume.setVisibility(View.GONE);
            }
        };
        handlerShowState.postDelayed(runnableState, 6000);//延迟3秒恢复

    }

    //监听U盘插入
    private void initUsbInsert() {
        USBReceiver mUsbReceiver = new USBReceiver(this);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter2.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter2.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter2.addDataScheme("file");
        registerReceiver(mUsbReceiver, intentFilter2);
    }


}
