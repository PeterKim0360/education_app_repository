package com.zjxu.educationapp.modules.controller;


import cn.hutool.core.lang.UUID;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.AliOSSUtil;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/common")
@Slf4j
@Tag(name = "通用接口")
public class CommonController {
    @Autowired
    private AliOSSUtil aliOSSUtil;

    /**
     * 文件上传，测试可以选择表单传文件
     *
     * @param fileList
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "传参：file;限制是单个文件100MB，单此请求1024MB")
    public Result<List<String>> upload(List<MultipartFile> fileList) {
        log.info("文件上传：{}", fileList);
        List<String> resList = new ArrayList<>(fileList.size());
        for (MultipartFile file : fileList) {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称,防止文件名冲突
            String objectName = UUID.randomUUID().toString() + extension;
            try {
                resList.add(aliOSSUtil.upload(file.getBytes(), objectName));
            } catch (IOException e) {
                log.error("文件上传失败：{}", e.getMessage());
                return Result.error(ErrorCode.UPLOAD_FAILED);
            }
        }
        return Result.ok(resList);
    }
}
