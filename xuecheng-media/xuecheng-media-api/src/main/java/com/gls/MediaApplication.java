package com.gls;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 启动类
 * @createDate 2024/7/9 19:02
 **/

@EnableSwagger2Doc
@SpringBootApplication
public class MediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaApplication.class, args);
    }
}