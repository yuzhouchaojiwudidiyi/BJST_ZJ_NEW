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
        App.tts.speakText(em.getTip());

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
