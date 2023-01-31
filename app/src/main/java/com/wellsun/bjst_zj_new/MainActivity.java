package com.wellsun.bjst_zj_new;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wellsun.bjst_zj_new.db.DbTestBean;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbTestBean dbTestBean = new DbTestBean();
                dbTestBean.setName(System.currentTimeMillis()+"");
                boolean save = dbTestBean.save();
                boolean saved = dbTestBean.isSaved();
                Log.v("内容是,",save+"   "+saved);

//                LitePal.delete(DbTestBean.class,1);

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
