package com.haotian.setdbtblp;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HAOTIAN on 2016/3/12.
 */
@Table(name ="tblP")
public class TblP {
    @Column(name= "id",isId = true)
    private int id;
    @Column(name = "usercode")
    private String usercode;
    @Column(name = "password")
    private String password;

    public int getId() {
        return id;
    }

    public String getUsercode() {
        return usercode;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TblP{" +
                "id=" + id +
                ", usercode='" + usercode + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
