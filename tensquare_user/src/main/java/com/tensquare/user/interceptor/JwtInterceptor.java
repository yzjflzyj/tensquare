package com.tensquare.user.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 进入Controller之前处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入拦截器....");
        String authorization = request.getHeader("Authorization");
        // 只处理有值的时候
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")){
            // 解析Token, 截取掉Bearer 前缀,后面才是真正的token
            String token = authorization.substring(7);
            Claims claims = null;
            try {
                claims = jwtUtil.parseJWT(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(null != claims){
                // 根据角色给request设置属性
                if("admin".equals(claims.get("roles"))){
                    // 管理员角色，用于Controller的鉴权判断
                    request.setAttribute("admin_claims", claims);
                }else if("user".equals(claims.get("roles"))){
                    // 普通用户角色
                    request.setAttribute("user_claims", claims);
                }
            }
        }
        return true;
    }
}
