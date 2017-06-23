package com.mingchu.ruolan.push.factory;

import com.mingchu.ruolan.push.bean.api.message.MessageCreateModel;
import com.mingchu.ruolan.push.bean.db.Group;
import com.mingchu.ruolan.push.bean.db.Message;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.utils.Hib;

/**
 * Created by wuyinlei on 2017/6/23.
 */
public class MessageFactory {

    //查询某一个消息
    public static Message findById(String id) {
        return Hib.query(session -> session.get(Message.class, id));
    }


    /**
     * 添加一条普通消息
     *
     * @param user
     * @param receiver
     * @param model
     * @return
     */
    public static Message add(User user, User receiver, MessageCreateModel model) {
        Message message = new Message(user, receiver, model);

        return save(message);
    }

    /**
     * 添加一条群消息
     *
     * @param user
     * @param group
     * @param model
     * @return
     */
    public static Message add(User user, Group group, MessageCreateModel model) {
        Message message = new Message(user, group, model);

        return save(message);
    }


    /**
     * 保存消息
     *
     * @param message 消息
     * @return Message
     */
    private static Message save(Message message) {
        return Hib.query(session -> {
            session.save(message);

            //写入到数据库
            session.flush();

            //紧接着从数据库中查询出
            session.refresh(message);
            return message;
        });
    }


}
