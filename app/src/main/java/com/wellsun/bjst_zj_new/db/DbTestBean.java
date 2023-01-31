package com.wellsun.bjst_zj_new.db;

import org.litepal.crud.LitePalSupport;

/**
 * date     : 2023-01-31
 * author   : ZhaoZheng
 * describe :
 */
public class DbTestBean extends LitePalSupport {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "DbTestBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    int id;
    String name;

}
