package com.ecnu2020.achieveit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源访问接口
 * 路由配置在yml
 * @author yan on 2020-02-27
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${file.excelPath}")
    private String excelPath;

    @Value("${file.excelUrl}")
    private String excelUrl;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler(excelUrl + "/**")
            .addResourceLocations("file:" + excelPath + "/");

    }


//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry){
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

}
