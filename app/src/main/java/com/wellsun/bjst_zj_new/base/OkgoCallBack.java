package com.wellsun.bjst_zj_new.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.wellsun.bjst_zj_new.R;
import com.wellsun.bjst_zj_new.utils.ToastPrint;
import java.lang.reflect.Type;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * date     : 2021/7/7
 * author   : ZhaoZheng
 * describe :
 */
public abstract class OkgoCallBack<T> extends AbsCallback<T> {
    private Type type;
    private Class<T> clazz;
    private boolean isShow = true;
    private Context mContext;

    public OkgoCallBack() {
    }

    /**
     * 没有加载框
     */
    public OkgoCallBack(Type type) {
        this.type = type;
    }

    public OkgoCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 有加载框
     */
    public OkgoCallBack(Type type, Context context) {
        this.type = type;
        this.mContext = context;
    }

    public OkgoCallBack(Class<T> clazz, Context context) {
        this.clazz = clazz;
        this.mContext = context;
    }


    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;
        T data = null;
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if (type != null) data = gson.fromJson(jsonReader, type);
        if (clazz != null) data = gson.fromJson(jsonReader, clazz);
        return data;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        startLoading("");
    }


    @Override
    public void onCacheSuccess(com.lzy.okgo.model.Response<T> response) {
        super.onCacheSuccess(response);
        stopLoading();
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        stopLoading();
        boolean isnetwork = isnetwork();
        //无网络
        if (!isnetwork) {
            response.setException(new Exception("no_network"));
            ToastPrint.showText("无网络");
        } else {
            ToastPrint.showText(response.getException().toString());
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        stopLoading();
    }

    @Override
    public void uploadProgress(Progress progress) {
        super.uploadProgress(progress);
    }

    @Override
    public void downloadProgress(Progress progress) {
        super.downloadProgress(progress);
    }


    private BaseDialog dialog;

    private void initDialog(String msg) {
        if (mContext == null) return;
        dialog = new BaseDialog(mContext, R.layout.requst_dialog);
        TextView tvRequest = dialog.findViewById(R.id.tv_request_tips);
        if (null != msg && !TextUtils.isEmpty(msg)) {
            tvRequest.setText(msg);
        } else {
            tvRequest.setText("Loading...");
        }
        dialog.show();
    }

    public void startLoading(String msg) {
        if (isShow) {
            if (null != dialog) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            } else {
                initDialog(msg);
            }
        }
    }

    public void stopLoading() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public boolean isnetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isAvailable();
        }
        return false;
    }
}
