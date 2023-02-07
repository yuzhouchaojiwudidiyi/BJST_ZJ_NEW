package com.wellsun.bjst_zj_new.utils;

import android.os.Environment;
import android.util.Log;

import com.wellsun.bjst_zj_new.data.StaticData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * date     : 2022-08-15
 * author   : ZhaoZheng
 * describe :
 */
public class CsvUtils {
    static ArrayList<String> lineList = new ArrayList<>();         //读取矩阵表每一行
    static String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/save_wellsun/";
    //    距离
    public static Map<String, String> getDistanceMap() {
        File[] files = new File(savePath).listFiles();
        if (files == null) {
            return null;
        }
        for (int k = 0; k < files.length; k++) {
            if (files[k].getName().toUpperCase().startsWith("FEEMAP")) {
                //获取矩阵表版本
                StaticData.distanceMap_version = files[k].getName().split("_")[1];
                //解析
                File fileSaveDistance = new File(files[k].getPath());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileSaveDistance), "UTF-8"));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        lineList.add(readLine);
                    }
                    bufferedReader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String line1 = lineList.get(0);                     //第一行站点内容
                String[] line1List = line1.split(",");
                Map<String, String> mapDistance = new LinkedHashMap<>();
                for (int i = 1; i < lineList.size(); i++) {         //从第二行开始
                    String lineNext = lineList.get(i);
                    String[] lineNextList = lineNext.split(",");
                    String startSplit = lineNextList[0];
                    for (int j = 1; j < lineNextList.length; j++) { //遍历每一行内容
                        String distance = lineNextList[j];           //距离
                        String endSplit = line1List[j];             //第一行 每个站点
                        mapDistance.put(startSplit + "_" + endSplit, distance);
                    }
                }

                return mapDistance;
            }

        }
        return null;
    }

    //  黑名单
    public static ArrayList<String> getBlackList() {
        ArrayList<String> blackList = new ArrayList<>();
        File[] files = new File(savePath).listFiles();
        Log.v("文件","savePath="+savePath);

        if (files == null) {
            return null;
        }
        Log.v("文件","files="+files.length);

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toUpperCase().startsWith("BL")) {
                //获取版本号
                StaticData.blackList_version = files[i].getName().split("_")[1];
                //解析
                File fileSaveDistance = new File(files[i].getPath());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileSaveDistance), "UTF-8"));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        blackList.add(readLine);
                    }
                    bufferedReader.close();
                    return blackList;

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
        return null;
    }

    // 价格
    public static Map<String, Integer> getPrice() {
        ArrayList<String> priceList = new ArrayList<>();
        File[] files = new File(savePath).listFiles();
        if (files == null) {
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toUpperCase().startsWith("FARE")) {
                //获取票价版本
                StaticData.mapPrice_version = files[i].getName().split("_")[1];
                //解析
                File fileSaveDistance = new File(files[i].getPath());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileSaveDistance), "UTF-8"));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        priceList.add(readLine);
                    }
                    bufferedReader.close();
                    Map<String, Integer> mapPrice = new LinkedHashMap<>();
                    for (int j = 1; j < priceList.size(); j++) {  //第一行不读取
                        String priceLine = priceList.get(j);
                        String[] priceSplit = priceLine.split(",");
                        String distance = priceSplit[0];
                        Integer price = Integer.parseInt(priceSplit[1]);
                        mapPrice.put(distance, price);
                        StaticData.punish_amount = price;//记录惩罚最大金额
                        Log.v("惩罚",price+"");
                    }
                    return mapPrice;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] aa) {
    }
}
