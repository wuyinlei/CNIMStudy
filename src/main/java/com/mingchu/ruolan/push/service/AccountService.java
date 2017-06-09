package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by wuyinlei on 2017/6/4.
 *
 * @function
 */
// 127.0.0.1/api/account/...
@Path("/account")
public class AccountService {

    @GET
    @Path("/login")
    //127.0.0.1/api/account/login
    public String get(){
       return "You get the login";
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    //127.0.0.1/api/account/login
    public User getUser(){
        User user = new User();
        user.setName("美女");
        user.setSex(29);
        return user;  //这里如果直接返回 是错误的  没有指定返回的类型 xml  gson
    }
}
