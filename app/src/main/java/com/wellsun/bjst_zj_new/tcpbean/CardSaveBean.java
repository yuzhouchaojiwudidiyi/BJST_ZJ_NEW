package com.wellsun.bjst_zj_new.tcpbean;

import com.wellsun.bjst_zj_new.base.App;
import com.wellsun.bjst_zj_new.data.StaticData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date     : 2022-09-07
 * author   : ZhaoZheng
 * describe : 卡消费数据上传
 */
public class CardSaveBean {
    String xieYiLeiXing = "01"; //协议类型            1
    String xieYiDaiMa = "0004"; //协议代码            2
    String posBianHao = StaticData.deviceMac;    //设备编号     4
    //    String posBianHao = "88602290"; //设备编号     4
//    String liuLiangKaHao = "0000000000000000000000000000000000000000"; //流量卡号  20
    String liuLiangKaHao = "3030303030303030303030303030303030303030"; //流量卡号  20
    String shuJuChangDu = "010C";         //数据长度   2
    /**
     * 上面总共29字节  下面为具体内容268字节
     */
//    String CorpId = "000000000001";       //公司代码 6字节  bcd
    String CorpId =StaticData.pasmId;              //公司代码 6字节  bcd
    String DataSerialNumber = "00000000";   //流水号 4字节 bcd
    String StopNumber = String.format("%04X", Integer.parseInt(StaticData.station_name)); //站点编号 2字节
    //下面为具体拼接内容  268-12 = 256字节
    String recordTag1 = "F0";              //记录标识 1字节
    String posNumber2 = StaticData.deviceMac;     // 设备唯一编号  4字节
    String tac3 = "00000000";              //tac码 4字节
    String cardId4 = "0000000000000000";   //应用序列号  8字节
    String tradeType5 = "09";              //06普通交易；09复合交易  1字节
    String recordType6 = "01";             // 正常记录01   换乘记录  02
    String pasmId7 = StaticData.pasmId;           //PSAM卡终端机编号 PSAM卡终端机编号  6字节
    String deviceType8 = "000001";         //闸机000001  3字节
    String yyyymmddhhmmss9 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //交易日期时间 7字节 bcd码
    String posLocalNumer10 = "00000000";   //POS机本地流水号  4字节
    String pasLocalNumer11 = "00000000";   //PSAM卡交易流水号  4字节
    String shiAmount12 = "00000000";       //本次交易金额单位：分  低字节在前 如1元：64000000   4字节  实扣
    String yingAmount13 = "00000000";      //优惠前应扣金额：分  低字节在前 如1元：64000000   4字节 应扣
    String yuAmount14 = "00000000";        //卡交易前余额：分  低字节在前 如1元：64000000   4字节  卡交易前余额
    String psamCounter15 = "0000";         //卡片交易计数器  2字节
    String cityCode16 = "0000";            //城市代码 2字节
    String hangYeCode17 = "0000";           //行业代码 2字节
    String csn18 = "00000000";              //物理卡号 4字节
    String card_in_version19 = "01";        //卡内版本 1字节
    String card_shape20 = "00";             //卡型标志 1字节
    String card_type21 = "00";              //主卡类型 1字节
    String record_success_gray22 = "01";    //0x01正常记录 0x02灰记录 1字节
    String lastRecord23 = "0000000000000000000000000000000000000000000000";//上次交易记录 23字节
    String lineNumber24 = "000000";              //线路号  3字节                   bcd
//    String carNumber25 = "00000000000000000000"; //车号  10字节    Ascii
    String carNumber25 = "30303030303030303030"; //车号  10字节    Ascii
    String yuanGong26 = "00000000";              // 员工号 4字节
    String cardIdFirst27 = "2140";               //  卡应用序列化前两个字节  2字节
    String keyIndex28 = "00";                     // 密匙索引  1字节
    String keyVersion29 = "01";                   // 密匙版本  1字节
    String sendCard30 = "0000000000000000";        // 发卡机构  8字节
    String pasmVerson31 = "01";                      //PSAM卡密钥版本 1字节
//    String stationNumber32 = String.format("%08X", App.station_name); //当前车站编号 4字节
    String stationNumber32 = new DecimalFormat("00000000").format(Integer.parseInt(StaticData.station_name)); //当前车站编号 4字节
    String longitude33 = "00000000";                 //经度 4字节
    String latitude34 = "00000000";                  //纬度 4字节
    String upDown35 = "00";                          //上下行 1字节  00上行 01下行
    String doorTag36 = "000000000000";               //车号带前后门标记（预留 6字节
    String getCarType37 = "00";                     //乘车类型  进站/上车00，出站/下车01，补扣进站 02，补扣出站 03
    String makeUpLineNumebr38 = "0000";            // 补扣线路号  2字节
    String makeUpCardNumebr39 = "000000000000";    // 补扣车号  6字节
    String appVerson40 = "00000000000000";         // 补扣车号  7字节
    String gpsInfo41 = "0000000000000000000000";   // GPS设备信息  11字节
    String xieYiCard42 = "00";                      // 未开通00 未开通00 季卡  02 周卡  03 折扣1 04 折扣2 05
    String weekCard43 = "00";                       //周卡 非周卡00 周卡第一周01 周卡第二周02 周卡第三周03  周卡第四周04
    String driverCardLength44 = "0004";             // 司机卡长度  2字节
    String driverCardNumber45 = "BFFFFFFD";             //司机卡号    8位
    String bu = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";


