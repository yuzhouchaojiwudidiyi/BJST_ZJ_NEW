package com.wellsun.bjst_zj_new.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;

import com.wellsun.bjst_zj_new.R;


/**
 * date     : 2021/7/8
 * author   : ZhaoZheng
 * describe :
 */

public class BaseDialog extends Dialog {
    private Context mContext;
    private final View mView;
    private boolean mOnTouchCanceled; //触摸是否消失
    private boolean isAnimation;
    private int mStyleAnimation;
    private boolean ifFull = false; //是否全屏

    public BaseDialog(@NonNull Context context, int layout) {
        super(context, R.style.dialog_style);
        this.mContext = context;
        this.mView = LayoutInflater.from(context).inflate(layout, null);
        this.mOnTouchCanceled = mOnTouchCanceled;

    }

    public BaseDialog(@NonNull Context context, int layout, boolean mOnTouchCanceled) {
        super(context, R.style.dialog_style);
        this.mContext = context;
        this.mView = LayoutInflater.from(context).inflate(layout, null);
        this.mOnTouchCanceled = mOnTouchCanceled;

    }

    public BaseDialog(@NonNull Context context, int layout, boolean isfull, boolean mOnTouchCanceled) {
        super(context, R.style.dialog_style);
        this.mContext = context;
        this.mView = LayoutInflater.from(context).inflate(layout, null);
        this.ifFull = isfull;
        this.mOnTouchCanceled = mOnTouchCanceled;

    }

    public BaseDialog(@NonNull Context context, int layout, int animation, boolean mOnTouchCanceled) {
        super(context, R.style.dialog_style);
        this.mContext = context;
        this.mView = LayoutInflater.from(context).inflate(layout, null);
        this.mStyleAnimation = animation;
        this.isAnimation = true;
        this.mOnTouchCanceled = mOnTouchCanceled;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        setCanceledOnTouchOutside(mOnTouchCanceled);
        final Window window = getWindow();
        if (isAnimation) {
            window.setWindowAnimations(mStyleAnimation);
        }

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        if (ifFull) {
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layoutParams.width = 1000; //设置Dialog的宽
//        layoutParams.height = 500; //设置Dialog的高
        } else {
            //一定要在setContentView之后调用，否则无效
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//        layoutParams.width = 1000; //设置Dialog的宽
//        layoutParams.height = 500; //设置Dialog的高
        }
        window.setAttributes(layoutParams);
        setCancelable(true);            //点击返回键取消
        setCanceledOnTouchOutside(true); //点击外部取消

//      解决弹出框时候底部导航栏弹出
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                uiOptions |= 0x00001000;
                window.getDecorView().setSystemUiVisibility(uiOptions);
            }
        });


    }

    public void dismissDialog() {
        if (!((Activity) mContext).isFinishing()) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShowing()) {
                        dismiss();
                    }
                }
            });
        }
    }

}