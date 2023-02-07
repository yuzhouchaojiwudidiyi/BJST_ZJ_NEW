package com.wellsun.bjst_zj_new.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.decard.NDKMethod.BasicOper;

import com.wellsun.bjst_zj_new.data.StaticData;
import com.wellsun.bjst_zj_new.dbsqlit.DbCardQrBean;
import com.wellsun.bjst_zj_new.em.ShowTipCodeEm;
import com.wellsun.bjst_zj_new.em.VoiceTipCodeEm;
import com.wellsun.bjst_zj_new.kx.BytesUtil;
import com.wellsun.bjst_zj_new.kx.CRC16;
import com.wellsun.bjst_zj_new.tcpbean.CardSaveBean;
import com.wellsun.bjst_zj_new.utils.L;
import com.wellsun.bjst_zj_new.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * date     : 2023-02-03
 * author   : ZhaoZheng
 * describe :
 */
public class ReadCardService extends Service {
    String csn = "00000000";//四字节物理卡号
    //    dc_cpuapdu_hex     cpu卡
    //    dc_pro_commandhex  非接触卡
    public static String consumeMoney = "00000000";    //消费金额8位
    public static String cardId = "0000000000000000";  //卡号 16位

    //pasm指令
    public static String cmd_choose_sam1 = "00A40000023F00";              //选择sam1卡应用
    public static String cmd_choose_pasm_application = "00a40000021001";  //pasm选择应用
    public static String cmd_getpasmid = "00B0960006";                    //获取终端机号
    public static String cmd_get_tradenumber_mac1 = "00C0000008";         //4 字节的终端脱机交易序号 + 4字节mac1
    public static String cmd_verifym_ac2 = "8072000004";                  // 验证mac2
    //选择电子钱包
    public static String chooseWallet = "00A40000020002";                 // 选择电子钱包
    //读电子钱包
    public static String readWallet = "805C000204";                       // 选择电子钱包
    //ic卡指令
    public static String consumeType = "09";                  //消费类型 09复合消费
    public static String cmd_choose_3f01 = "00A40000023F01";  //选择3f01电子钱包应用
    public static String cmd_consume_one = "805003020B01";    //复合消费初始化第一步
    public static String cmd_calculate_mac1 = "807000001C";   //pasm卡请求 计算mac1
    public static String cmd_read_001A_01 = "00B201D000";     //变成记录文件 复合交易文件 公交第一条记录
    public static String cmd_up_001A_01 = "80DC01D078";       //更新复合文件 第一条数据
    public static String cmd_consume_two = "805401000F";      //复合消费初始化第二步 扣款
    public static String cmd_choose_1001 = "00A40000021001";  //管理员卡应用
    public static String cmd_read_manage_0010 = "00B2018400"; //管理员0010 文件 卡类型
    private int cardBalanceInt;  //余额
    String showHuancheng = "0";  //换乘次数
    String showLeijiJuli = "0";  //累计距离
    String showYingkouJine = "0";//应扣金额
    String showShikouJine = "0"; //实扣金额
    private ShowTipCodeEm showTipCodeEm;
    ExecutorService esCardDataSave = Executors.newSingleThreadExecutor(); //接收线程池


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ReadCardThread readCardThread = new ReadCardThread();
        readCardThread.start();
    }

    //循环读卡逻辑
    class ReadCardThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    //处理逻辑
                    consume();
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("异常",e.toString());
                }
            }
        }
    }

    private void consume() {
        //1寻卡 0x00表示对空闲卡进行操作，0x01表示对所有卡操作。
        String r_xunKa = BasicOper.dc_card_hex(0x01);
        String[] rA_xunKa = r_xunKa.split("\\|", -1);
        L.v("指令", "寻卡结果=" + r_xunKa);
        if (rA_xunKa[0].equals("0000")) {
            csn = rA_xunKa[1];  //物理卡号
            csn = csn.substring(6, 8) + csn.substring(4, 6) + csn.substring(2, 4) + csn.substring(0, 2);
            String new_r_001A_01 = "";
            //2卡片复位
            String r_fuWei = BasicOper.dc_pro_resethex();
            String[] rA_fuWei = r_fuWei.split("\\|", -1);
            L.v("指令", "卡片复位结果=" + r_fuWei);
            if (rA_fuWei[0].equals("0000")) {
                //3进入电子钱包应用
                String r_choose_3f01 = BasicOper.dc_pro_commandhex(cmd_choose_3f01, 7);
                String[] rA_choose_3f01 = r_choose_3f01.split("\\|", -1);
                L.v("指令", "进入电子钱包应用结果=" + r_choose_3f01);
                //6A81 卡片锁住了
                if (rA_choose_3f01[0].equals("0000") && rA_choose_3f01[1].endsWith("6A81")) {
                    EventBus.getDefault().post(VoiceTipCodeEm.warn_cardLocked);
                    return;
                }
                //6A82 没有找到这个应用 应该是管理员卡
                if (rA_choose_3f01[0].equals("0000") && rA_choose_3f01[1].endsWith("6A82")) {
                    /*处理*/
                    //manageCard(); //管理卡
                    EventBus.getDefault().post(VoiceTipCodeEm.warn_manageCard);
                    return;
                }
                //闸机是否开启使用状态
                if (!StaticData.use_state) {
                    /*处理*/
                    EventBus.getDefault().post(VoiceTipCodeEm.warn_stationLocked);
                    return;
                }
                if (rA_choose_3f01[0].equals("0000") && rA_choose_3f01[1].endsWith("9000")) {
                    String cardMessage = rA_choose_3f01[1].substring(44, rA_choose_3f01[1].length() - 4);
                    String qiYongTag = cardMessage.substring(16, 18);     //启用标识  00未启用 01已启用
                    cardId = cardMessage.substring(24, 40);               //卡号
                    String qiYongDate = cardMessage.substring(40, 48);    //应用启用日期
                    String youXiaoDate = cardMessage.substring(48, 56);   //应用有效日期
                    String zhuKaType = cardMessage.substring(56, 58);     //主卡类型
                    String ziKaType = cardMessage.substring(58, 60);      //子卡类型
                    String qiYongTag2 = cardMessage.substring(60, 62);    //协议卡 00停用 01启用
                    String zheKouLv_2 = cardMessage.substring(62, 64);    // 协议卡票价折扣百分率 0x32表示50%
                    String qiYongDate_2 = cardMessage.substring(64, 72);  // 协议签订日期启动时更新
                    String youXiaoDate_2 = cardMessage.substring(72, 80); // 协议有效日期启动时更新

                    String yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String yymmdd = yyyyMMddHHmmss.substring(0, 8);     //年月日
                    String hhmmss = yyyyMMddHHmmss.substring(8, 14);    //时分秒
                    //判断卡未启用
                    if (qiYongTag.equals("00")) {
                        EventBus.getDefault().post(VoiceTipCodeEm.warn_card_no_enable);
                        return;
                    }
                    //判断启用时间
                    if (yymmdd.compareTo(qiYongDate) < 0) {  //请设置系统日期 当前日期小于卡启用日期
                        EventBus.getDefault().post(VoiceTipCodeEm.warn_set_date);
                        return;
                    }
                    //判断过期时间
                    if (yymmdd.compareTo(youXiaoDate) > 0) {  //卡过期 当前日期大于卡有效日期
                        EventBus.getDefault().post(VoiceTipCodeEm.warn_invalid_card);
                        return;
                    }
                    //判断黑名单
                    if (StaticData.blackList != null && StaticData.blackList.contains(cardId)) {
                        EventBus.getDefault().post(VoiceTipCodeEm.warn_card_black);
                        return;
                    }
                    //选电子钱包
                    String r_chooseWallet = BasicOper.dc_pro_commandhex(chooseWallet, 7);
                    String[] rA_chooseWallet = r_chooseWallet.split("\\|", -1);
                    L.v("指令", "选择电子钱包结果" + r_chooseWallet);
                    if (!rA_chooseWallet[0].equals("0000") || !rA_chooseWallet[1].endsWith("9000")) {
                        EventBus.getDefault().post(VoiceTipCodeEm.error_read_wallet_fail);
                        return;
                    }
                    //读电子钱包
                    String r_readWallet = BasicOper.dc_pro_commandhex(readWallet, 7);
                    String[] rA_readWallet = r_readWallet.split("\\|", -1);
                    L.v("指令", "读电子钱包结果" + r_readWallet);
                    if (!rA_readWallet[0].equals("0000") || !rA_readWallet[1].endsWith("9000")) {
                        EventBus.getDefault().post(VoiceTipCodeEm.error_read_wallet_fail);
                        return;
                    }
                    //余额十六进制数
                    String cardBalanceHex = rA_readWallet[1].substring(0, 8);
                    //余额十进制数
                    cardBalanceInt = Integer.parseInt(cardBalanceHex, 16);
                    //余额不足
                    if (cardBalanceInt < StaticData.punish_amount) {
                        EventBus.getDefault().post(VoiceTipCodeEm.warn_low_balance);
                        return;
                    }
                    //读取复合交易文件变长记录文件   运算扣款金额
                    String r_001A_01 = read001A_01();                            //变成记录文件 复合交易文件 公交第一条记录
                    String fuhe_jilu_biaoshi = r_001A_01.substring(0, 2);        //01~01	复合记录标识	1	BCD	//0x01
                    String changdu = r_001A_01.substring(2, 4);                  //02~02	复合交易数据长度	1	BCD	固定值
                    String jiaoyi_suoding = r_001A_01.substring(4, 6);           //03~03	交易锁定标志	1	BCD	//0x00 允许//0x01 禁止
                    String jilu_shiyong_biaoshi = r_001A_01.substring(6, 14);    //04~07	记录使用标识	4	ASCII	ZHKX
                    String baoliu = r_001A_01.substring(14, 18);                 //08~09	Ruf	2	HEX	保留域
                    String jiaoyi_zhuangtai = r_001A_01.substring(18, 20);       //10~10	交易状态	1	BCD	//0x00 进站//0x01 出站
                    String jiaoyi_liushuihao = r_001A_01.substring(20, 36);      //11~18	交易流水号	8	BCD	全0
                    String jinzhan_zhandian = r_001A_01.substring(36, 44);       //19~22	进站站点	4	HEX	19：方向
                    String jinzhan_shijian = r_001A_01.substring(44, 58);        //23~29	进站时间	7	BCD	YYYYMMDDhhmmss
                    String beiyong1 = r_001A_01.substring(58, 60);               //30~30	备用	1	HEX
                    String jinzhan_zhongduan_bianhao = r_001A_01.substring(60, 68);//31~34	进站终端编号	4	HEX
                    String chuzhan_zhandian = r_001A_01.substring(68, 76);       //35~38	出站站点	4	HEX	35：方向
                    String chuzhan_shijian = r_001A_01.substring(76, 90);        //39~45	出站时间	7	BCD	YYYYMMDDhhmmss
                    String beiyong2 = r_001A_01.substring(90, 94);               //46~47	备用	2	HEX
                    String chuzhan_zhongduan_bianhao = r_001A_01.substring(94, 102);  //48~51	出站终端编号	4	HEX
                    String zuida_xiaofei_jine = r_001A_01.substring(102, 110);        //52~55	最大消费金额	4	HEX	高字节在前
                    String xingcheng_zhuangkuang = r_001A_01.substring(110, 112);     //56	行程状况	1	HEX 	0：异常  1：正常  2：转乘1次   3：转乘2次
                    String leiji_chengche_juli = r_001A_01.substring(112, 116);       //57-58	累计乘车距离	2	HEX	高位在前，低位在后
                    String yikou_feiyong = r_001A_01.substring(116, 120);             //59-60	已扣费用	2	HEX	高位在前，低位在后
                    String jinzhan_xianluhao = r_001A_01.substring(120, 126);         //进站线路号
                    String chuzhan_xianluhao = r_001A_01.substring(126, 132);         //出站线路号
                    String qita = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"; //线路号
                    L.v("记录", "r_001A_01=" + r_001A_01);
                    L.v("记录", "复合记录标识" + fuhe_jilu_biaoshi);
                    L.v("记录", "长度" + changdu);
                    L.v("记录", "交易锁定标识" + jiaoyi_suoding);
                    L.v("记录", "记录使用标识" + jilu_shiyong_biaoshi);
                    L.v("记录", "保留" + baoliu);
                    L.v("记录", "交易状态" + jiaoyi_zhuangtai);
                    L.v("记录", "交易流水号" + jiaoyi_liushuihao);
                    L.v("记录", "进站站点" + jinzhan_zhandian);
                    L.v("记录", "进站时间" + jinzhan_shijian);
                    L.v("记录", "备用1=" + beiyong1);
                    L.v("记录", "终端编号" + jinzhan_zhongduan_bianhao);
                    L.v("记录", "出站站点" + chuzhan_zhandian);
                    L.v("记录", "出站时间" + chuzhan_shijian);
                    L.v("记录", "备用2=" + beiyong2);
                    L.v("记录", "出站终端编号" + chuzhan_zhongduan_bianhao);
                    L.v("记录", "最大消费金额" + zuida_xiaofei_jine);
                    L.v("记录", "行程状态" + xingcheng_zhuangkuang);
                    L.v("记录", "累计乘车距离" + leiji_chengche_juli);
                    L.v("记录", "已扣费用" + yikou_feiyong);
                    L.v("记录", "进站线路号" + jinzhan_xianluhao);
                    L.v("记录", "出站线路号" + chuzhan_xianluhao);
                    L.v("记录", "其它" + qita);
                    //第一次刷卡设置上次交易为出站
                    if (jinzhan_shijian.equals("00000000000000")) {
                        jiaoyi_zhuangtai = "01";
                    }

                    long differTimeIn = getTimeDiffer(yyyyMMddHHmmss, jinzhan_shijian);
                    //进站标记
                    if (StaticData.check_in_out.equals("00")) {
                        switch (jiaoyi_zhuangtai) {
                            case "00"://上次标记进站
                                if (differTimeIn < StaticData.max_swipe_time) {
                                    //**1已进站提示
                                    EventBus.getDefault().post(VoiceTipCodeEm.warn_already_checked_in);
                                    return;
                                } else {
                                    //**2进站又进站惩罚扣款
                                    xingcheng_zhuangkuang = "00";                                      //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                    leiji_chengche_juli = "0000";                                      //异常状态 累计乘车距离清零
                                    yikou_feiyong = String.format("%04X", StaticData.punish_amount);   //写入复合交易 扣款的为惩罚额度
                                    consumeMoney = String.format("%08X", StaticData.punish_amount);    //消费扣款金额  罚款额度
                                    showTipCodeEm = ShowTipCodeEm.punish_already_checked_in;
                                }
                                break;
                            case "01"://上次标记出站
                                //**3正常进站逻辑
//                                xingcheng_zhuangkuang = "00";                                      //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
//                                leiji_chengche_juli = "0000";                                      //异常状态 累计乘车距离清零
                                yikou_feiyong = String.format("%04X", 0);                           //写入复合交易0
                                consumeMoney = String.format("%08X", 0);                            //进站消费扣款0
                                showTipCodeEm = ShowTipCodeEm.success_checked_in;
                                break;
                        }
                        jiaoyi_zhuangtai = "00";
                        jinzhan_zhandian = "01" + String.format("%06d", Integer.parseInt(StaticData.station_name));//进站站点8位 01闸机固定  站点号转成6位十六进制数
                        jinzhan_shijian = yyyyMMddHHmmss;                                  //进站时间
                        jinzhan_zhongduan_bianhao = StaticData.deviceMac;                  //设备号
                        jinzhan_xianluhao = String.format("%06d", Integer.parseInt(StaticData.station_name)); //进站线路号

                        //出站标记
                    } else if (StaticData.check_in_out.equals("01")) {
                        switch (jiaoyi_zhuangtai) {
                            case "00"://上次标记进站
                                if (differTimeIn >= StaticData.max_travel_time) {
                                    //**4超时罚款出站
                                    xingcheng_zhuangkuang = "00";                                     //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                    leiji_chengche_juli = "0000";                                     //异常状态 累计乘车距离清零
                                    yikou_feiyong = String.format("%04X", StaticData.punish_amount);  // 写入复合交易 扣款的为惩罚额度
                                    consumeMoney = String.format("%08X", StaticData.punish_amount);   //消费扣款金额  罚款额度
                                    showTipCodeEm = ShowTipCodeEm.punish_travel_overtime;
                                } else {
                                    //**5正常出站
                                    int shangxia_xing_decimal = Integer.parseInt(jinzhan_zhandian.substring(0, 2), 16);    //十进制上下行  1或者2
                                    int jinzhan_zhandian_decimal = Integer.parseInt(jinzhan_zhandian.substring(2, 8), 16); //进站站点 16进制转十进制
                                    int chuzhan_zhandian_decimal = Integer.parseInt(StaticData.station_name);                                             //出站站点 16进制
                                    String distanceKey = jinzhan_zhandian_decimal + "|" + shangxia_xing_decimal + "_" + chuzhan_zhandian_decimal + "|" + "1";
                                    String distanceValue = StaticData.distanceMap.get(distanceKey); //本次消费距离
                                    //**5-1同站设置时间内进出免费
                                    if (jinzhan_zhandian_decimal == Integer.parseInt(StaticData.station_name) && (getTimeDiffer(yyyyMMddHHmmss, jinzhan_shijian) < StaticData.max_same_station_time)) {
                                        xingcheng_zhuangkuang = "00";
                                        leiji_chengche_juli = "0000";
                                        yikou_feiyong = String.format("%04X", 0);                           //写入复合交易0
                                        consumeMoney = String.format("%08X", 0);                            //进站消费扣款0
                                        showTipCodeEm = ShowTipCodeEm.success_checked_out_free;

                                    } else if (distanceValue == null || distanceValue.equals("-1")) {
                                        //**5-2异常出站 或没有找到票价
                                        xingcheng_zhuangkuang = "00";
                                        leiji_chengche_juli = "0000";
                                        yikou_feiyong = String.format("%04X", StaticData.punish_amount);// 写入复合交易 扣款
                                        consumeMoney = String.format("%08X", StaticData.punish_amount); //消费扣款金额
                                        showTipCodeEm = ShowTipCodeEm.error_noset_distance;
                                    } else {
                                        //正常消费
                                        Integer yingkou_price = getDistancePrice(distanceValue);  //根据距离算价格
                                        //**5-3 上次出站线路号和本次出站线路号一样不免费 记录距离乘车状态恢复成01
                                        if (Integer.parseInt(chuzhan_xianluhao, 16) == (Integer.parseInt(StaticData.line_number))) {
                                            xingcheng_zhuangkuang = "01";
                                            leiji_chengche_juli = String.format("%04X", Integer.parseInt(distanceValue)); //记录累计乘车距离
                                            yikou_feiyong = String.format("%04X", yingkou_price);                         // 写入复合交易 扣款
                                            consumeMoney = String.format("%08X", yingkou_price);                          //消费扣款金额
                                        } else {
                                            //**5-4 优惠消费
                                            switch (xingcheng_zhuangkuang) {
                                                case "00":  //上次异常状态
                                                case "03":
                                                    leiji_chengche_juli = String.format("%04X", Integer.parseInt(distanceValue)); //记录累计乘车距离
                                                    yikou_feiyong = String.format("%04X", yingkou_price);                         // 写入复合交易 扣款
                                                    consumeMoney = String.format("%08X", yingkou_price);                          //消费扣款金额
                                                    break;
                                                case "01":  //场次正常状态
                                                case "02":
                                                    int leiji_chengche_juli_decimal_1 = Integer.parseInt(leiji_chengche_juli, 16);   //之前累计消费距离 16进制字符串 转10进制数
                                                    int priceLeiji_1 = getDistancePrice(leiji_chengche_juli_decimal_1 + "");               //除了本次累积乘车距离金额
                                                    int totalDistance_1 = leiji_chengche_juli_decimal_1 + Integer.parseInt(distanceValue); //第一个参数十六进制转十进制  第二个参数字符串转十进制
                                                    int priceTotal_1 = getDistancePrice(totalDistance_1 + "");                             //上次距离+本次距离价格

                                                    leiji_chengche_juli = String.format("%04X", totalDistance_1);                          //记录两次累计乘车距离
                                                    int shikou_price_1 = priceTotal_1 - priceLeiji_1;
                                                    yikou_feiyong = String.format("%04X", shikou_price_1);                                 // 写入复合交易 扣款
                                                    consumeMoney = String.format("%08X", shikou_price_1);                                  //消费扣款金额
                                                    break;
                                            }
                                            if (xingcheng_zhuangkuang.equals("00")) {
                                                xingcheng_zhuangkuang = "01"; //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                            } else if (xingcheng_zhuangkuang.equals("01")) {
                                                xingcheng_zhuangkuang = "02"; //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                            } else if (xingcheng_zhuangkuang.equals("02")) {
                                                xingcheng_zhuangkuang = "03"; //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                            } else if (xingcheng_zhuangkuang.equals("03")) {
                                                xingcheng_zhuangkuang = "01"; //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                            }
                                            showTipCodeEm = ShowTipCodeEm.success_checked_out;
                                        }
                                    }

                                }
                                break;
                            case "01"://上次标记出站
                                if (differTimeIn < StaticData.max_swipe_time) {
                                    //**6已出站提示
                                    /* 处理出进站*/
                                    EventBus.getDefault().post(VoiceTipCodeEm.warn_already_checked_out);
                                    return;
                                } else {
                                    //**7出站又出站惩罚扣款
                                    xingcheng_zhuangkuang = "00";                                     //恢复正常  0：异常    1：正常    2：转乘1次     3：转乘2次
                                    leiji_chengche_juli = "0000";                                     //异常状态 累计乘车距离清零
                                    yikou_feiyong = String.format("%04X", StaticData.punish_amount);  // 写入复合交易 扣款的为惩罚额度
                                    consumeMoney = String.format("%08X", StaticData.punish_amount);   //消费扣款金额  罚款额度
                                    EventBus.getDefault().post(VoiceTipCodeEm.punish_already_checked_out);
                                }
                                break;
                        }

                        jiaoyi_zhuangtai = "01";
                        chuzhan_zhandian = "01" + String.format("%06d", Integer.parseInt(StaticData.station_name));//出站站点8位 01闸机固定   站点号转成6位十六进制数
                        chuzhan_shijian = yyyyMMddHHmmss;                                 //出站时间
                        chuzhan_zhongduan_bianhao = StaticData.deviceMac;                 //出站终端机号
                        chuzhan_xianluhao = String.format("%06d", Integer.parseInt(StaticData.line_number));//出站线路号
                    }


                    //消费写卡逻辑
                    String cmd_consume_one_all = cmd_consume_one + consumeMoney + StaticData.pasmId + "0F";
                    String r_consume_one = BasicOper.dc_pro_commandhex(cmd_consume_one_all, 7); //消费初始化第一步
                    String[] rA_consume_one = r_consume_one.split("\\|", -1);
                    L.v("指令", "初始化消费第一步" + cmd_consume_one_all);
                    L.v("指令", "初始化消费第一步结果" + r_consume_one);
                    if (rA_consume_one[0].equals("0000") && rA_consume_one[1].endsWith("9000")) {
                        String rA_consume_one_0 = rA_consume_one[1];
                        String surplus = rA_consume_one_0.substring(0, 8);       //余额
                        String tradeNumber = rA_consume_one_0.substring(8, 12);  //电子钱包脱机交易序号
                        String overdraw = rA_consume_one_0.substring(12, 18);    //透支金额
                        String keyVersion = rA_consume_one_0.substring(18, 20);  //密匙版本号
                        String algorithm = rA_consume_one_0.substring(20, 22);   //算法标识
                        String random = rA_consume_one_0.substring(22, 30);      //伪随机数
                        //6 pasm卡 计算mac1值
                        String cmd_calculate_mac1_all = cmd_calculate_mac1 + random + tradeNumber + consumeMoney + consumeType + yymmdd + hhmmss + keyVersion + algorithm + cardId + "08";
                        String r_cmd_calculate_mac1_all = BasicOper.dc_cpuapdu_hex(cmd_calculate_mac1_all);        //pasm卡计算mac1
                        String[] rA_cmd_calculate_mac1_all = r_cmd_calculate_mac1_all.split("\\|", -1);
                        L.v("指令", "pasm卡计算交易序列号和mac1" + cmd_calculate_mac1_all);
                        L.v("指令", "pasm卡计算交易序列号和mac1结果" + r_cmd_calculate_mac1_all);
                        if (rA_cmd_calculate_mac1_all[0].endsWith("0000") && rA_cmd_calculate_mac1_all[1].endsWith("6108")) {
                            //7 获取4字节终端交易序号+4字节mac1
                            String r_cmd_get_tradenumber_mac1 = BasicOper.dc_cpuapdu_hex(cmd_get_tradenumber_mac1);       //获取响应8字节数据
                            String[] rA_cmd_get_tradeNumber_mac1 = r_cmd_get_tradenumber_mac1.split("\\|", -1);
                            L.v("指令", "获取pasm卡计算交易序列号和mac1" + cmd_get_tradenumber_mac1);
                            L.v("指令", "获取pasm卡计算交易序列号和mac1结果" + r_cmd_get_tradenumber_mac1);
                            if (rA_cmd_get_tradeNumber_mac1[0].endsWith("0000") && rA_cmd_get_tradeNumber_mac1[1].endsWith("9000")) {
                                //8 更新复合交易文件 001A 第一条数据
                                L.v("记录", "************************************************************");
                                L.v("记录", "r_001A_01=" + r_001A_01);
                                L.v("记录", "复合记录标识" + fuhe_jilu_biaoshi);
                                L.v("记录", "长度" + changdu);
                                L.v("记录", "交易锁定标识" + jiaoyi_suoding);
                                L.v("记录", "记录使用标识" + jilu_shiyong_biaoshi);
                                L.v("记录", "保留" + baoliu);
                                L.v("记录", "交易状态" + jiaoyi_zhuangtai);
                                L.v("记录", "交易流水号" + jiaoyi_liushuihao);
                                L.v("记录", "进站站点" + jinzhan_zhandian);
                                L.v("记录", "进站时间" + jinzhan_shijian);
                                L.v("记录", "备用1=" + beiyong1);
                                L.v("记录", "进站终端编号" + jinzhan_zhongduan_bianhao);
                                L.v("记录", "出站站点" + chuzhan_zhandian);
                                L.v("记录", "出站时间" + chuzhan_shijian);
                                L.v("记录", "备用2=" + beiyong2);
                                L.v("记录", "出站终端编号" + chuzhan_zhongduan_bianhao);
                                L.v("记录", "最大消费金额" + zuida_xiaofei_jine);
                                L.v("记录", "形成状态" + xingcheng_zhuangkuang);
                                L.v("记录", "累计乘车距离" + leiji_chengche_juli);
                                L.v("记录", "已扣费用" + yikou_feiyong);
                                L.v("记录", "其它" + qita);

                                new_r_001A_01 = fuhe_jilu_biaoshi
                                        + changdu
                                        + jiaoyi_suoding
                                        + jilu_shiyong_biaoshi
                                        + baoliu
                                        + jiaoyi_zhuangtai
                                        + jiaoyi_liushuihao
                                        + jinzhan_zhandian
                                        + jinzhan_shijian
                                        + beiyong1
                                        + jinzhan_zhongduan_bianhao
                                        + chuzhan_zhandian
                                        + chuzhan_shijian
                                        + beiyong2
                                        + chuzhan_zhongduan_bianhao
                                        + zuida_xiaofei_jine
                                        + xingcheng_zhuangkuang
                                        + leiji_chengche_juli
                                        + yikou_feiyong
                                        + jinzhan_xianluhao
                                        + chuzhan_xianluhao
                                        + qita;
                                L.v("记录", "新拼接=" + new_r_001A_01);

                                String cmd_up_001A_01_all = cmd_up_001A_01 + new_r_001A_01;
                                String r_cmd_up_001A_01_all = BasicOper.dc_pro_commandhex(cmd_up_001A_01_all, 7);   //更新复合文件第一条记录
                                String[] rA_cmd_up_001A_01_all = r_cmd_up_001A_01_all.split("\\|", -1);
                                L.v("指令", "更新交易记录" + cmd_up_001A_01_all);
                                L.v("指令", "更新交易记录结果" + r_cmd_up_001A_01_all);

                                if (rA_cmd_up_001A_01_all[0].endsWith("0000") && rA_cmd_up_001A_01_all[1].endsWith("9000")) {
                                    //9 真正去消费
                                    String offlineTradeNumber = rA_cmd_get_tradeNumber_mac1[1].substring(0, 8); //脱机交易号
                                    String mac1 = rA_cmd_get_tradeNumber_mac1[1].substring(8, 16); //mac1
                                    String cmd_consume_two_all = cmd_consume_two + offlineTradeNumber + yymmdd + hhmmss + mac1 + "08";
                                    String r_cmd_consume_two_all = BasicOper.dc_pro_commandhex(cmd_consume_two_all, 7);  //消费去扣款第二次
                                    String[] rA_cmd_consume_two_all = r_cmd_consume_two_all.split("\\|", -1);
                                    L.v("指令", "消费第二步扣款" + cmd_consume_two_all);
                                    L.v("指令", "消费第二步扣款结果" + r_cmd_up_001A_01_all);
                                    if (rA_cmd_consume_two_all[0].endsWith("0000") && rA_cmd_consume_two_all[1].endsWith("9000")) {
                                        String tac = rA_cmd_consume_two_all[1].substring(0, 8); //交易验证码tac
                                        String mac2 = rA_cmd_consume_two_all[1].substring(8, 16); //mac2
                                        String cmd_verifym_ac2_all = cmd_verifym_ac2 + mac2;
                                        String r_cmd_verifym_ac2_all = BasicOper.dc_cpuapdu_hex(cmd_verifym_ac2_all);      //验证mac2
                                        String[] rA_cmd_verifym_ac2_all = r_cmd_verifym_ac2_all.split("\\|", -1);
                                        L.v("指令", "pasm卡验证mac2" + cmd_verifym_ac2_all);
                                        L.v("指令", "pasm卡验证mac2结果" + r_cmd_verifym_ac2_all);
                                        if (rA_cmd_verifym_ac2_all[0].equals("0000") && rA_cmd_verifym_ac2_all[1].equals("9000")) {

                                            L.v("消费金额", consumeMoney);
                                            L.v("消费金额", Integer.parseInt(consumeMoney, 16) + "");
                                            //code  bState des  换乘次数  换乘距离  应扣费用  实际扣费用
                                            String openType = "00";
                                            switch (showTipCodeEm) {
                                                case punish_already_checked_in:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.punish_already_checked_in);
                                                    break;
                                                case success_checked_in:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.success_checked_in);
                                                    break;
                                                case punish_travel_overtime:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.punish_travel_overtime);
                                                    break;
                                                case success_checked_out:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.success_checked_out);
                                                    break;
                                                case success_checked_out_free:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.success_checked_out_free);
                                                    break;
                                                case error_noset_distance:
                                                    EventBus.getDefault().post(VoiceTipCodeEm.error_noset_distance);
                                                    break;
                                            }

                                            Log.v("长度是多少", "保存开始");

                                            String finalXingcheng_zhuangkuang = xingcheng_zhuangkuang;
                                            String finalOpenType = openType;
                                            esCardDataSave.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //pos机本地流水号
                                                    String localTrade = new DecimalFormat("00000000").format(StaticData.localTradeNumber + 1);
                                                    CardSaveBean cardSaveBean = new CardSaveBean();         //保存消费数据类

                                                    cardSaveBean.setDataSerialNumber(localTrade);           //流水号
                                                    cardSaveBean.setPosBianHao(StaticData.deviceMac);              //2设置pos机编号 4字节
                                                    cardSaveBean.setTac3(tac);                              //3设置tac码 4字节
                                                    cardSaveBean.setCardId4(cardId);                        //4设置卡号 8字节
                                                    if (finalXingcheng_zhuangkuang.equals("02") || finalXingcheng_zhuangkuang.equals("03")) {
                                                        cardSaveBean.setRecordType6("02");                   //6设置 01正常记录 02换成记录
                                                    }
                                                    cardSaveBean.setPasmId7(StaticData.pasmId);                    //7设置 设置卡终端机号 7字节
                                                    cardSaveBean.setYyyymmddhhmmss9(yyyyMMddHHmmss);        //9设置时间 7字节
                                                    cardSaveBean.setPosLocalNumer10(localTrade);            //10设置 机本地流水号 4字节
                                                    cardSaveBean.setPasLocalNumer11(offlineTradeNumber);    //11设置 卡交易流水号 4字节
                                                    String shiKou = String.format("%08X", Integer.parseInt(showShikouJine));
                                                    String shiKouHex = shiKou.substring(6, 8) + shiKou.substring(4, 6) + shiKou.substring(2, 4) + shiKou.substring(0, 2);
                                                    cardSaveBean.setShiAmount12(shiKouHex);    //12设置 实扣金额    4字节
                                                    String yingKou = String.format("%08X", Integer.parseInt(showYingkouJine));
                                                    String yingKouHex = yingKou.substring(6, 8) + yingKou.substring(4, 6) + yingKou.substring(2, 4) + yingKou.substring(0, 2);
                                                    cardSaveBean.setYingAmount13(yingKouHex); //13设置 应扣金额     4字节
                                                    String yuEHex = cardBalanceHex.substring(6, 8) + cardBalanceHex.substring(4, 6) + cardBalanceHex.substring(2, 4) + cardBalanceHex.substring(0, 2);
                                                    cardSaveBean.setYuAmount14(yuEHex);             //14设置余额 4字节
                                                    cardSaveBean.setPsamCounter15(tradeNumber);             //15设置 卡片交易计数器 2字节
                                                    cardSaveBean.setCityCode16(cardMessage.substring(4, 8)); //16设置 城市代码 2字节
                                                    cardSaveBean.setHangYeCode17(cardMessage.substring(8, 12));//17设置 行业代码 2字节
                                                    cardSaveBean.setCsn18(csn);                               //18物理卡号  4字节
                                                    cardSaveBean.setCard_type21(zhuKaType);                   //21设置主卡类型 1字节
                                                    cardSaveBean.setLineNumber24(new DecimalFormat("000000").format(Integer.parseInt(StaticData.line_number))); //24设置线路号 3字节
//                                                    cardSaveBean.setCarNumber25(("30303030" + AsciiUtils.stringToAscii(new DecimalFormat("000000").format(App.line_number)))); // 25车号 10字节
                                                    cardSaveBean.setKeyIndex28(keyVersion);    //28设置 密匙索引 1字节
                                                    cardSaveBean.setKeyVersion29(algorithm);   //29设置 密匙版本 1字节
                                                    cardSaveBean.setGetCarType37(finalOpenType);   //37设置 乘车类型 1字节
                                                    cardSaveBean.setAppVerson40(String.format("%014X", StaticData.versionCode)); //40设置app版本号 7字节

                                                    SPUtils.getInstance().setLocalTradeNumber(StaticData.localTradeNumber + 1);
                                                    StaticData.localTradeNumber = StaticData.localTradeNumber + 1;
                                                    Log.v("长度是多少", cardSaveBean.getCardData().length() + "");
                                                    Log.v("长度是多少", "localTradeNumer=" + localTrade);
                                                    Log.v("长度是多少", "localTradeNumber=" + StaticData.localTradeNumber);
                                                    String detail = cardSaveBean.getCardData();
                                                    byte[] bArr = BytesUtil.hexString2Bytes(detail);
                                                    String crc16 = String.format("%04X", Integer.valueOf(CRC16.CRC16(bArr)));
                                                    String date = detail + crc16;

                                                    DbCardQrBean dbCardQrBean = new DbCardQrBean();
                                                    dbCardQrBean.setOrderNmber(localTrade);
                                                    dbCardQrBean.setbUpload(false);
                                                    dbCardQrBean.setType("1");
                                                    dbCardQrBean.setData(date);
                                                    boolean result = dbCardQrBean.save();
                                                    Log.v("保存的数据", "数据整体=" + date);
                                                    Log.v("保存的数据", cardSaveBean.getRecordType6());
                                                    //上传逻辑
//                                                    EventBus.getDefault().post(new UploadBean());

                                                }
                                            });


                                        }
                                    }

                                }
                            }

                        }

                    }

                }
            }
        }

    }

    public static String read001A_01() {
        String r_cmd_read_001A_01 = BasicOper.dc_pro_commandhex(cmd_read_001A_01, 7);
        String[] rA_cmd_read_001A_01 = r_cmd_read_001A_01.split("\\|", -1);
        L.v("指令", "读001a文件变长记录文件01记录结果" + r_cmd_read_001A_01);
        if (rA_cmd_read_001A_01[0].endsWith("0000") && rA_cmd_read_001A_01[1].endsWith("9000")) {
            String oo1A_01 = rA_cmd_read_001A_01[1];
            return oo1A_01.substring(0, oo1A_01.length() - 4);
        } else {
            return "";
        }
    }

    //计算两个时间相差的秒数
    public long getTimeDiffer(String startTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        long eTime = 0;
        long sTime = 0;
        try {
            eTime = df.parse(endTime).getTime();
            sTime = df.parse(startTime).getTime();
        } catch (ParseException e) {
        }
        long diff = (sTime - eTime) / 1000;
        return diff;
    }

    //根据距离算价格
    private Integer getDistancePrice(String distance) {  //距离string
        Integer price = 0;
        ArrayList<String> distanceAarray = new ArrayList<String>(StaticData.mapPrice.keySet());
        for (int i = 0; i < distanceAarray.size(); i++) {
            if (Integer.parseInt(distance) < Integer.parseInt(distanceAarray.get(i))) {
                price = StaticData.mapPrice.get(distanceAarray.get(i));
                return price;
            }
        }
        return StaticData.punish_amount;  //没有找到就返回 最大惩罚额度

    }

}
