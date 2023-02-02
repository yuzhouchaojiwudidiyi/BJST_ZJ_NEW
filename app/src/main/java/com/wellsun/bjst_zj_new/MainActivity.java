package com.wellsun.bjst_zj_new;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cczhr.TTS;
import com.cczhr.TTSConstants;
import com.decard.NDKMethod.BasicOper;
import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.base.BaseActivity;
import com.wellsun.bjst_zj_new.broacast.USBReceiver;
import com.wellsun.bjst_zj_new.db.DbTestBean;
import com.wellsun.bjst_zj_new.readcard.Cmd;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {
        initUsbInsert();  //U盘插入监听

    }

    @Override
    public void initData() {
        Cmd.connectD8(this);          //初始化读卡器

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             App.tts.speakText("0123456789");
            }
        },3000);

    }

    @Override
    public void onClick(View view) {

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
