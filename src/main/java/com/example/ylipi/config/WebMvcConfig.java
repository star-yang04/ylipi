package com.example.ylipi.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 功能
 * 作者：yangxin
 * 日期：2025/5/8 17:38
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射：访问 http://yourdomain.com/avatars/xxx.png 时返回磁盘图片
        // 将/avatars/**  映射到：/opt/uploads/avatars/
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:/opt/uploads/avatars/");
    }
}

