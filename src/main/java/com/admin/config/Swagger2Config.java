package com.admin.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Swagger2配置
 */
@Configuration
@EnableSwagger2
public class Swagger2Config extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.admin.project.controller")
                .title("移动端系统")
                .description("移动端系统相关接口文档")
                .contactName("rayBack")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
