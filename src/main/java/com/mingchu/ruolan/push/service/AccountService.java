package com.mingchu.ruolan.push.service;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.account.AccountRspModel;
import com.mingchu.ruolan.push.bean.api.account.LoginModel;
import com.mingchu.ruolan.push.bean.api.account.RegisterModel;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
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
public class AccountService extends BaseService {


    /**
     * 绑定的操作
     *
     * @param self   自己
     * @param pushId pushId
     * @return user
     */
    private static ResponseModel<AccountRspModel> bind(User self, String pushId) {
        //进行设备绑定的操作
        self = UserFactory.bindPushId(self, pushId);
        if (self == null) {
            return ResponseModel.buildServiceError();
        }

        //返回当前的账户   并且已经绑定了设备id
        AccountRspModel accountRspModel = new AccountRspModel(self, true);
        return ResponseModel.buildOk(accountRspModel);


    }

    @POST
    @Path("/login")  //注册接口
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    //127.0.0.1/api/account/login
    public ResponseModel<AccountRspModel> login(LoginModel model) {

        if (LoginModel.check(model))  //如果校验不成功  返回参数异常
            return ResponseModel.buildParameterError();

        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            //如果已经携带了pushId
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }

            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        } else {
            return ResponseModel.buildLoginError();
        }

    }

    @POST
    @Path("/register")  //注册接口
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    //127.0.0.1/api/account/login
    public ResponseModel<AccountRspModel> register(RegisterModel model) {

        if (RegisterModel.check(model))
            return ResponseModel.buildParameterError();

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
            //如果已经携带了pushId
            if (!Strings.isNullOrEmpty(model.getPassword())) {
                return bind(user, model.getPushId());
            }

            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        } else {
            return ResponseModel.buildRegisterError();
        }
    }

    //绑定推送id
    @POST
    @Path("/bind/{pushId}")  //绑定
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSONd
    @Produces(MediaType.APPLICATION_JSON)
    //127.0.0.1/api/account/login   //从请求头中获取token字段  pushId从Url地址中获取
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token,
                                               @PathParam("pushId") String pushId) {

        if (Strings.isNullOrEmpty(token) || Strings.isNullOrEmpty(pushId)) {
            return ResponseModel.buildParameterError();
        }

        //拿到自己的个人信息
        User user = getSelf();

        //进行设备绑定的操作
        return bind(user, pushId);

    }


}
