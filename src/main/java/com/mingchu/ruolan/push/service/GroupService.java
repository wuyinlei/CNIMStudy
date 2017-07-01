package com.mingchu.ruolan.push.service;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.group.GroupCreateModel;
import com.mingchu.ruolan.push.bean.api.group.GroupMemberAddModel;
import com.mingchu.ruolan.push.bean.api.group.GroupMemberUpdateModel;
import com.mingchu.ruolan.push.bean.card.ApplyCard;
import com.mingchu.ruolan.push.bean.card.GroupCard;
import com.mingchu.ruolan.push.bean.card.GroupMemberCard;
import com.mingchu.ruolan.push.bean.db.Group;
import com.mingchu.ruolan.push.bean.db.GroupMember;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.GroupFactory;
import com.mingchu.ruolan.push.factory.PushFactory;
import com.mingchu.ruolan.push.factory.UserFactory;
import com.mingchu.ruolan.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //创建者
        User creator = getSelf();
        //移除  不在列表中
        model.getUsers().remove(creator.getId());

        if (model.getUsers().size() == 0) {
            ResponseModel.buildParameterError();
        }

        //检查是否已经有这个名字
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }

        List<User> users = new ArrayList<>();
        for (String userId : model.getUsers()) {
            User user = UserFactory.findById(userId);
            if (user == null)
                continue;

            users.add(user);
        }

        //没有一个成员
        if (users.size() == 0)
            ResponseModel.buildParameterError();

        Group group = GroupFactory.create(creator, model, users);

        if (group == null)
            return ResponseModel.buildServiceError();

        //拿群的管理员
        GroupMember createMember = GroupFactory.getMember(creator.getId(), group.getId());
        if (createMember == null)
            return ResponseModel.buildServiceError();

        //拿到群的成员  给所有的群成员发送信息  已经被添加到群的信息
        Set<GroupMember> groupMembers = GroupFactory.getMembers(group);

        if (groupMembers == null && groupMembers.size() == 0)
            return ResponseModel.buildServiceError();
        groupMembers = groupMembers.stream()
                .filter(
                        groupMember ->
                                !groupMember.getId().equalsIgnoreCase(createMember.getId())
                ).collect(Collectors.toSet());

        //开始发起推送
        PushFactory.pusGroupAdd(groupMembers);


        return ResponseModel.buildOk(new GroupCard(createMember));
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

        User user = getSelf();

        List<Group> groups = GroupFactory.search(name);

        if (groups != null && groups.size() > 0) {

            List<GroupCard> groupCards = groups.stream()
                    .map(group -> {

                        GroupMember member = GroupFactory.getMember(user.getId(), group.getId());

                        return new GroupCard(group, member);

                    }).collect(Collectors.toList());

            return ResponseModel.buildOk(groupCards);
        }

        return ResponseModel.buildOk();
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

        User self = getSelf();

        //拿时间
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(date)) {
            try {
                dateTime = LocalDateTime.parse(date, LocalDateTimeConverter.FORMATTER);

            } catch (Exception e) {
                dateTime = null;
            }
        }

        Set<GroupMember> members = GroupFactory.getMembers(self);

        if (members == null || members.size() == 0)
            return ResponseModel.buildOk();

        final LocalDateTime finalDateTime = dateTime;

        List<GroupCard> groupCards = members.stream()
                .filter(groupMember -> finalDateTime == null //时间如果为null  则不做限制
                        || groupMember.getUpdateAt().isAfter(finalDateTime))  //或者说时间不为null  你需要在我的这个时间之后
                .map(GroupCard::new)  //转换操作
                .collect(Collectors.toList());  //转换成list


        return ResponseModel.buildOk(groupCards);
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

        if (Strings.isNullOrEmpty(groupId))
            return ResponseModel.buildParameterError();

        User self = getSelf();

        GroupMember member = GroupFactory.getMember(self.getId(), groupId);

        if (member == null)
            return ResponseModel.buildNotFoundGroupError(null);

        return ResponseModel.buildOk(new GroupCard(member));
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

        User self = getSelf();

        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);

        if (selfMember == null)
            return ResponseModel.buildNoPermissionError();

        //拉取群的成员
        Set<GroupMember> members = GroupFactory.getMembers(group);

        if (members == null)
            return ResponseModel.buildServiceError();

        List<GroupMemberCard> groupMemberCards = members
                .stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        return ResponseModel.buildOk(groupMemberCards);
    }


    /**
     * 添加一个人到群
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


        if (Strings.isNullOrEmpty(groupId) && !GroupMemberAddModel.check(model))
            return ResponseModel.buildParameterError();

        User self = getSelf();

        model.getUsers().remove(self.getId());  //移除自己

        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();

        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);

        //必须是成员  同时也必须是管理员级别以及以上
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.NOTIFY_LEVEL_NONE)
            return ResponseModel.buildNoPermissionError();

        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        if (oldMembers == null && oldMembers.size() == 0)
            return ResponseModel.buildNotFoundGroupError(null);

        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());

        List<User> insertUsers = new ArrayList<>();


        for (String userId : model.getUsers()) {
            User user = UserFactory.findById(userId);
            if (user == null)
                continue;

            if (oldMemberUserIds.contains(user.getId()))
                continue;

            insertUsers.add(user);
        }

        if (insertUsers.size() == 0)
            return ResponseModel.buildParameterError();

        Set<GroupMember> insertMembers = GroupFactory.addMembers(group, insertUsers);

        if (insertMembers == null)
            return ResponseModel.buildServiceError();

        List<GroupMemberCard> insertCarts = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        //通知


        //通知新增的群员   你已经被加入了  群
        PushFactory.pushJoinGroup(insertMembers);

        //通知老的成员   有  xxx  加入了  群
        PushFactory.pushGroupMemberAdd(oldMembers,insertCarts);

        return ResponseModel.buildOk(insertCarts);
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
