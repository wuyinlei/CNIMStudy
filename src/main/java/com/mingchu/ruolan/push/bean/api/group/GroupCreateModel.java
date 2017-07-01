package com.mingchu.ruolan.push.bean.api.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuyinlei on 2017/6/30.
 *
 * @function 群添加的model
 */
public class GroupCreateModel {

    @Expose
    private String name;

    @Expose
    private String desc;

    @Expose
    private String picture;

    @Expose
    private Set<String> users = new HashSet<>();

    public static boolean check(GroupCreateModel model) {

        return !(Strings.isNullOrEmpty(model.name)
                || Strings.isNullOrEmpty(model.desc)
                || Strings.isNullOrEmpty(model.picture)
                || model.users == null
                || model.users.size() == 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
