package com.mingchu.ruolan.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/9.
 */
@Entity
@Table(name = "TB_GROUP_MEMBER")
public class GroupMember {

    public static final int PERMISSION_TYPE_NONE = 0; //默认权限  普通成员
    public static final int PERMISSION_TYPE_ADMIN = -1;  //管理员
    public static final int PERMISSION_TYPE_SU = 100;  //创建者

    //通知类型
    public static final int NOTIFY_LEVEL_INVALID = -1;  //默认不通知
    public static final int NOTIFY_LEVEL_NONE = 0;  //默认通知级别
    public static final int NOTIFY_LEVEL_CLOSE = 1;  //接收消息不提示

    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")   //UUID的类型  主键生成的存储的类型
    @GenericGenerator(name = "uuid", strategy = "uuid2")  //把uuid的生成器定义为uuid2  uuid2在hibernate中是常规的UUID
    @Column(unique = false, nullable = false)  //不能更改 不允许为空
    private String id;


    @Column   //别名
    private String alias;


    @Column(nullable = false)
    private int notifyLevel = NOTIFY_LEVEL_NONE;

    //成员的权限类型
    @Column(nullable = false)
    private int permissionType = PERMISSION_TYPE_NONE;


    @CreationTimestamp   //定义为创建时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();  //当前创建时间

    @UpdateTimestamp   //更新时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();  //更新时间

    //成员对应的用户信息
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, updatable = false, insertable = false)
    private String userId;


    @JoinColumn(name = "groupId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Group group;

    @Column(nullable = false, updatable = false, insertable = false)
    private String groupId;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
