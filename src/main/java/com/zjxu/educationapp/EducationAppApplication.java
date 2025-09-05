package com.zjxu.educationapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zjxu.educationapp.modules.mapper")
public class EducationAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducationAppApplication.class, args);
    }

}
