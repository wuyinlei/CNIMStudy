package com.mingchu.ruolan.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * Created by wuyinlei on 2017/6/10.
 */
public class LoginModel {

    @Expose
    private String account;  //账户
    @Expose
    private String password;  //秘密吗

    /**
     * 校验
     * @param model  LoginModel
     * @return  false  true
     */
    public static boolean check(LoginModel model) {

        return model != null
                && Strings.isNullOrEmpty(model.account)
                && Strings.isNullOrEmpty(model.password);
    }

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

}
