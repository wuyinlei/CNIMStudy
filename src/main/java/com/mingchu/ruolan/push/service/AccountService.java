package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.api.account.AccountRspModel;
import com.mingchu.ruolan.push.bean.api.account.RegisterModel;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
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
    public ResponseModel<AccountRspModel> register(RegisterModel model) {

        User user = UserFactory.findByPhone(model.getName());

        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }

        user = UserFactory.findByPhone(model.getAccount());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }

        //开始注册逻辑
        user = UserFactory.register(model.getAccount(),
                model.getPassword(), model.getName());

        if (user != null) {
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        } else {
            return ResponseModel.buildRegisterError();
        }
    }
}