    public String getCardData() {
        String cardData = xieYiLeiXing + xieYiDaiMa + posBianHao + liuLiangKaHao + shuJuChangDu + CorpId + DataSerialNumber + StopNumber +
                recordTag1 + posNumber2 + tac3 + cardId4 + tradeType5 + recordType6 + pasmId7 + deviceType8 + yyyymmddhhmmss9 + posLocalNumer10
                + pasLocalNumer11 + shiAmount12 + yingAmount13 + yuAmount14 + psamCounter15 + cityCode16 + hangYeCode17 + csn18 + card_in_version19
                + card_shape20 + card_type21 + record_success_gray22 + lastRecord23 + lineNumber24 + carNumber25 + yuanGong26 + cardIdFirst27
                + keyIndex28 + keyVersion29 + sendCard30 + pasmVerson31 + stationNumber32 + longitude33 + latitude34 + upDown35 + doorTag36 + getCarType37
                + makeUpLineNumebr38 + makeUpCardNumebr39 + appVerson40 + gpsInfo41 + xieYiCard42 + weekCard43 + driverCardLength44 + driverCardNumber45 + bu;
        return cardData;
    }


    public String getXieYiLeiXing() {
        return xieYiLeiXing;
    }

    public void setXieYiLeiXing(String xieYiLeiXing) {
        this.xieYiLeiXing = xieYiLeiXing;
    }

    public String getXieYiDaiMa() {
        return xieYiDaiMa;
    }

    public void setXieYiDaiMa(String xieYiDaiMa) {
        this.xieYiDaiMa = xieYiDaiMa;
    }

    public String getPosBianHao() {
        return posBianHao;
    }

    public void setPosBianHao(String posBianHao) {
        this.posBianHao = posBianHao;
    }

    public String getLiuLiangKaHao() {
        return liuLiangKaHao;
    }

    public void setLiuLiangKaHao(String liuLiangKaHao) {
        this.liuLiangKaHao = liuLiangKaHao;
    }

    public String getShuJuChangDu() {
        return shuJuChangDu;
    }

    public void setShuJuChangDu(String shuJuChangDu) {
        this.shuJuChangDu = shuJuChangDu;
    }

    public String getCorpId() {
        return CorpId;
    }

    public void setCorpId(String corpId) {
        CorpId = corpId;
    }

    public String getDataSerialNumber() {
        return DataSerialNumber;
    }

    public void setDataSerialNumber(String dataSerialNumber) {
        DataSerialNumber = dataSerialNumber;
    }

    public String getStopNumber() {
        return StopNumber;
    }

