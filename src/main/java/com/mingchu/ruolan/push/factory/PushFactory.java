package com.mingchu.ruolan.push.factory;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.PushModel;
import com.mingchu.ruolan.push.bean.card.GroupMemberCard;
import com.mingchu.ruolan.push.bean.card.MessageCard;
import com.mingchu.ruolan.push.bean.card.UserCard;
import com.mingchu.ruolan.push.bean.db.*;
import com.mingchu.ruolan.push.utils.Hib;
import com.mingchu.ruolan.push.utils.PushDispatcher;
import com.mingchu.ruolan.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by wuyinlei on 2017/6/23.
 * <p>
 * 消息存储与处理的方法
 */
public class PushFactory {


    /**
     * 发送一条消息 并在当前的消息历史中存储
     *
     * @param sender  推送者
     * @param message 消息
     */
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null)
            return;

        //消息卡片  用于发送
        MessageCard card = new MessageCard(message);

        //要推送的字符串
        String entity = TextUtil.toJson(card);

        PushDispatcher dispatcher = PushDispatcher.start();

        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //给单个用户发送消息
            User receiver = UserFactory.findById(message.getReceiverId());

            if (receiver == null)
                return;

            //历史消息记录
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            pushHistory.setEntity(entity);
            pushHistory.setReceiver(receiver);
            //当前的设备推送id
            pushHistory.setReceiverPushId(receiver.getPushId());

            //推送的真实model
            PushModel model = new PushModel();
            //每一条历史记录都是独立的  可以单独发送
            model.add(pushHistory.getEntityType(), pushHistory.getEntity());

            //把需要发送的数据丢给发送者进行发送
            dispatcher.add(receiver, model);

            //保存到数据库
            Hib.queryOnly(session -> session.save(pushHistory));


        } else {

            Group group = message.getGroup();
            //查询一个群   因为延迟加载  有可能为null  通过群id查询
            if (group == null)
                group = GroupFactory.findById(message.getGroupId());

            if (group == null)
                //如果群真的没有
                return;

            //给群发消息
            Set<GroupMember> members = GroupFactory.getMembers(group);

            if (members == null && members.size() == 0)
                return;

            //找寻id都不是我自己的id
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());

            //一个历史记录列表
            List<PushHistory> historyList = new ArrayList<>();

            addGroupMembersPushModel(dispatcher, //推送发送者
                    historyList,//数据库要存储的列表
                    members,//群所有成员
                    entity,//要发送的数据
                    PushModel.ENTITY_TYPE_MESSAGE); //发送的类型

            //保存到数据库的操作
            Hib.queryOnly(session -> {
                for (PushHistory pushHistory : historyList) {
                    session.saveOrUpdate(pushHistory);
                }
            });


        }

        dispatcher.submit();  //发送者进行真实的提交


    }

    /**
     * 给群成员构建一个消息   并且把消息存储到数据库的历史记录中  每个人 每个消息都是一个记录
     *
     * @param dispatcher        推送发送者
     * @param historyList       数据库要存储的列表
     * @param members           群所有成员
     * @param entity            要发送的数据
     * @param entityTypeMessage 发送的类型
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<PushHistory> historyList,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage) {

        for (GroupMember member : members) {
            //无须通过id再去查找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;
            //历史消息记录
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(entityTypeMessage);
            pushHistory.setEntity(entity);
            pushHistory.setReceiver(receiver);
            //当前的设备推送id
            pushHistory.setReceiverPushId(receiver.getPushId());

            historyList.add(pushHistory);

            PushModel pushModel = new PushModel();
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

            dispatcher.add(receiver, pushModel);


        }
    }

    /**
     * 创建群的时候给每个群成员发送一条已经加入群的消息
     *
     * @param groupMembers 群成员
     */
    public static void pusGroupAdd(Set<GroupMember> groupMembers) {

        //发送者
        PushDispatcher dispatcher = PushDispatcher.start();

        //一个历史记录列表
        List<PushHistory> historyList = new ArrayList<>();

        for (GroupMember member : groupMembers) {
            //无须通过id再去查找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;

            //每个成员信息的卡片
            GroupMemberCard card = new GroupMemberCard(member);
            String entity = TextUtil.toJson(member);
            //历史消息记录
            PushHistory pushHistory = new PushHistory();
            //你被添加到群的类型
            pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            pushHistory.setEntity(entity);
            pushHistory.setReceiver(receiver);
            //当前的设备推送id
            pushHistory.setReceiverPushId(receiver.getPushId());

            historyList.add(pushHistory);

            PushModel pushModel = new PushModel();
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

            dispatcher.add(receiver, pushModel);


        }


        //保存到数据库
        Hib.queryOnly(session -> {
            for (PushHistory history : historyList) {
                session.saveOrUpdate(history);
            }
        });

        //提交发送
        dispatcher.submit();


    }

    /**
     * 推送给新加入的成员 告知他们已经加入到了xxx群
     *
     * @param insertMembers 被加入群的成员
     */
    public static void pushJoinGroup(Set<GroupMember> insertMembers) {


    }

    /**
     * 通知老的成员 有一系列 新的人加入新增
     *
     * @param oldMembers    老成员
     * @param insertMembers 新增加的
     */
    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertMembers) {


        //发送者
        PushDispatcher dispatcher = PushDispatcher.start();

        //一个历史记录列表
        List<PushHistory> historyList = new ArrayList<>();

        //当前新增的用户的集合Json字符串
        String entity = TextUtil.toJson(insertMembers);

        //进行循环添加  给oldMembers每一个老用户构建一个消息  消息的内容是新增的用户的集合
        //通知的类型是群成员添加了的类型
        addGroupMembersPushModel(dispatcher,
                historyList,
                oldMembers,
                entity,
                PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);

        for (GroupMember oldMember : oldMembers) {


        }


        //保存到数据库
        Hib.queryOnly(session -> {
            for (PushHistory history : historyList) {
                session.saveOrUpdate(history);
            }
        });

        //提交发送
        dispatcher.submit();


    }

    /**
     * 推送退出消息
     *
     * @param receiver 接收者
     * @param pushId   这个时刻的接收者的设备id
     */
    public static void pushLogout(User receiver, String pushId) {

        //发送者
        PushDispatcher dispatcher = PushDispatcher.start();


        //历史消息记录
        PushHistory pushHistory = new PushHistory();
        //你被添加到群的类型
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        pushHistory.setEntity("Account logout");
        pushHistory.setReceiver(receiver);
        //当前的设备推送id
        pushHistory.setReceiverPushId(pushId);

        Hib.queryOnly(session ->
                session.save(pushHistory)
        );


        //具体推送的内容
        PushModel model = new PushModel().add(pushHistory.getEntityType(), pushHistory.getEntity());

        dispatcher.add(receiver, model);  //推送

        dispatcher.submit();

    }

    /**
     * 给被关注的人一个推送信息
     *  @param receiver  被关注的人
     * @param userCard  我的信息
     */
    public static void pushFollow(User receiver, UserCard userCard) {

        userCard.setIsFollow(true);

        //发送者
        PushDispatcher dispatcher = PushDispatcher.start();


        String entity = TextUtil.toJson(userCard);

        //历史消息记录
        PushHistory pushHistory = new PushHistory();
        //你被添加到群的类型
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        pushHistory.setEntity(entity);
        pushHistory.setReceiver(receiver);
        //当前的设备推送id
        pushHistory.setReceiverPushId(receiver.getPushId());

        Hib.queryOnly(session -> session.save(pushHistory));


        //具体推送的内容
        PushModel model = new PushModel().add(pushHistory.getEntityType(), pushHistory.getEntity());

        dispatcher.add(receiver,model);

        dispatcher.submit();


    }
}
