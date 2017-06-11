package com.mingchu.ruolan.push.service;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.account.RegisterModel;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.user.UpdateInfoModel;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.UserFactory;

import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    //拉取联系人接口
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //拿到我的联系人
        List<User> userList = UserFactory.contacts(self);
        //转换成UserCard
        List<UserCard> userCards = userList.stream().map(
                user -> new UserCard(user, true)
        ).collect(Collectors.toList()); //map操作 相当于转置操作  User-->UserCard

        return ResponseModel.buildOk(userCards);
    }

    //关注人
    @PUT  //修改类  使用PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId){
        User self = getSelf();
        
        if (self.getId().equalsIgnoreCase(followId)){
            //自己不能关注自己   返回参数有问题
            return ResponseModel.buildParameterError();
        }

        //找到我要关注的人
        User followUser = UserFactory.findById(followId);  
        if(followId == null){
            //没有找到这个人
            return ResponseModel.buildNotFoundUserError(null);
        }
        //备注默认没有
        followUser = UserFactory.follow(self,followUser,null);
        
        if (followUser == null){
            //关注失败  服务器异常
            return ResponseModel.buildServiceError();
        }

        // TODO: 2017/6/11  
        //通知我关注的人   提示一条信息  我关注了她  
        
        //返回关注的人的信息
        return ResponseModel.buildOk(new UserCard(followUser,true));
        
        
    }

}
