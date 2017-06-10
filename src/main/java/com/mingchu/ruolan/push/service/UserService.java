package com.mingchu.ruolan.push.service;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.user.UpdateInfoModel;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function 用户信息处理
 */
@Path("/user")
public class UserService extends BaseService {


    @PUT
//    @Path("/update")  //注册接口  不需要写 就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(
            UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        //拿到自己的个人信息
        User user = getSelf();

        //进行设备绑定的操作
        user = model.updateToUser(user);
        UserFactory.update(user);
        UserCard card = new UserCard(user, true);
        return ResponseModel.buildOk(card);


    }

}