    public void setStopNumber(String stopNumber) {
        StopNumber = stopNumber;
    }

    public String getRecordTag1() {
        return recordTag1;
    }

    public void setRecordTag1(String recordTag1) {
        this.recordTag1 = recordTag1;
    }

    public String getPosNumber2() {
        return posNumber2;
    }

    public void setPosNumber2(String posNumber2) {
        this.posNumber2 = posNumber2;
    }

    public String getTac3() {
        return tac3;
    }

    public void setTac3(String tac3) {
        this.tac3 = tac3;
    }

    public String getCardId4() {
        return cardId4;
    }

    public void setCardId4(String cardId4) {
        this.cardId4 = cardId4;
    }

    public String getTradeType5() {
        return tradeType5;
    }

    public void setTradeType5(String tradeType5) {
        this.tradeType5 = tradeType5;
    }

    public String getRecordType6() {
        return recordType6;
    }

    public void setRecordType6(String recordType6) {
        this.recordType6 = recordType6;
    }

    public String getPasmId7() {
        return pasmId7;
    }

    public void setPasmId7(String pasmId7) {
        this.pasmId7 = pasmId7;
    }

    public String getDeviceType8() {
        return deviceType8;
    }

    public void setDeviceType8(String deviceType8) {
        this.deviceType8 = deviceType8;
    }

    public String getYyyymmddhhmmss9() {
        return yyyymmddhhmmss9;
    }

    public void setYyyymmddhhmmss9(String yyyymmddhhmmss9) {
        this.yyyymmddhhmmss9 = yyyymmddhhmmss9;
    }

    public String getPosLocalNumer10() {
        return posLocalNumer10;
    }

    public void setPosLocalNumer10(String posLocalNumer10) {
        this.posLocalNumer10 = posLocalNumer10;
    }

    public String getPasLocalNumer11() {
        return pasLocalNumer11;
    }

    public void setPasLocalNumer11(String pasLocalNumer11) {
        this.pasLocalNumer11 = pasLocalNumer11;
    }

    public String getShiAmount12() {
        return shiAmount12;
    }

    public void setShiAmount12(String shiAmount12) {
        this.shiAmount12 = shiAmount12;
    }

    public String getYingAmount13() {
        return yingAmount13;
    }

    public void setYingAmount13(String yingAmount13) {
        this.yingAmount13 = yingAmount13;
    }

    public String getYuAmount14() {
        return yuAmount14;
    }

    public void setYuAmount14(String yuAmount14) {
        this.yuAmount14 = yuAmount14;
    }

    public String getPsamCounter15() {
        return psamCounter15;
    }

    public void setPsamCounter15(String psamCounter15) {
        this.psamCounter15 = psamCounter15;
    }

    public String getCityCode16() {
        return cityCode16;
    }

    public void setCityCode16(String cityCode16) {
        this.cityCode16 = cityCode16;
    }

    public String getHangYeCode17() {
        return hangYeCode17;
    }

    public void setHangYeCode17(String hangYeCode17) {
        this.hangYeCode17 = hangYeCode17;
    }

    public String getCsn18() {
        return csn18;
    }

    public void setCsn18(String csn18) {
        this.csn18 = csn18;
    }

    public String getCard_in_version19() {
        return card_in_version19;
    }

    public void setCard_in_version19(String card_in_version19) {
        this.card_in_version19 = card_in_version19;
    }

    public String getCard_shape20() {
        return card_shape20;
    }

    public void setCard_shape20(String card_shape20) {
        this.card_shape20 = card_shape20;
    }

    public String getCard_type21() {
        return card_type21;
    }

    public void setCard_type21(String card_type21) {
        this.card_type21 = card_type21;
    }

    public String getRecord_success_gray22() {
        return record_success_gray22;
    }

    public void setRecord_success_gray22(String record_success_gray22) {
        this.record_success_gray22 = record_success_gray22;
    }

    public String getLastRecord23() {
        return lastRecord23;
    }

    public void setLastRecord23(String lastRecord23) {
        this.lastRecord23 = lastRecord23;
    }

