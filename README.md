# CNIMStudy
即时聊天后台服务端代码

# IM知识点(前后端都涉及大)

标签（空格分隔）： 开源项目 JAVA后台 Android前端

---
##How to use this Project
* https://github.com/wuyinlei/CNIMStudy/blob/master/src/main/file/how_to_use.md

###IM四大协议
* IMPP：即时信息和空间协议
* XMPP：可扩展通讯和表示协议
* SIMPLE(SIP)：针对及时信息和空间平衡的扩充协议
* PRIM：空间和及时信息协议(已未使用)
###实现方案
* 基于IM四大协议自己实现
* 网易云信 SDK UIKIT
* WebSoket    Socket.IO
* 推送方案(Socket、Netty)

###第三方平台准备
####1、推送平台
    
* 速度:越快越好；限制：越低越好
* 现有:已有项目；价格：越好越好 
* 友盟、腾讯、阿里、个推、小米、、、

####2、存储平台

* 七牛
* 阿里OSS
* 百度、腾讯

####个推配置：
Appid：z3XMHzGxto75mpW5ExvQn7
AppSecret：hv9MIQpECo5j6qVI196gz7
AppKey：xO2Cm6I3do61pXLseLU0S2
MasterSecret：OXGSBCVB8u5skpYTCDHNb1
####阿里配置
登录账号：loveruolan111

###Git 相关操作
####Gti Tag相关操作
- git tag
- git tag -a tag_name -m "tag描述"
- git checkout tag_name
- git branch
- git checkout -b 'branch_name'

###服务端环境搭建
* 相关依赖
```
compile 'com.google.code.gson:gson:2.8.0'
    compile 'com.google.guava:guava:21.0'

    compile 'com.gexin.platform:gexin-rp-sdk-http:4.0.1.7'

    compile 'org.glassfish.jersey.core:jersey-client:2.26-b03'
    compile 'org.glassfish.jersey.core:jersey-server:2.26-b03'
    compile 'org.glassfish.jersey.containers:jersey-container-servlet:2.26-b03'
    compile 'org.glassfish.jersey.media:jersey-media-json-jackson:2.26-b03'
    
    // https://mvnrepository.com/artifact/org.hibernate/hibernate-core
    compile 'org.hibernate:hibernate-core:5.2.9.Final'
    // https://mvnrepository.com/artifact/org.hibernate/hibernate-entitymanager
    compile 'org.hibernate:hibernate-entitymanager:5.2.9.Final'
    // https://mvnrepository.com/artifact/org.hibernate/hibernate-c3p0
    compile 'org.hibernate:hibernate-c3p0:5.2.9.Final'

    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    compile group: 'mysql', name: 'mysql-connector-java', version: '6.0.6'

    testCompile group: 'junit', name: 'junit', version: '4.11'

```
####hibernate.cfg.xml文件配置
```
<?xml version='1.0' encoding='utf-8'?>
<!--
  ~ Hibernate, Relational Persistence for Idiomatic Java
  ~
  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  -->
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings 数据库连接驱动-->
        <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <!--<property name="connection.url">jdbc:mysql://127.0.0.1:3306/DB_I_T_PUSH?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false</property>-->
        <!--<property name="connection.username">root</property>-->
        <!--<property name="connection.password">123456</property>-->
        <!--本地地址-->
        <property name="connection.url">jdbc:mysql://127.0.0.1:8080/DB_I_T_PUSH?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false</property>
        <!--<property name="connection.url">jdbc:mysql://qiujuer.net:6968/DB_I_T_PUSH?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false</property>-->
        <property name="connection.username">root</property>
        <property name="connection.password">root</property>


        <!-- JDBC connection pool (use the built-in) 连接池大小-->
        <property name="connection.pool_size">5</property>

        <!-- SQL dialect -->
        <property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>

        <!-- Enable Hibernate's automatic session context management Hibernate上下文线程池级别-->
        <property name="current_session_context_class">thread</property>

        <!-- Disable the second-level cache  -->
        <property name="cache.provider_class">org.hibernate.c3p0.internal.C3P0ConnectionProvider</property>
        <property name="c3p0.min_size">6</property> <!--在连接池中可用数据库连接的最小数目-->
        <property name="c3p0.max_size">50</property> <!--在连接池中所有数据库连接的最大数目-->
        <property name="c3p0.time_out">1800</property> <!--设定数据库连接的超时时间-->
        <property name="c3p0.max_statement">50</property> <!--可以被缓存的PreparedStatement的最大数目-->
        <!-- configuration pool via c3p0-->
        <property name="c3p0.acquire_increment">1</property>
        <property name="c3p0.idle_test_period">100</property> <!-- seconds -->
        <property name="c3p0.max_statements">0</property>
        <property name="c3p0.timeout">100</property> <!-- seconds -->

        <!-- Echo all executed SQL to stdout -->
        <property name="show_sql">true</property>
        <property name="format_sql">true</property>


        <!--
            create:  表示启动的时候先drop  在create
            create-drop：也表示创建  只不过在系统关闭前执行一下drop
            update: 这个操作启动的时候会去检查schema是否一致，如果不一致会做schema更新
            validate： 启动时验证现有的schema与你配置的hibernate是否一致，如果不一致就会抛出异常  并不做更新
        -->
        <!-- Drop and re-create the database schema on startup -->
        <property name="hbm2ddl.auto">update</property>

    
```
web.xml配置
```
<?xml version="1.0" encoding="UTF-8"?>

<web-app>

    <display-name>CNIMStudy</display-name>

    <servlet>
        <servlet-name>ITalkerApiServlet</servlet-name>
        <!--容器-->
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <!--映射的包名 用于搜索处理-->
            <param-name>jersey.config.server.provider.packages</param-name>
            <param-value>com.mingchu.ruolan.push.service</param-value>
        </init-param>
        <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>com.mingchu.ruolan.push.Application</param-value>
        </init-param>

        <!--启动的时候是否加载  true-->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!--映射-->
    <servlet-mapping>
        <servlet-name>ITalkerApiServlet</servlet-name>
        <!--访问路径-->
        <url-pattern>/api/*</url-pattern>
    </servlet-mapping>
</web-app>
```
