package com.mingchu.ruolan.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Hibernate 封装
 */
public class Hib {
    //全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        //静态初始化sessionFactory
        init();
    }

    private static void init() {
        // A SessionFactory is set up once for an application!
        //从Hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static void initOld() {
        try {
            //第一步:读取Hibernate的配置文件  hibernamte.cfg.xml文件
            Configuration configuration = new Configuration()
                    .configure("hibernate.cfg.xml");
            //第二步:创建服务注册构建器对象，通过配置对象中加载所有的配置信息
            StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder();
            sb.applySettings(configuration.getProperties());
            //创建注册服务
            StandardServiceRegistry standardServiceRegistry = sb.build();
            //第三步:创建会话工厂
            sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * 保存数据
     *
     * @param obj 传入类型  任意类型
     * @return 类型  返回的类型  任意类型
     */
    public static Object save(Object obj) {
        return query(session -> {
            session.saveOrUpdate(obj);
            return obj;
        });
    }

    /**
     * 保存数据   保存的集合数据   没有返回类型
     *
     * @param objectList 需要保存的集合数据
     */
    public static void save(List objectList) {
        queryOnly(session -> {
            for (Object o : objectList) {
                session.saveOrUpdate(o);
            }
        });
    }

    /**
     * 查询数据
     *
     * @param result 查询的条件
     * @param <T>    泛型T
     * @return 泛型T
     */
    public static <T> T query(QueryResult<T> result) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        T t = null;
        try {
            t = result.query(session);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transaction.rollback();
            } catch (RuntimeException rbe) {
                rbe.printStackTrace();
            }
        } finally {
            session.close();
        }
        return t;
    }

    /**
     * 就仅仅查询 不需要返回
     *
     * @param result
     */
    public static void queryOnly(QueryOnly result) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            result.query(session);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transaction.rollback();
            } catch (RuntimeException rbe) {
                rbe.printStackTrace();
            }
        } finally {
            session.close();
        }
    }

    /**
     * 用户的实际操作的一个接口  有返回值
     *
     * @param <T>
     */
    public interface QueryResult<T> {
        T query(Session session);
    }

    //无返回值
    public interface QueryOnly {
        void query(Session session);
    }
}
