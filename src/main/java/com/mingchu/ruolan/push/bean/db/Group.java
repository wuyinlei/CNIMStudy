package com.mingchu.ruolan.push.bean.db;

import com.mingchu.ruolan.push.bean.api.group.GroupCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/9.
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {

    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")   //UUID的类型  主键生成的存储的类型
    @GenericGenerator(name = "uuid", strategy = "uuid2")  //把uuid的生成器定义为uuid2  uuid2在hibernate中是常规的UUID
    @Column(unique = false, nullable = false)  //不能更改 不允许为空
    private String id;

    //群名称
    @Column(nullable = false)
    private String name;

    //群描述
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String picture;

    @CreationTimestamp   //定义为创建时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();  //当前创建时间

    @UpdateTimestamp   //更新时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();  //更新时间


    //群的创建者   可选为false  必须有一个创建者  加载方式为急加载 意味着在加载群信息的时候就必须加载创建者的信息
    //cascade  连级级别  所有的更改(更新 删除等) 都将进行关系更新
    @JoinColumn(name = "ownerId")
    @ManyToOne(optional = false,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private User owner;

    @Column(nullable = false,updatable = false,insertable = false)
    private String ownerId;

    public Group() {
    }

    public Group(User owner, GroupCreateModel model){
        this.owner = owner;
        this.name = model.getName();
        this.description = model.getDesc();
        this.picture = model.getPicture();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
