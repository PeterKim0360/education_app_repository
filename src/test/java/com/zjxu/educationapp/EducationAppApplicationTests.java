package com.zjxu.educationapp;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import com.zjxu.educationapp.common.utils.AliOssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EducationAppApplicationTests {

    //    @Test
//    void testToken() {
//        System.out.println("token:" + StpUtil.getTokenValue());
//    }
    @Autowired
    private AliOssUtil aliOSSUtil;
    @Test
    void testOSS(){
        System.out.println("123");
        System.out.println(aliOSSUtil.getAccessKeyId());
        System.out.println(aliOSSUtil.getAccessKeySecret());
    }

    @Test
    void upload() throws IOException {
        // 文件路径
        String filePath = "C:\\Users\\Kim-Peter\\Desktop\\90.jpg";

        // 将文件读取为字节数组
        Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);

        // 生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + ".jpg";

        // 上传文件
        String url = aliOSSUtil.upload(fileBytes, fileName);

        System.out.println("文件上传成功，访问URL: " + url);
    }
}
