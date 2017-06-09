package com.mingchu.ruolan.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/9.
 *
 * @消息模型
 */
@Entity
@Table(name = "TB_MESSAGE")
public class Message {

    public static final int TYPE_STR = 1;//字符串类型
    public static final int TYPE_PIC = 2;//图片类型
    public static final int TYPE_FILE = 3;//文件类型
    public static final int TYPE_AUDIO = 4;//语音类型

    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    //避免复杂的服务器和客户端的映射关系
    //这里不自动生成UUID  ID由代码写入  由客户端负责生成
//    @GeneratedValue(generator = "uuid")   //UUID的类型  主键生成的存储的类型
    @GenericGenerator(name = "uuid", strategy = "uuid2")  //把uuid的生成器定义为uuid2  uuid2在hibernate中是常规的UUID
    @Column(unique = false, nullable = false)  //不能更改 不允许为空
    private String id;

    //内容不允许为空  类型为text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //附件
    @Column
    private String attach;

    //消息类型
    @Column(nullable = false)
    private String type;


    @CreationTimestamp   //定义为创建时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();  //当前创建时间

    @UpdateTimestamp   //更新时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();  //更新时间

    //发送者  多个消息对应一个发送者  不为空
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;

    //这个字段仅仅只是为了对应sender的数据库字段senderId
    //不允许手动的更新和插入
    @Column(updatable = false, insertable = false, nullable = false)
    private String senderId;

    //接收者  可以为空
    //多个消息对应一个接收者
    @JoinColumn(name = "receiverId")
    @ManyToOne(optional = true)
    private User receiver;
    //
    @Column(updatable = false, insertable = false)
    private String receiverId;


    //一个群可以接收多个消息
    @JoinColumn(name = "groupId")
    @ManyToOne(optional = true)
    private Group group;
    //
    @Column(updatable = false, insertable = false)
    private String groupId;

    public static int getTypeStr() {
        return TYPE_STR;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
