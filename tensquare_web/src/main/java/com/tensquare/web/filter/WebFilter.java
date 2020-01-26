package com.tensquare.web.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class WebFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 转发前处理
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 值越小越优先
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // 是否要过滤
        return true;
    }

    /**
     * 过滤执行的方法
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //获取请求对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String authorization = request.getHeader("Authorization");
        // 只处理有值的时候
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")){
            // 使用zuul转发
            currentContext.addZuulRequestHeader("Authorization", authorization);
        }
        return null;
    }
}
