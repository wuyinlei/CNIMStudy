package com.mingchu.ruolan.push.factory;

import com.mingchu.ruolan.push.bean.db.Group;
import com.mingchu.ruolan.push.bean.db.GroupMember;
import com.mingchu.ruolan.push.bean.db.User;

import java.util.Set;

/**
 * Created by wuyinlei on 2017/6/23.
 */
public class GroupFactory {

    /**
     * 通过群id查询一个群
     *
     * @param groupId 群id
     * @return Group
     */
    public static Group findById(String groupId) {

        return null;
    }

    /**
     * 查询一个群的所有成员
     *
     * @param group 群
     * @return
     */
    public static Set<GroupMember> getMembers(Group group) {

        return null;
    }


    /**
     * 查询一个群  同时User必须是该群的成员
     *
     * @param sender     发送者
     * @param receiverId 接收者id
     * @return Group
     */
    public static Group findById(User sender, String receiverId) {


        return null;
    }
}
