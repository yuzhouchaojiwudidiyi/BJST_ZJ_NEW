package com.wellsun.bjst_zj_new;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cczhr.TTS;
import com.cczhr.TTSConstants;
import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.db.DbTestBean;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;
    private TTS tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取单例对象
        tts = TTS.getInstance();
        tts.init(MainActivity.this, TTSConstants.TTS_XIAOYAN);//初始化
        initView();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbTestBean dbTestBean = new DbTestBean();
                dbTestBean.setName(System.currentTimeMillis() + "");
                boolean save = dbTestBean.save();
                boolean saved = dbTestBean.isSaved();
                Log.v("内容是,", save + "   " + saved);

//                LitePal.delete(DbTestBean.class,1);
                tts.stop();
                tts.speakText(System.currentTimeMillis() + "");

            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DbTestBean> all = LitePal.findAll(DbTestBean.class);
                Log.v("内容是", Arrays.toString(all.toArray()));

            }
        });
    }
}
