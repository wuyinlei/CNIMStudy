package com.mingchu.ruolan.push.factory;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.bean.db.UserFollow;
import com.mingchu.ruolan.push.utils.Hib;
import com.mingchu.ruolan.push.utils.TextUtil;
import org.hibernate.Session;

import javax.jws.soap.SOAPBinding;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function
 */
public class UserFactory {

    /**
     * 根据手机号进行查询操作
     *
     * @param phone 手机号
     * @return User
     */
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone =:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    /**
     * 根据手机号进行查询操作
     *
     * @param name 手机号
     * @return User
     */
    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name =:inName")
                .setParameter("inName", name)
                .uniqueResult());
    }

    /**
     * 根据手机号进行查询操作
     *
     * @param userId 手机号
     * @return User
     */
    public static User findById(String userId) {
        //通过id查询更加方便
        return Hib.query(session -> session.get(User.class, userId));
    }


    /**
     * 根据Token进行查询操作  只能自己查询  只能自己使用  非他人的信息
     *
     * @param token token
     * @return User
     */
    public static User findByToken(String token) {
        return Hib.query(session -> (User) session.createQuery("from User where token =:token")
                .setParameter("token", token)
                .uniqueResult());
    }

    /**
     * 更新到数据库
     *
     * @param user user
     * @return user
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 给当前的账户绑定pushId
     *
     * @param user   自己的USER
     * @param pushId 自己的设备id
     * @return User
     */
    public static User bindPushId(User user, String pushId) {

        if (Strings.isNullOrEmpty(pushId))
            return null;
        String id = user.getId();
        //第一步  查询是否有其他账号绑定了当前id
        //取消绑定 避免推送混乱  //查询的列表不能包括自己
        Hib.queryOnly(session -> {
            @SuppressWarnings("uncheced")
            List<User> userList = (List<User>) session.createQuery("from User" +
                    " where lower(pushId)=:pushId and" +
                    "id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", id)
                    .list();

            for (User u : userList) {
                u.setPushId(null);  //设置pushId为null
                session.saveOrUpdate(u);  //更新
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())) {
            return user;  //如果当前需要绑定的设备id  之前已经绑定过了   那么就不需要额外的绑定
        } else {
            //如果当前账户的之前的设备id  和需要绑定的不同   那么 需要单点登录
            // 让之前的设备退出账户  给之前的设备推送一条退出消息  todo  推送的退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {

            }
            //更新新的设备id
            user.setPushId(pushId);
            return update(user);
        }
    }


    /**
     * 用户注册的操作  需要写入数据库  并且返回数据库中的User信息
     *
     * @param account  账户
     * @param password 密码
     * @param name     名字
     * @return user
     */
    public static User register(String account, String password, String name) {
        //去除空格  首尾
        account = account.trim();
        //处理密码
        password = encodePassword(password);

        //进行数据库操作  创建一个会话
        Session session = Hib.session();
        session.beginTransaction();  //开启一个事务
        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);
        }
        return user;
    }

    /**
     * 登录逻辑  操作
     *
     * @param account  账户
     * @param password 密码
     * @return User
     */
    public static User login(String account, String password) {
        String accountStr = account.trim();
        //把原文进行同样的密码加密处理  然后进行匹配
        String encodePassword = encodePassword(password);
        User user = Hib.query(session ->
                (User) session.createQuery("from User where phone=:phone and password=:password")
                        .setParameter("phone", accountStr)
                        .setParameter("password", encodePassword)
                        .uniqueResult());
        if (user != null)
            //对user进行登录操作  更新Token
            user = login(user);
        return user;
    }

    /**
     * 注册部分 创建账户
     *
     * @param account  账户  手机号
     * @param password 加密后的密码
     * @param name     名字
     * @return User
     */
    private static User createUser(String account, String password, String name) {

        final User user = new User();

        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);  //账户就是手机号

        //数据库存储
        return Hib.query(session ->
                {
                    session.save(user);
                    return user;
                }
        );
    }

    /**
     * 把一个User进行登录操作
     * 本质上是对Token的更新
     *
     * @param user user
     * @return user
     */
    public static User login(User user) {
        String newToken = UUID.randomUUID().toString();   //使用一个随机的UUID值充当Token
        newToken = TextUtil.encodeBase64(newToken); //进行一次base64操作
        user.setToken(newToken);

        //
        return update(user);
    }


    /**
     * 密码进行加密
     *
     * @param password 密码
     * @return 经过MD5加密后的密码
     */
    private static String encodePassword(String password) {
        password = password.trim();
        //进行MD5加密
        password = TextUtil.getMD5(password);

        return TextUtil.encodeBase64(password);  //在进行一次对称的Base64加密  当然也可以采取加盐的方法
    }


    /**
     * 查询联系人
     *
     * @return 联系人的列表
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            //重新加载一次用户信息 到self中  和当前的session绑定
            session.load(self, self.getId());
            //获取我关注的人
            Set<UserFollow> follows = self.getFollowers();

            //使用Java8简写方法
            return follows.stream()
                    .map(flow -> flow.getTarget()).collect(Collectors.toList());

        });
    }


    /**
     * 关注人的操作
     *
     * @param origin 发起者
     * @param target 被关注的人
     * @param alias  备注名
     * @return 被关注的人
     */
    public static User follow(final User origin, final User target, final String alias) {
        UserFollow userFollow = getUserFollow(origin, target);
        if (userFollow != null) {
            //已经关注  直接返回
            return userFollow.getTarget();
        }

        return Hib.query(session -> {
            //想要重新操作懒加载的数据  需要重新load一次
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            //我关注人的时候   同时他也关注我  所以需要添加两条数据
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            originFollow.setAlias(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(origin);
            targetFollow.setTarget(target);

            //保存操作
            session.saveOrUpdate(originFollow);
            session.saveOrUpdate(targetFollow);

            return target;

        });
    }

    /**
     * 查询两个人是否已经关注
     *
     * @param origin 发起者
     * @param target 被关注人
     * @return UserFollow  中间类
     */
    public static UserFollow getUserFollow(final User origin, final User target) {
        return Hib.query((Session session) -> (UserFollow) session.createQuery("from UserFollow where originId=:originId and targetId=:targetId ")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                //查询一条数据
                .uniqueResult());
    }


}