    public String getLineNumber24() {
        return lineNumber24;
    }

    public void setLineNumber24(String lineNumber24) {
        this.lineNumber24 = lineNumber24;
    }

    public String getCarNumber25() {
        return carNumber25;
    }

    public void setCarNumber25(String carNumber25) {
        this.carNumber25 = carNumber25;
    }

    public String getYuanGong26() {
        return yuanGong26;
    }

    public void setYuanGong26(String yuanGong26) {
        this.yuanGong26 = yuanGong26;
    }

    public String getCardIdFirst27() {
        return cardIdFirst27;
    }

    public void setCardIdFirst27(String cardIdFirst27) {
        this.cardIdFirst27 = cardIdFirst27;
    }

    public String getKeyIndex28() {
        return keyIndex28;
    }

    public void setKeyIndex28(String keyIndex28) {
        this.keyIndex28 = keyIndex28;
    }

    public String getKeyVersion29() {
        return keyVersion29;
    }

    public void setKeyVersion29(String keyVersion29) {
        this.keyVersion29 = keyVersion29;
    }

    public String getSendCard30() {
        return sendCard30;
    }

    public void setSendCard30(String sendCard30) {
        this.sendCard30 = sendCard30;
    }

    public String getPasmVerson31() {
        return pasmVerson31;
    }

    public void setPasmVerson31(String pasmVerson31) {
        this.pasmVerson31 = pasmVerson31;
    }

    public String getStationNumber32() {
        return stationNumber32;
    }

    public void setStationNumber32(String stationNumber32) {
        this.stationNumber32 = stationNumber32;
    }

    public String getLongitude33() {
        return longitude33;
    }

    public void setLongitude33(String longitude33) {
        this.longitude33 = longitude33;
    }

    public String getLatitude34() {
        return latitude34;
    }

    public void setLatitude34(String latitude34) {
        this.latitude34 = latitude34;
    }

    public String getUpDown35() {
        return upDown35;
    }

    public void setUpDown35(String upDown35) {
        this.upDown35 = upDown35;
    }

    public String getDoorTag36() {
        return doorTag36;
    }

    public void setDoorTag36(String doorTag36) {
        this.doorTag36 = doorTag36;
    }

    public String getGetCarType37() {
        return getCarType37;
    }

    public void setGetCarType37(String getCarType37) {
        this.getCarType37 = getCarType37;
    }

    public String getMakeUpLineNumebr38() {
        return makeUpLineNumebr38;
    }

    public void setMakeUpLineNumebr38(String makeUpLineNumebr38) {
        this.makeUpLineNumebr38 = makeUpLineNumebr38;
    }

    public String getMakeUpCardNumebr39() {
        return makeUpCardNumebr39;
    }

    public void setMakeUpCardNumebr39(String makeUpCardNumebr39) {
        this.makeUpCardNumebr39 = makeUpCardNumebr39;
    }

    public String getAppVerson40() {
        return appVerson40;
    }

    public void setAppVerson40(String appVerson40) {
        this.appVerson40 = appVerson40;
    }

    public String getGpsInfo41() {
        return gpsInfo41;
    }

    public void setGpsInfo41(String gpsInfo41) {
        this.gpsInfo41 = gpsInfo41;
    }

    public String getXieYiCard42() {
        return xieYiCard42;
    }

    public void setXieYiCard42(String xieYiCard42) {
        this.xieYiCard42 = xieYiCard42;
    }

    public String getWeekCard43() {
        return weekCard43;
    }

    public void setWeekCard43(String weekCard43) {
        this.weekCard43 = weekCard43;
    }

    public String getDriverCardLength44() {
        return driverCardLength44;
    }

    public void setDriverCardLength44(String driverCardLength44) {
        this.driverCardLength44 = driverCardLength44;
    }

    public String getDriverCardNumber45() {
        return driverCardNumber45;
    }

    public void setDriverCardNumber45(String driverCardNumber45) {
        this.driverCardNumber45 = driverCardNumber45;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }


}
