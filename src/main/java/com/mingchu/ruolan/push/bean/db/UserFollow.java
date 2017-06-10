package com.mingchu.ruolan.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/9.
 *
 * @function 用于用户直接进行好友关系的实现
 */
@Entity
@Table(name = "TB_USER_FOLLOW")
public class UserFollow {

    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")   //UUID的类型  主键生成的存储的类型
    @GenericGenerator(name = "uuid", strategy = "uuid2")  //把uuid的生成器定义为uuid2  uuid2在hibernate中是常规的UUID
    @Column(unique = false, nullable = false)  //不能更改 不允许为空
    private String id;

    //发起人   关注某人的那个发起关注的  你
    //不可选  必须存储  一条关注一定要有一个关注人
    @ManyToOne(optional = false)  //多对一  你可以关注很多人   每一次关注都是一条数据   可以创建很多个关注的信息  一个user对应多个UserFollow
    @JoinColumn(name = "originId")  //定义关联的表字段名为originId  对应的是User.id
    private User origin;
    //把每个列提取到我们的Model中
    @Column(nullable = false ,updatable = false,insertable = false)
    private String originId;  //不允许为空 不允许更新  不允许插入

    //定义关注的目标  你关注的人   所以就是  多个UserFollow对应一个User
    @ManyToOne(optional = false)   //多对一   你可以被很多人关注
    @JoinColumn(name = "targetId")  //对应的是User.id
    private User target;

    @Column(nullable = false, updatable = false, insertable = false)
    private String targetId;  //不允许为空 不允许更新  不允许插入

    //别名也就是对target的备注  可以为null
    @Column
    private String alias;

    @CreationTimestamp   //定义为创建时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();  //当前创建时间

    @UpdateTimestamp   //更新时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();  //更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
