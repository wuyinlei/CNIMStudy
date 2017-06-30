package com.mingchu.ruolan.push.service;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.PushModel;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.user.UpdateInfoModel;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.UserFactory;
import com.mingchu.ruolan.push.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function 用户信息处理
 */
@Path("/user")
public class UserService extends BaseService {


    @PUT
    @Path("/update")  //更新接口  不需要写 就是当前目录
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

//
//        PushModel model = new PushModel();
//        model.add(new PushModel.Entity(0,"hello"));
//        //测试
//        PushDispatcher dispatcher = PushDispatcher.start();
//        dispatcher.add(self,model);
//        dispatcher.submit();

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
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();

        if (Strings.isNullOrEmpty(followId))
            ResponseModel.buildParameterError();
        if (self.getId().equalsIgnoreCase(followId)) {
            //自己不能关注自己   返回参数有问题
            return ResponseModel.buildParameterError();
        }

        //找到我要关注的人
        User followUser = UserFactory.findById(followId);
        if (followId == null) {
            //没有找到这个人
            return ResponseModel.buildNotFoundUserError(null);
        }
        //备注默认没有
        followUser = UserFactory.follow(self, followUser, null);

        if (followUser == null) {
            //关注失败  服务器异常
            return ResponseModel.buildServiceError();
        }

        // TODO: 2017/6/11  
        //通知我关注的人   提示一条信息  我关注了她

        //返回关注的人的信息
        return ResponseModel.buildOk(new UserCard(followUser, true));


    }

    //获取某人的信息
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String userId) {
        if (Strings.isNullOrEmpty(userId)) {
            //返回参数异常
            ResponseModel.buildParameterError();
        }

        User self = getSelf();

        if (self.getId().equalsIgnoreCase(userId)){
            //就是我自己  不必去查询数据库
            return ResponseModel.buildOk(new UserCard(self,true));
        }

        User user = UserFactory.findById(userId);
        if (user == null){
            return ResponseModel.buildNotFoundUserError(null);
        }

        //如果我们直接有关注的记录   则我已经关注了我查询的用户
        boolean isFollow = UserFactory.getUserFollow(self,user) != null;

        return ResponseModel.buildOk(new UserCard(user,isFollow));

    }

    //搜索人的接口实现
    @GET  //不涉及数据更改  只是查询数据库
    @Path("/search/{name:(.*)?}")  //名字是任意字符 可以为空
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> searchUser(@DefaultValue("") @PathParam("name") String name){

        //拿到自己
        User self = getSelf();

        //先查询数据
        List<User> searchUsers = UserFactory.search(name);
        //把查询的人封装成UserCard  判断这些人是否有我已经关注的人  如果有  则应该设置好关注状态
       final List<User> following = UserFactory.contacts(self);  //拿出的联系人  关注列表

        //把User-->UserCard
        List<UserCard> userCards = searchUsers.stream()
                .map(
                        user -> {
                            //判断这个人是否是我自己  或者是我联系人中的人
                            boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                                    || following.stream().
                                    //进行联系人的任意匹配
                                    anyMatch(
                                            contactUser -> contactUser.getId().equalsIgnoreCase(user.getId())
                                    ); //关注状态

                            return new UserCard(user,isFollow);
                        }
                ).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

}
