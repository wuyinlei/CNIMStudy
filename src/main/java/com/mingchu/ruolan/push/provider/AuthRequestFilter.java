package com.mingchu.ruolan.push.provider;

import com.google.common.base.Strings;
import com.mingchu.ruolan.push.bean.api.base.ResponseModel;
import com.mingchu.ruolan.push.bean.db.User;
import com.mingchu.ruolan.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by wuyinlei on 2017/6/10.
 *
 * @function 请求拦截器   用于所有的请求接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {


    /**
     * 实现接口的过滤方法
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        //检测是否是登录注册接口
        String relationPath = ((ContainerRequest)requestContext).getPath(false);
        if (relationPath.startsWith("account/login")
                ||relationPath.startsWith("account/register")){
            return;  //直接return返回  不做拦截
        }

        //从Headers里面找到第一个token
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)){
            //查找自己的信息
            final User user = UserFactory.findByToken(token);
            if (user != null){
                //查询到了自己的信息
                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {

                    //主体部分
                    @Override
                    public Principal getUserPrincipal() {
                        //User  实现 Principal接口
                        return user;
                    }

                    //
                    @Override
                    public boolean isUserInRole(String role) {
                        //可以在这里写入用户的权限  role是权限名
                        //可以管理员权限。。。
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //默认false  HTTPS
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //不用理会
                        return null;
                    }
                });

                //写入上下文后 就返回
                return;

            }
        }

        //直接返回一个账户异常  需要登录的model
        ResponseModel model = ResponseModel.buildAccountError();

       //构建一个返回
        Response response = Response.status
                (Response.Status.OK)
                .entity(model)
                .build();

        //停止一个请求的继续下发  调用该方法后直接返回请求  不会走到service
        requestContext.abortWith(
                response);
    }
}
