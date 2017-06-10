package com.mingchu.ruolan.push.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function 注册Model
 */
public class RegisterModel {

    @Expose
    private String account;  //账户
    @Expose
    private String password;  //秘密吗
    @Expose
    private String name;  //名字

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
