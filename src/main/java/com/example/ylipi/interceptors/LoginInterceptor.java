package com.example.ylipi.interceptors;

import com.alibaba.fastjson.JSON;
import com.example.ylipi.common.Result;
import com.example.ylipi.utils.JwtUtil;
import com.example.ylipi.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String token = request.getHeader("Authorization");

        //验证token
        try {
            //从redis中获取相同的token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (redisToken == null) {
                //Token已经失效
                Result result = Result.error("Token已失效,请重新登录");
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(result));
                return false;
            }

            Map<String, Object> claims = JwtUtil.parseToken(token);

            //把业务数据存储到ThreadLocal当中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        } catch (Exception e) {
            Result result = Result.error("Token验证失败,请重新登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}