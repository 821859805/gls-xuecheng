package com.gls.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mp配置扫描mapper和分页插件
 *
 * @author 郭林赛
 * @version 1.0
 * @createDate 2024/6/25 18:44
 **/

@Configuration
@MapperScan("com.gls.content.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //定义mp拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加MySQL类型的分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
