package com.mingchu.ruolan.push.bean.api.account;

import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.User;

/**
 * Created by wuyinlei on 2017/6/10.
 * <p>
 * 账户返回的Model
 */
public class AccountRspModel {
    //用户基本信息
    @Expose
    private UserCard user;
    //当前登录的账号
    @Expose
    private String account;
    //当前登录成功后获取的token   可以通过token获取用户所有的信息
    @Expose
    private String token;
    //标识是否已经绑定到了设备pushId上
    @Expose
    private boolean isBind;

    public AccountRspModel(User user, boolean isBind) {
        this.user = new UserCard(user);
        this.account = user.getPhone();
        this.token = user.getToken();
        this.isBind = isBind;
    }

    public AccountRspModel(User user) {
        this(user, false);
    }

    public UserCard getUser() {
        return user;
    }

    public void setUser(UserCard user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
