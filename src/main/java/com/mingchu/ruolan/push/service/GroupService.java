package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.group.GroupCreateModel;
import com.mingchu.ruolan.push.bean.api.group.GroupMemberAddModel;
import com.mingchu.ruolan.push.bean.api.group.GroupMemberUpdateModel;
import com.mingchu.ruolan.push.bean.card.ApplyCard;
import com.mingchu.ruolan.push.bean.card.GroupCard;
import com.mingchu.ruolan.push.bean.card.GroupMemberCard;
import com.mingchu.ruolan.push.bean.card.UserCard;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by wuyinlei on 2017/6/30.
 *
 * @function 群组的接口入口
 */

@Path("/group")
public class GroupService extends BaseService {


    /**
     * 创建群
     *
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> createGroup(GroupCreateModel model) {


        return null;
    }


    /**
     * 查找群
     *
     * @param name 参数  没有传递参数代表搜索的是最近的群
     * @return 群信息的列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> searchGroup(@PathParam("name") @DefaultValue("") String name) {


        return null;
    }


    /**
     * 拉取自己当前群的列表
     *
     * @param date 时间字段  不传递  则返回全部当前的群列表  有时间 则返回这个时间之后的加入的群的
     * @return 群信息列表
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> groupList(@DefaultValue("") @PathParam("date") String date) {


        return null;
    }

    /**
     * 获取一个群的信息
     *
     * @param groupId 群Id
     * @return 群的信息
     */
    @GET
    @Path("/info/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroupInfo(@PathParam("groupId") String groupId) {


        return null;
    }


    /**
     * 拉取群成员信息  必须是成员之一才可以
     *
     * @param groupId 群Id
     * @return 群的群员的信息
     */
    @GET
    @Path("/members/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> getGroupMembers(@PathParam("groupId") String groupId) {


        return null;
    }


    /**
     * 申请加入一个群
     *
     * @param groupId 群Id   必须是群的管理者之一
     * @param model   创建的model
     * @return 返回成员列表
     */
    @POST
    @Path("/add/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> addGroupMember(@PathParam("groupId") String groupId, GroupMemberAddModel model) {

        return null;
    }

    /**
     * 更改群成员信息   可以设置别名
     *
     * @param memberId 成员id  可以查询对应的群和人   请求的人是管理员   要么是成员本人
     * @param model    修改的model
     * @return 返回成员列表  当前成员的信息
     */
    @PUT
    @Path("/update/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> notifyGroupMember(@PathParam("memberId") String memberId, GroupMemberUpdateModel model) {

        return null;
    }

    /**
     * 申请加入一个群  此时会创建一个加入的申请 并写入表   然后会给管理员发送消息
     * 管理员统一   其实就是调用添加成员的接口 把对应的用户信息添加进入
     *
     * @param groupId 群Id
     * @return 申请Card
     */
    @POST
    @Path("/apply/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {


        return null;
    }


}
