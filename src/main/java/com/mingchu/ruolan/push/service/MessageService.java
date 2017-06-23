package com.mingchu.ruolan.push.service;

import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.api.message.MessageCreateModel;
import com.mingchu.ruolan.push.bean.card.MessageCard;
import com.mingchu.ruolan.push.bean.db.Group;
import com.mingchu.ruolan.push.bean.db.Message;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.GroupFactory;
import com.mingchu.ruolan.push.factory.MessageFactory;
import com.mingchu.ruolan.push.factory.PushFactory;
import com.mingchu.ruolan.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by wuyinlei on 2017/6/23.
 */
@Path("/msg")
public class MessageService extends BaseService {


    //拉取联系人接口
    @POST
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求返回的响应体为JSON
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model))
            return ResponseModel.buildParameterError();

        User self = getSelf();

        //查询是否已经在数据库中存在
        Message message = MessageFactory.findById(model.getId());
        if (message != null) {
            //正常返回
            return ResponseModel.buildOk(new MessageCard(message));
        }

        if (model.getType() == Message.RECEIVER_TYPE_GROUP) {
            return pushToGroup(self, model);
        } else if (model.getType() == Message.RECEIVER_TYPE_NONW) {
            return pushToUser(self, model);
        }
        return null;

    }

    /**
     * 发送给人的消息
     *
     * @param sender 发送者
     * @param model  model
     */
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {

        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null)
            //没有找到接收者
            return ResponseModel.buildNotFoundUserError("Can't find receiver user");

        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            //发送者和接收者是同一个人  就返回创建爱消息失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }

        Message message = MessageFactory.add(sender, receiver, model);

        return buildAndPushResponse(sender, message);
    }


    /**
     * 发送给群
     *
     * @param sender  发送者
     * @param model 消息
     */
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {

        //找群  是有权限的  必须是群的成员
        Group group = GroupFactory.findById(sender,model.getReceiverId());
        if (group == null){
            //没有找到接收者群  可能不是群成员
            return ResponseModel.buildNotFoundUserError("Can't find receiver group");
        }

        Message message = MessageFactory.add(sender,group,model);


        return buildAndPushResponse(sender,message);
    }

    /**
     * 推送并构建一个返回信息
     *
     * @param sender  发送者
     * @param message 消息
     * @return
     */
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {

        if (message == null) {
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }

        PushFactory.pushNewMessage(sender, message);

        return ResponseModel.buildOk(new MessageCard(message));
    }
}
