//package com.zjxu.educationapp;
//
//import cn.dev33.satoken.stp.StpUtil;
//import cn.hutool.core.lang.UUID;
//import com.zjxu.educationapp.common.utils.AliOSSUtil;
//import com.zjxu.educationapp.modules.dto.LoginDTO;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//class EducationAppApplicationTests {
//
//    //    @Test
////    void testToken() {
////        System.out.println("token:" + StpUtil.getTokenValue());
////    }
//    @Autowired
//    private AliOSSUtil aliOSSUtil;
//    @Test
//    void testOSS(){
//        System.out.println("123");
//        System.out.println(aliOSSUtil.getAccessKeyId());
//        System.out.println(aliOSSUtil.getAccessKeySecret());
//    }
//
//    @Test
//    void upload() throws IOException {
//        // 文件路径
//        String filePath = "C:\\Users\\Kim-Peter\\Desktop\\90.jpg";
//
//        // 将文件读取为字节数组
//        Path path = Paths.get(filePath);
//        byte[] fileBytes = Files.readAllBytes(path);
//
//        // 生成唯一的文件名
//        String fileName = "updateFiles/"+UUID.randomUUID().toString() + ".jpg";
//
//        // 上传文件
//        String url = aliOSSUtil.upload(fileBytes, fileName);
//
//        System.out.println("文件上传成功，访问URL: " + url);
//    }
//
//    @Test
//    public void testCopyBean(){
//        LoginDTO loginDTO = new LoginDTO();
//
//    }
//
//    @Test
//    public void testStream(){
//        System.out.println("HelloWorld");
//        String url = "http://192.168.88.130:8090/control/drop/publisher";
//        Map<String, String> body = new HashMap<>();
//        body.put("app", "live");
//        body.put("name", "demo");
//        RestTemplate restTemplate = new RestTemplate();
//        String response = restTemplate.postForObject(url, body, String.class);
//        System.out.println(response);
//    }
//
//    @Test
//    public void testStream2(){
//        String uuid= UUID.randomUUID().toString();
//        String streamKey = "/"+uuid+"?"+"userId="+"11";
//        System.out.println(streamKey);
//    }
//}
