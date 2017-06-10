package com.mingchu.ruolan.push.factory;

import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.utils.Hib;
import com.mingchu.ruolan.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.UUID;

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
                (User) session.save(user)
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
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
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

}
