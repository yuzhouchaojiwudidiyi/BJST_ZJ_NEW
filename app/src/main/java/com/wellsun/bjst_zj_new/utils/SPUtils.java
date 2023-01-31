package com.wellsun.bjst_zj_new.utils;


import com.wellsun.bjst_zj_new.base.BasePreference;

/**
 * 储存工具类
 * content:
 * time: 2020/10/21
 *
 * @author: ZhaoZheng
 */
public class SPUtils extends BasePreference {
    private static SPUtils spUtils;

    public synchronized static SPUtils getInstance() {
        if (null == spUtils) {
            spUtils = new SPUtils();
        }
        return spUtils;
    }

    /**
     * 需要增加key就在这里新建
     */
    //用户名的key
    private static final String USER_NAME = "user_name";
    private static final String REMEBER_PAASS_WORD = "remeber_pass_word";


    /**
     * 账号
     *
     * @return
     */
    public String getUSER_NAME() {
        return getString(USER_NAME);
    }

    public void setUSER_NAME(String user_name) {
        setString(USER_NAME, user_name);
    }


    /**
     * 是否记住密码
     *
     * @return
     */
    public boolean getREMEBER_PAASS_WORD() {
        return getBoolean(REMEBER_PAASS_WORD);
    }

    public void setREMEBER_PAASS_WORD(Boolean remeber_pass_word) {
        setBoolean(REMEBER_PAASS_WORD, remeber_pass_word);
    }

    public void clear() {
        sp.edit().clear();
        sp.edit().commit();
    }


}
