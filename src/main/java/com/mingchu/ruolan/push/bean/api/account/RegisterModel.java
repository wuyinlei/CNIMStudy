package com.mingchu.ruolan.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function 注册Model
 */
public class RegisterModel {

    @Expose
    private String account;  //账户  也就是用户手机号
    @Expose
    private String password;  //秘密
    @Expose
    private String name;  //名字

    @Expose
    private String pushId;

    /**
     * 校验
     * @param model  RegisterModel
     * @return  false  true
     */
    public static boolean check(RegisterModel model) {

        return model != null
                && Strings.isNullOrEmpty(model.account)
                && Strings.isNullOrEmpty(model.password)
                && Strings.isNullOrEmpty(model.name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

}
