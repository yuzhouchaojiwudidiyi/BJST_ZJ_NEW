package com.wellsun.bjst_zj_new.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.wellsun.bjst_zj_new.MainActivity;
import com.wellsun.bjst_zj_new.R;
import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.base.BaseActivity;
import com.wellsun.bjst_zj_new.data.StaticData;
import com.wellsun.bjst_zj_new.utils.CsvUtils;
import com.wellsun.bjst_zj_new.utils.InstallApkUtils;
import com.wellsun.bjst_zj_new.utils.ToastPrint;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;

public class Permissionctivity extends BaseActivity {
      String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String localPathDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/save_wellsun/";
    private boolean isInstallApk;

    @Override
    public int getLayoutId() {
        return R.layout.activity_permissionctivity;
    }

    @Override
    public void initView() {
//        startActivity(new Intent(mContext, MainActivity.class));
        initPermissions();
    }

    private void initPermissions() {
        XXPermissions.with(this)
                // 申请单个权限
                //.permission(Permission.RECORD_AUDIO)
                // 申请多个权限
                .permission(PERMISSIONS_STORAGE)
                // 申请多个权限
                //.permission(Permission.Group.CALENDAR)
                // 申请安装包权限
                //.permission(Permission.REQUEST_INSTALL_PACKAGES)
                // 申请悬浮窗权限
                //.permission(Permission.SYSTEM_ALERT_WINDOW)
                // 申请通知栏权限
                //.permission(Permission.NOTIFICATION_SERVICE)
                // 申请系统设置权限
                //.permission(Permission.WRITE_SETTINGS)
                // 设置权限请求拦截器
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制
                //.unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            //ToastPrint.showText("获取录音和日历权限成功");
                            initCsvFile();
                        } else {
                            ToastPrint.showText("获取部分权限成功，但部分权限未正常授予");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            ToastPrint.showText("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(mContext, permissions);
                        } else {
                            ToastPrint.showText("获取录音和日历权限失败");
                        }
                    }
                });
    }

    private void initCsvFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downFile();
                StaticData.blackList = CsvUtils.getBlackList();
                StaticData.distanceMap = CsvUtils.getDistanceMap();
                Log.v("内容是=", Arrays.asList(StaticData.distanceMap).toString());
                StaticData.mapPrice = CsvUtils.getPrice();
                //数据库 处理 黑名单  距离矩阵   距离票价
                if (!isInstallApk) { //如果安装apk 不跳转
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                }
            }
        }).start();
    }

    @SuppressLint("NewApi")
    private void downFile() {
        if (!(new File(localPathDir).exists())) {
            new File(localPathDir).mkdirs();
        }

        File[] localFiles = new File(localPathDir).listFiles(); //本地文件数据
        List<String> localFileNameList = new ArrayList<>();
        for (File pathFile : localFiles) {        //获取本地文件名称数据
            localFileNameList.add(pathFile.getName());
        }
        FTPClient client = new FTPClient();
        try {
            client.connect(StaticData.FtpIp, Integer.parseInt(StaticData.FtpPort));//host:服务器IP地址 port:端口
            client.login(StaticData.FtpAccount, StaticData.FtpPassWord);  //账号秘密
            Log.v("ftp=", "登录成功");
            //下载黑名单
            client.changeDirectory("/DOWNLOAD/GATE/BLACKLIST/");
            /** 获取所有文件的名称**/
            FTPFile[] list = client.list();   //服务器ftp文件夹内文件
            for (int i = 0; i < list.length; i++) {
                String ftp_name = list[i].getName();
                if (ftp_name.startsWith("BL")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.startsWith("BL"))) {  //有以bl开头的
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("BL")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    client.download("/DOWNLOAD/GATE/BLACKLIST/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener()); //下载新的
                                    Log.v("ftp=", "下载bl=" + "  ftpname=" + ftp_name);

                                }
                            }
                        }

                    } else {
                        client.download("/DOWNLOAD/GATE/BLACKLIST/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                        Log.v("ftp=1", "下载bl=" + "  ftpname=" + ftp_name);

                    }
                }
            }

            //价格表  矩阵表
            client.changeDirectory("/DOWNLOAD/GATE/FEEMAP/");
            list = client.list();
            for (int i = 0; i < list.length; i++) {
                String ftp_name = list[i].getName();
                if (ftp_name.startsWith("Fare")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.startsWith("Fare"))) {  //有以Fare开头的  价格表
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("Fare")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    client.download("/DOWNLOAD/GATE/FEEMAP/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                                    Log.v("ftp=", "下载fare=" + "  ftpname=" + ftp_name);

                                }
                            }
                        }
                    } else {
                        client.download("/DOWNLOAD/GATE/FEEMAP/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                        Log.v("ftp=1", "下载fare=" + "  ftpname=" + ftp_name);

                    }
                } else if (ftp_name.startsWith("FEEMAP")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.startsWith("FEEMAP"))) {
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("FEEMAP")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    client.download("/DOWNLOAD/GATE/FEEMAP/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                                    Log.v("ftp=", "下载feemap=" + "  ftpname=" + ftp_name);

                                }
                            }
                        }

                    } else {
                        client.download("/DOWNLOAD/GATE/FEEMAP/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                        Log.v("ftp=1", "下载feemap=" + "  ftpname=" + ftp_name);

                    }
                }
            }


            //下载bin文件和apk
            client.changeDirectory("/DOWNLOAD/GATE/Program/");
            list = client.list();
            for (int i = 0; i < list.length; i++) {
                String ftp_name = list[i].getName();
                if (ftp_name.endsWith("apk")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.endsWith("apk"))) {  //有以Fare开头的  价格表
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).endsWith("apk")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    client.download("/DOWNLOAD/GATE/Program/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListenerApk());
                                    Log.v("ftp=", "下载bin=" + "  ftpname=" + ftp_name);
                                }
                            }
                        }

                    } else {
                        client.download("/DOWNLOAD/GATE/Program/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListenerApk());
                        Log.v("ftp=1", "下载apk=" + "  ftpname=" + ftp_name);

                    }

                } else if (ftp_name.endsWith("bin")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.endsWith("bin"))) {
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("bin")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    client.download("/DOWNLOAD/GATE/Program/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                                    Log.v("ftp=", "下载bin=" + "  ftpname=" + ftp_name);
                                }
                            }
                        }

                    } else {
                        client.download("/DOWNLOAD/GATE/Program/" + ftp_name, new File(localPathDir + ftp_name), new MyTransferListener());
                        Log.v("ftp=1", "下载bin=" + "  ftpname=" + ftp_name);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("ftp=", "登录失败=" + e);
            return;
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {

    }


    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            // Transfer started
            Log.v("ftp", "下载开始");
        }

        public void transferred(int length) {
            // Yet other length bytes has been transferred since the last time this
            // method was called
            Log.v("ftp", "下载进度" + length);

        }

        public void completed() {
            // Transfer completed
            Log.v("ftp", "下载完成");


        }

        public void aborted() {
            // Transfer aborted
            Log.v("ftp", "下载中断");

        }

        public void failed() {
            // Transfer failed
            Log.v("ftp", "下载失败");

        }

    }


    public class MyTransferListenerApk implements FTPDataTransferListener {

        public void started() {
            // Transfer started
            Log.v("ftp", "下载开始");
        }

        public void transferred(int length) {
            // Yet other length bytes has been transferred since the last time this
            // method was called
            Log.v("ftp", "下载进度" + length);

        }

        public void completed() {
            // Transfer completed
            Log.v("ftp", "下载完成");
            String localPathDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/save_wellsun/";
            File[] localFiles = new File(localPathDir).listFiles(); //本地文件数据
            List<String> localFileNameList = new ArrayList<>();
            for (File pathFile : localFiles) {        //获取本地文件名称数据
                localFileNameList.add(pathFile.getName());
                String name = pathFile.getName();
                if (name.endsWith("apk")) {
                    String path = pathFile.getPath();
                    Log.v("apk地址", path);
                    String vsFtpApk = name.split("_")[1]; //ftp版本
                    if (Integer.parseInt(vsFtpApk) > StaticData.versionCode) {   //更新apk
                        isInstallApk = true;
                        InstallApkUtils.installApk(Permissionctivity.this, path);
                    }
                }
            }

        }

        public void aborted() {
            // Transfer aborted
            Log.v("ftp", "下载中断");

        }

        public void failed() {
            // Transfer failed
            Log.v("ftp", "下载失败");

        }

    }
}
