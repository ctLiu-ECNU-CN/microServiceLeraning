package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    //    做用户信息
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        获取登录用户信息
        String userId = request.getHeader("user-info");
//        如果有信息,存入ThreadLocal
        if(StrUtil.isNotBlank(userId)) {
            UserContext.setUser(Long.valueOf(userId));
        }
//        放行
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,Object handler, Exception e) throws Exception{
//        清理用户信息
        UserContext.removeUser();
    }
}
