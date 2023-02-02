package com.wellsun.bjst_zj_new.readcard;

import android.content.Context;

import com.decard.NDKMethod.BasicOper;
import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.data.StaticData;
import com.wellsun.bjst_zj_new.utils.L;
import com.wellsun.bjst_zj_new.utils.ToastPrint;

/**
 * date     : 2022-08-12
 * author   : ZhaoZheng
 * describe :
 */
public class Cmd {
    //    dc_cpuapdu_hex     cpu卡
    //    dc_pro_commandhex  非接触卡
    public static String consumeMoney = "00000000";    //消费金额
    public static String cardId = "0";  //卡号 16位
    public static String cmd_choose_sam1 = "00A40000023F00";              //选择sam1卡应用

    /**
     * pasm指令
     */
    public static String cmd_choose_pasm_application = "00a40000021001";  //pasm选择应用
    public static String cmd_getpasmid = "00B0960006";                    //获取终端机号
    public static String cmd_get_tradenumber_mac1 = "00C0000008";         //4 字节的终端脱机交易序号 + 4字节mac1
    public static String cmd_verifym_ac2 = "8072000004";                  // 验证mac2

    /**
     * ic卡指令
     */
    public static String consumeType = "09";                   //消费类型 09复合消费
    public static String cmd_choose_3f01 = "00A40000023F01";  //选择3f01电子钱包应用
    public static String cmd_consume_one = "805003020B01";    //复合消费初始化第一步
    public static String cmd_calculate_mac1 = "807000001C";   //pasm卡请求 计算mac1
    public static String cmd_read_001A_01 = "00B201D000";     //变成记录文件 复合交易文件 公交第一条记录
    public static String cmd_up_001A_01 = "80DC01D078";       //更新复合文件 第一条数据
    public static String cmd_consume_two = "805401000F";      //复合消费初始化第二步 扣款

    public static void connectD8(Context context) {
        //向系统申请使用USB权限,此过程为异步,建议放在程序启动时调用。 返回0请求权限
        int iReqPermission = BasicOper.dc_AUSB_ReqPermission(context);
        //打开端口，usb模式，打开之前必须确保已经获取到USB权限，返回值为设备句柄号。 //成功返回180
        int devHandle = BasicOper.dc_open("AUSB", context, "", 0);
        if (devHandle > 0) {  //sam卡异常
            StaticData.readCardState = true;
            initSam();
        }
    }

    public static void initSam() {
        //第一步设置卡座
        String r_kaZuo = BasicOper.dc_setcpu(2); //2表示sim1卡 3表示sim2卡
        String[] rA_kazuo = r_kaZuo.split("\\|", -1);
        if (rA_kazuo[0].equals("0000")) {
            //第二步设置参数
            String r_canShu = BasicOper.dc_setcpupara(2, 0x00, 0x5C);  //2表示sim1卡   卡协议编号，0x00表示T0，0x01表示T1，默认为0x00 卡复位波特率编号，0x5C表示9600，0x14表示38400
            String[] rA_canshu = r_canShu.split("\\|", -1);
            if (rA_canshu[0].equals("0000")) {
                //第三步 复位
                String r_fuWei = BasicOper.dc_cpureset_hex();
                String[] rA_fuWei = r_fuWei.split("\\|", -1);
                if (rA_fuWei[0].equals("0000")) {
                    //第四步 获取终端机编号
                    String r_cmd_getpasmid = BasicOper.dc_cpuapdu_hex(cmd_getpasmid);
                    String[] rA_cmd_getpasmid = r_cmd_getpasmid.split("\\|", -1);
                    L.v("指令", "获取终端机指令=" + cmd_getpasmid);
                    L.v("指令", "获取终端机指令结果" + r_cmd_getpasmid);
                    if (rA_cmd_getpasmid[0].equals("0000") && rA_cmd_getpasmid[1].endsWith("9000")) {
                        StaticData.pasmId = rA_cmd_getpasmid[1].substring(0, 12);
                        //第五步 选择sim1卡
                        String r_sim1 = BasicOper.dc_cpuapdu_hex(cmd_choose_sam1);
                        String[] rt_sim1 = r_sim1.split("\\|", -1);
                        L.v("指令", "选择sim1卡=" + cmd_choose_sam1);
                        L.v("指令", "选择sim1卡结果" + r_sim1);
                        if (rt_sim1[0].equals("0000") && rt_sim1[1].equals("6117")) {  //6017
                            //第六步 选择pasm卡选择应用
                            String r_cmd_choose_pasm_application = BasicOper.dc_cpuapdu_hex(cmd_choose_pasm_application); //0000|6110
                            String[] rA_cmd_choose_pasm_application = r_cmd_choose_pasm_application.split("\\|", -1);
                            L.v("指令", "选择sim1卡应用=" + cmd_choose_pasm_application);
                            L.v("指令", "选择sim1卡应用结果" + r_cmd_choose_pasm_application);
                            if (rA_cmd_choose_pasm_application[0].equals("0000") && rA_cmd_choose_pasm_application[1].equals("6110")) {
                                ToastPrint.showView("读卡器连接成功");
                                StaticData.samCardState = true;
                            }
                        }
                    }
                }
            }
        }
    }

}
