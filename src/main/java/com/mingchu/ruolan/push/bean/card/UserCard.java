package com.mingchu.ruolan.push.bean.card;

import com.google.gson.annotations.Expose;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.utils.Hib;

import java.time.LocalDateTime;

/**
 * Created by wuyinlei on 2017/6/10.
 */
public class UserCard {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String password;
    @Expose
    private String portrait;
    @Expose
    private String desc;
    @Expose
    private int sex = 0;
    @Expose
    private int follows;//用户关注数量
    @Expose
    private int following;//用户粉丝数量
    @Expose
    private LocalDateTime modifyAt;  //用户最后登录的更新时间
    @Expose
    private boolean isFollow;  //我当前User的关系状态 是否已经关注了这个人

    public UserCard(User user) {
       this(user,false);
        // TODO: 2017/6/10   得打关注人和粉丝的数量
//        user.getFollowers().size();
         //懒加载会报错  因为没有Session

    }

    public UserCard(User user,boolean isFollow) {
        this.isFollow = isFollow;
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.password = user.getPassword();
        this.portrait = user.getPortrait();
        this.desc = user.getDescription();
        this.sex = user.getSex();
        this.modifyAt = user.getUpdateAt();

        // TODO: 2017/6/10   得打关注人和粉丝的数量
//        user.getFollowers().size();
        //懒加载会报错  因为没有Session
        Hib.queryOnly(session -> {
            session.load(user, user.getId());
            follows = user.getFollowers().size();
            following = user.getFollowing().size();
        });

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }

    public boolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }
}
