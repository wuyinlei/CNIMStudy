package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by wuyinlei on 2017/6/10.
 */
public class BaseService {

    //添加一个上下文注解  该注解会给 mSecurityContext赋值  具体的值为我们的拦截器中所返回的上下文
    @Context
    protected SecurityContext mSecurityContext;

    /**
     * 从上下文中直接获取自己的信息
     * @return User
     */
    public User getSelf() {
        return (User) mSecurityContext.getUserPrincipal();
    }


}
