package com.wellsun.bjst_zj_new.broacast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.wellsun.bjst_zj_new.utils.InstallApkUtils;
import com.wellsun.bjst_zj_new.utils.ToastPrint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * date     : 2022-08-14
 * author   : ZhaoZheng
 * describe :
 */
public class USBReceiver extends BroadcastReceiver {
    Context context;
    int usbOnce = 0;
    boolean isResult = false;

    public USBReceiver(Context mainActivity) {
        context = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 这里可以拿到插入的USB设备对象
//        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        switch (intent.getAction()) {
            case "android.intent.action.MEDIA_EJECT": // 拔出USB设备
                ToastPrint.showView("拔出USB设备");
//                downloadTask.cancel(true);
                break;
            case "android.intent.action.MEDIA_MOUNTED": //检测到usb
                Log.v("usb显示:", "usbOnce=" + usbOnce + "");

                usbOnce++;
                if (usbOnce % 2 == 0) {    //偶数
                    ToastPrint.showView("插入USB设备");
                    Log.v("usb显示:", "走几次=" + usbOnce + "");
                    if (intent.getAction() != null) {
                        String usbPath = intent.getData().getPath();
                        if (!TextUtils.isEmpty(usbPath)) {
                            usbFolder(usbPath, context);
                            Log.v("usb显示:", "usbPath=" + usbPath + "");
                        }
                    }
                }
                break;
            default:
                break;
        }


    }


    @SuppressLint("NewApi")
    private synchronized void usbFolder(String usbPath, Context context) {   //usbPath  外部usb存储
        String localPathDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/save_wellsun/";
        // 内部saveFolder创建
        File filesSaveFolder = new File(localPathDir);
        if (!filesSaveFolder.exists()) {
            // 创建文件夹
            filesSaveFolder.mkdirs();
        }

        File[] localFiles = new File(localPathDir).listFiles(); //本地文件数据
        List<String> localFileNameList = new ArrayList<>();     //本地save_wellsun文件名称
        for (File pathFile : localFiles) {                      //获取本地文件名称数据
            localFileNameList.add(pathFile.getName());
        }


        if (!TextUtils.isEmpty(usbPath)) {
            //usb内存
            File[] listFilesUsb = new File(usbPath + "/wellsun/").listFiles();
            Log.v("内存读取:", "listFiles=" + Arrays.asList(listFilesUsb));
            if (listFilesUsb == null) {
                ToastPrint.showView("usb读取错误");
                return;
            }

            for (int i = 0; i < listFilesUsb.length; i++) {
                String ftp_name = listFilesUsb[i].getName();
                if (ftp_name.startsWith("BL")) {

                    if (localFileNameList.stream().anyMatch((a) -> a.startsWith("BL"))) {  //有以bl开头的
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("BL")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                                }
                            }
                        }

                    } else {
                        copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                    }

                } else if (ftp_name.startsWith("Fare")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.startsWith("Fare"))) {  //有以Fare开头的  价格表
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).startsWith("Fare")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                                    Log.v("ftp=", "下载fare=" + "  ftpname=" + ftp_name);

                                }
                            }
                        }
                    } else {
                        copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
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
                                    copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                                    Log.v("ftp=", "下载feemap=" + "  ftpname=" + ftp_name);

                                }
                            }
                        }

                    } else {
                        copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                        Log.v("ftp=1", "下载feemap=" + "  ftpname=" + ftp_name);
                    }

                } else if (ftp_name.endsWith("apk")) {
                    if (localFileNameList.stream().anyMatch((a) -> a.endsWith("apk"))) {  //有以Fare开头的  价格表
                        for (int j = 0; j < localFileNameList.size(); j++) {
                            if (localFileNameList.get(j).endsWith("apk")) {
                                String vsFtp = ftp_name.split("_")[1]; //ftp版本
                                String vsLocal = localFileNameList.get(j).split("_")[1];  //本地版本
                                if (vsLocal.compareTo(vsFtp) < 0) {  //版本小于 删除下载
                                    new File(localPathDir + localFileNameList.get(j)).delete(); //删除本地
                                    copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                                    Log.v("ftp=", "下载bin=" + "  ftpname=" + ftp_name);
                                    InstallApkUtils.installApk((Activity) context, localPathDir+ftp_name);

                                }
                            }
                        }

                    } else {
                        copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                        InstallApkUtils.installApk((Activity) context, localPathDir+ftp_name);
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
                                    copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                                    Log.v("ftp=", "下载bin=" + "  ftpname=" + ftp_name);
                                }
                            }
                        }

                    } else {
                        copy(usbPath + "/wellsun/"+ftp_name, localPathDir+ftp_name);  //要拷贝的  拷贝后的
                        Log.v("ftp=1", "下载bin=" + "  ftpname=" + ftp_name);
                    }
                }


            }


//            //设备自身内存
//            File[] listFilesSave = new File(save_wellsun).listFiles();
//            if (listFilesSave == null) {
//                ToastPrint.showText("读取文件失败");
//                return;
//            }
//
//
//            for (int i = 0; i < listFilesUsb.length; i++) {
//                File fileUsb = listFilesUsb[i];                    //循环usb内文件
//                String usbName = fileUsb.getName().substring(0, 2);
//                for (int j = 0; j < listFilesSave.length; j++) {
//                    File fileSave = listFilesSave[j];
//                    String saveName = fileSave.getName();
//                    if (saveName.toUpperCase().startsWith(usbName)) {
//                        new File(fileSave.getPath()).delete();     //删除和usb内存相同开头的文件
//                    }
//                }
//            }
//
//            for (int i = 0; i < listFilesUsb.length; i++) {
//                File fileUsb = listFilesUsb[i];
//                copy(fileUsb.getPath(), save_wellsun + "/" + fileUsb.getName());  //要拷贝的  拷贝后的
//            }
        }
        Log.v("ftp","是否安装重启"+isResult);

        if (isResult){
            Log.v("ftp","复制完毕");
            ToastPrint.showView("复制完毕");
            restartApp(context);
        }
    }

    /**
     * 重启应用
     *
     * @param context
     */
    public void restartApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }


    public void copy(String strName, String copyStrName) { //要拷贝的  拷贝后的
        isResult = true;
        ToastPrint.showView("复制中...");
        File file = new File(strName);
        //判断要拷贝文件在不
        if (!file.exists()) {
            ToastPrint.showText("没有找到要复制的文件");
            return;
        }
        File fileCopy = new File(copyStrName);
        //判断拷贝新文件在不
        if (!fileCopy.exists()) {
            try {
                fileCopy.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //开始拷贝
        try {
            FileInputStream fileInputStream = new FileInputStream(strName);
            FileOutputStream fileOutputStream = new FileOutputStream(copyStrName);
            byte[] bArr = new byte[8192];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
