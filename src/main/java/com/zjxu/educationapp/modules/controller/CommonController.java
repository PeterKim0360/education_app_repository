package com.zjxu.educationapp.modules.controller;


import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.AliOssUtil;
import com.zjxu.educationapp.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/common")
@Slf4j
@Tag(name = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传",description = "传参：file")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
//            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
//            String objectName= UUID.randomUUID().toString()+ extension;

            String filePath = aliOssUtil.upload(file.getBytes(), originalFilename);
            return Result.ok(filePath);
        } catch (IOException e) {
            log.error("文件上传失败: {}",e);
        }
        return Result.error(ErrorCode.UPLOAD_FAILED);
    }
}
