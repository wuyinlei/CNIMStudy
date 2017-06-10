package com.mingchu.ruolan.push.factory;

import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.utils.Hib;
import com.mingchu.ruolan.push.utils.TextUtil;
import org.hibernate.Session;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function
 */
public class UserFactory {

    /**
     * 根据手机号进行查询操作
     * @param phone  手机号
     * @return User
     */
    public static User findByPhone(String phone){
        return Hib.query(session -> (User) session.createQuery("from User where phone =:inPhone")
                .setParameter("inPhone",phone)
                .uniqueResult());
    }

    /**
     * 根据手机号进行查询操作
     * @param name  手机号
     * @return User
     */
    public static User findByName(String name){
        return Hib.query(session -> (User) session.createQuery("from User where name =:inName")
                .setParameter("inName",name)
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

        User user = new User();

        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);  //账户就是手机号

        //进行数据库操作  创建一个会话
        Session session = Hib.session();
        session.beginTransaction();  //开启一个事务

        try {
            session.save(user);  //保存操作
            session.getTransaction().commit(); //提交事务
            return user;
        } catch (Exception e) {
            session.getTransaction().rollback();  //回滚事务
            return null;
        }
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
        password =  TextUtil.getMD5(password);

        return TextUtil.encodeBase64(password);  //在进行一次对称的Base64加密  当然也可以采取加盐的方法
    }

}
