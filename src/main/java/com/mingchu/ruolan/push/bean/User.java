package com.mingchu.ruolan.push.bean;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/4.
 *
 * @function 用户的Model  对应数据库
 */
@Entity
@Table(name = "TB_USER")
public class User {

    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")   //UUID的类型  主键生成的存储的类型
    @GenericGenerator(name = "uuid", strategy = "uuid2")  //把uuid的生成器定义为uuid2  uuid2在hibernate中是常规的UUID
    @Column(unique = false, nullable = false)  //不能更改 不允许为空
    private String id;

    @Column(nullable = false,length = 128,unique = true)  //不能为空 长度最大128  唯一
    private String name;

    @Column(nullable = false,length = 62,unique = true)  //不能为空 长度最大128  唯一
    private String phone;

    @Column(nullable = false)  //不能为空
    private String password;

    @Column //允许为空
    private String portrait;

    @Column //允许为空
    private String description;

    @Column(nullable = false)  //有初始值  不为空
    private int sex = 0;

    @Column(unique = true)  //token唯一 可以拉取用户信息
    private String token;

    @Column
    private String pushId;  //客户端上传 用于消息传送的时候的推送id

    @CreationTimestamp   //定义为创建时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();  //当前创建时间

    @UpdateTimestamp   //更新时间戳  在创建时候就已经写入
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();  //更新时间

    @Column(nullable = false)
    private LocalDateTime lastReceiveAt = LocalDateTime.now();  //最后一次接收到消息的时间


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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
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

    public LocalDateTime getLastReceiveAt() {
        return lastReceiveAt;
    }

    public void setLastReceiveAt(LocalDateTime lastReceiveAt) {
        this.lastReceiveAt = lastReceiveAt;
    }
}
