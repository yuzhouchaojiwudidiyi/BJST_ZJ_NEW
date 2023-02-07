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
    public static String pasmId = "";
    //闸机是否开启使用状态
    public static boolean use_state=true;
    //进出站标记 00进站 01出站
    public static String check_in_out = "00";
    //最大重刷卡时间
    public static long max_swipe_time = 30;
    //最大旅行时间
    public static long max_travel_time = 60 * 60 * 2;
    //站点名6位 bcd
    public static String station_name="000000";
    //设备编号8位 bcd
    public static String deviceMac="00000000";
    //线路号 bcd
    public static String line_number="000000";
    //同站进出免费最大时间
    public static long max_same_station_time=60*10;
    //本地流水号
    public static int localTradeNumber;
    //
}
