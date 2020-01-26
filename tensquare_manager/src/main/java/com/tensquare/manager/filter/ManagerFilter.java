package com.tensquare.manager.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@Component
public class ManagerFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //获取请求对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String authorization = request.getHeader("Authorization");
        // 只处理有值的时候
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")){
            String token = authorization.substring(7);
            Claims claims = null;
            try {
                claims = jwtUtil.parseJWT(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(null != claims){
                if("admin".equals(claims.get("roles"))){
                    // 使用zuul转发
                    currentContext.addZuulRequestHeader("Authorization", authorization);
                    return null;
                }
            }
        }
        // 终止转发
        currentContext.setSendZuulResponse(false);
        currentContext.setResponseStatusCode(401); // 401 没有权限 403 禁止访问
        currentContext.setResponseBody("没有权限");
        currentContext.getResponse().setContentType("text/html;charset=utf-8");

        return null;
    }
}
