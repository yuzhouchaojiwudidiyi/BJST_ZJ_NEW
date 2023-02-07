package com.wellsun.bjst_zj_new.dbsqlit;

import org.litepal.crud.LitePalSupport;

/**
 * date     : 2022-09-05
 * author   : ZhaoZheng
 * describe :
 */
public class DbCardQrBean extends LitePalSupport {
    private int id;          //id
    private String orderNmber;//订单号
    private boolean bUpload; //是否上传
    private String type;     //类型
    private String data;     //内容

    public DbCardQrBean() {
    }

    public DbCardQrBean(String orderNmber, boolean bUpload, String type, String data) {
        this.orderNmber = orderNmber;
        this.bUpload = bUpload;
        this.type = type;
        this.data = data;
    }

    public String getOrderNmber() {
        return orderNmber;
    }

    public void setOrderNmber(String orderNmber) {
        this.orderNmber = orderNmber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isbUpload() {
        return bUpload;
    }

    public void setbUpload(boolean bUpload) {
        this.bUpload = bUpload;
    }


    @Override
    public String toString() {
        return "DbCardQrBean{" +
                "id=" + id +
                ", orderNmber='" + orderNmber + '\'' +
                ", bUpload=" + bUpload +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

}
