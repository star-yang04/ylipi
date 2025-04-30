package com.example.ylipi;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * 功能
 * 作者：yangxin
 * 日期：2025/4/29 23:35
 */
public class CodeGenerator {
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://115.120.224.202:3306/ylipi?serverTimezone=GMT%2B8", "ylipi", "ylipi")
                .globalConfig(builder -> {
                    builder.author("Yang xin") // 设置作者
                            .outputDir("src/main/java"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example") // 设置父包名
                            .entity("entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mappers"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("appeals", "audit_logs","comments","merchant_orders","merchant_products","posts","private_messages","second_hand_orders","second_hand_products","sensitive_words","task_acceptance","tasks","users") // 设置需要生成的表名
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
