package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.api.account.RegisterModel;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.UserFactory;

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


    @POST
    @Path("/register")  //注册接口
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    //127.0.0.1/api/account/login
    public UserCard register(RegisterModel model) {

        User user = UserFactory.register(model.getAccount(),
                model.getPassword(), model.getName());

        if (user != null) {
            UserCard card = new UserCard();
            card.setName(user.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setIsFollow(true);  //自己本身已经关注
            card.setModifyAt(user.getUpdateAt());
            return card;
        }
        return null;  //这里如果直接返回 是错误的  没有指定返回的类型 xml  gson
    }


}
