package com.mingchu.ruolan.push.factory;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.PushModel;
import com.mingchu.ruolan.push.bean.card.MessageCard;
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

            //历史消息记录
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(entityTypeMessage);
            pushHistory.setEntity(entity);
            pushHistory.setReceiver(receiver);
            //当前的设备推送id
            pushHistory.setReceiverPushId(receiver.getPushId());

            historyList.add(pushHistory);

            PushModel pushModel = new PushModel();
            pushModel.add(pushHistory.getEntityType(),pushHistory.getEntity());

            dispatcher.add(receiver,pushModel);


        }
    }
}
