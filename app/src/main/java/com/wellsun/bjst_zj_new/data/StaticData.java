package com.wellsun.bjst_zj_new.data;

import java.util.ArrayList;
import java.util.Map;

/**
 * date     : 2023-01-31
 * author   : ZhaoZheng
 * describe :
 */
public class StaticData {
    //ftp参数
    public static String FtpIp = "192.168.0.101";
    public static String FtpPort = "21";
    public final static String FtpAccount = "GATE";
    public final static String FtpPassWord = "GATE@2019";
    //程序版本
    public static String app_version_name = "0";
    public static int versionCode = 0;
    //黑名单  矩阵  价格
    public static ArrayList<String> blackList;
    public static Map<String, String> distanceMap;
    public static Map<String, Integer> mapPrice;
    //矩阵版本 黑名单版本 价格版本
    public static String distanceMap_version = "";
    public static String blackList_version = "";
    public static String mapPrice_version = "";
    //惩罚额度
    public static Integer punish_amount = 0;
    //读卡器连接状况
    public static boolean readCardState;
    //psam卡状况
    public static boolean samCardState;
    //psam卡终端机编号
    public static String pasmId="";
    //
}
