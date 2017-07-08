package com.mingchu.ruolan.push.factory;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.group.GroupCreateModel;
import com.mingchu.ruolan.push.bean.db.Group;
import com.mingchu.ruolan.push.bean.db.GroupMember;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        return Hib.query(session -> session.get(Group.class, groupId));
    }

    /**
     * 查找一个群 通过群的名字
     *
     * @param name 群的名字
     * @return 群
     */
    public static Group findByName(String name) {

        return Hib.query(session ->
                (Group) session.createQuery("from Group where lower(name)=:name")
                        .setParameter("name", name.toLowerCase())
                        .uniqueResult());
    }

    /**
     * 查询一个群的所有成员
     *
     * @param group 群
     * @return 群的所有成员
     */
    public static Set<GroupMember> getMembers(Group group) {
        return Hib.query(session -> {

            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where group = :group")
                    .setParameter("group", group)
                    .list();

            return new HashSet<>(members);
        });

    }


    /**
     * 查询一个群  同时User必须是该群的成员
     *
     * @param sender  发送者
     * @param groupId 接收者id
     * @return Group
     */
    public static Group findById(User sender, String groupId) {

        GroupMember member = getMember(sender.getId(), groupId);
        if (member != null)
            return member.getGroup();

        return null;
    }


    /**
     * 创建一个群
     *
     * @param creator 创建者
     * @param model   创建者model
     * @param users   群成员
     * @return 创建好的群
     */
    public static Group create(User creator, GroupCreateModel model, List<User> users) {

        return Hib.query(session -> {

            Group group = new Group(creator, model);
            session.save(group);

            GroupMember ownerMember = new GroupMember(creator, group);

            //设置超级权限
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            //保存  并没有提交到数据库 此时
            session.save(ownerMember);

            for (User user : users) {

                GroupMember member = new GroupMember(user, group);

                session.save(member);
            }

            session.saveOrUpdate(group);

//            session.flush();  刷新缓冲区
//            session.load(group,group.getId());  重新加载

            return group;

        });


    }

    /**
     * 获取到群的管理员
     *
     * @param creatorId 创建者id
     * @param groupId   群id
     * @return 管理员
     */
    public static GroupMember getMember(String creatorId, String groupId) {

        return Hib.query(session ->

                (GroupMember) session.createQuery("from GroupMember where userId =:userId and groupId = :groupId")
                        .setParameter("userId", creatorId)
                        .setParameter("groupId", groupId)
                        .setMaxResults(1)
                        .uniqueResult()
        );
    }

    /**
     * 查询群
     *
     * @param name
     * @return
     */
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";

        final String searchGroupName = "%" + name + "%";  //进行模糊匹配

        return Hib.query(session -> (List<Group>) session
                .createQuery("from Group  where lower(name) like :name")
                .setParameter("name", searchGroupName)
                .setMaxResults(20)
                .list());

    }

    /**
     * 获取一个人加入的所有群
     *
     * @param self User
     * @return Set<GroupMember>
     */
    public static Set<GroupMember> getMembers(User self) {

        return Hib.query(session -> {
                    List<GroupMember> members = session.createQuery("from GroupMember where userId =:userId")
                            .setParameter("userId", self.getId())
                            .list();

                    return new HashSet<>(members);
                }
        );
    }

    /**
     * 添加成员
     * @param group  群
     * @param insertUsers  添加的用户成员
     * @return Set<GroupMember>
     */
    public static Set<GroupMember> addMembers(Group group, List<User> insertUsers) {

       return Hib.query(session -> {

            Set<GroupMember> members = new HashSet<>();

            for (User user : insertUsers) {

                GroupMember member = new GroupMember(user, group);

                session.save(member);
                members.add(member);
            }

            //进行数据刷新
//           for (GroupMember member : members) {
//               session.refresh(member);  //进行刷新   会进行关联查询 但是在此循环 消耗较高
//           }



            return members;
        });


    }
}
