package com.wellsun.bjst_zj_new.em;

import com.wellsun.bjst_zj_new.R;

/**
 * date     : 2023-02-03
 * author   : ZhaoZheng
 * describe :
 */
public enum VoiceTipCodeEm {
    error_read_wallet_fail(8, R.drawable.welcome, "读电子钱包失败"),
    error_noset_distance(11, R.drawable.error_distance_noset, "未找到票价"),
    warn_cardLocked(1, R.drawable.error_card_locked, "卡片锁定"),
    warn_stationLocked(2, R.drawable.welcome, "闸机锁定"),
    warn_manageCard(3, R.drawable.welcome, "管理卡"),
    warn_card_no_enable(4, R.drawable.error_card_no_enable, "卡片未启用"),
    warn_set_date(5, R.drawable.error_set_date, "请设置日期"),
    warn_invalid_card(6, R.drawable.error_invalid_card, "失效过期卡"),
    warn_card_black(7, R.drawable.error_card_blocked, "黑名单卡"),
    warn_low_balance(9, R.drawable.worn_no_balance, "余额不足"),
    warn_already_checked_in(10, R.drawable.worn_already_checked_in, "已经进站"),
    warn_already_checked_out(11, R.drawable.worn_already_checked_out, "已经出站"),
    punish_travel_overtime(10, R.drawable.punish_travel_overtime, "旅行超时"),
    punish_already_checked_in(10, R.drawable.worn_already_checked_in, "重复进站"),
    punish_already_checked_out(11, R.drawable.worn_already_checked_out, "重复出站"),
    success_checked_in(10, R.drawable.success_in_bus, "请进站"),
    success_checked_out_free(11, R.drawable.success_out_bus, "请出站"),
    success_checked_out(11, R.drawable.success_out_bus, "请出站");


    private int code;
    private int src;
    private String tip;

    VoiceTipCodeEm(int i, int src, String tip) {
        this.code = i;
        this.src = src;
        this.tip = tip;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
