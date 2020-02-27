package com.ecnu2020.achieveit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义json序列化策略
 * @author yan on 2020-02-27
 */
@Configuration
public class JsonConfig {

    @Bean("json")
    public Gson json(){
        return new GsonBuilder().serializeNulls().create();
    }

}
