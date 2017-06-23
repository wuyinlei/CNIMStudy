package com.mingchu.ruolan.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.bean.db.Message;

/**
 * API请求的Model格式
 * <p>
 * Created by wuyinlei on 2017/6/23.
 */
public class MessageCreateModel {

    //这是一个主键
    @Expose
    private String id;
    @Expose
    private String content;
    @Expose
    private String attach;
    @Expose
    private int type = Message.RECEIVER_TYPE_NONW;
    @Expose
    private String receiverId;
    //接收者的类型  群 和 人
    @Expose
    private int receiverType;

    public static boolean check(MessageCreateModel model) {

        //model不允许为空
        return model != null &&
                !(Strings.isNullOrEmpty(model.id)
                        || Strings.isNullOrEmpty(model.content)
                        || Strings.isNullOrEmpty(model.receiverId))

                && (model.receiverType == Message.RECEIVER_TYPE_NONW ||
                model.receiverType == Message.RECEIVER_TYPE_GROUP)

                && (model.type == Message.TYPE_STR
                || model.type == Message.TYPE_PIC
                || model.type == Message.TYPE_FILE
                || model.type == Message.TYPE_AUDIO);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }
}
