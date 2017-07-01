package com.mingchu.ruolan.push.bean.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuyinlei on 2017/6/30.
 *
 * @function 添加群成员model
 */
public class GroupMemberAddModel {

    @Expose
    private Set<String> users = new HashSet<>();

    public static boolean check(GroupMemberAddModel model){
        return !(model.getUsers() == null && model.getUsers().size() == 0);
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
