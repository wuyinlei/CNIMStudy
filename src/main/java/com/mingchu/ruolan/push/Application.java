package com.mingchu.ruolan.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.mingchu.ruolan.push.service.AccountService;
import com.sun.tools.javac.util.Log;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * Created by wuyinlei on 2017/6/4.
 */
public class Application extends ResourceConfig{


    public Application() {
        //注册逻辑处理的包名  以下两种方式都是可以的
//        packages("com.mingchu.ruolan.push.service");
        packages(AccountService.class.getPackage().getName());

        System.out.println("到这了");

        //注册Json转换器
        register(JacksonJaxbJsonProvider.class);

        //注册日志打印输出
        register(Logger.class);
    }
}
