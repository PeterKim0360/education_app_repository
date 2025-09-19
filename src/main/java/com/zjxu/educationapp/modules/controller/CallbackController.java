package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.LiveRoomEntity;
import com.zjxu.educationapp.modules.service.LiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * nginx的RTMP模块回调接口
 */
@RestController
@RequestMapping("/callback/stream")
@Slf4j
public class CallbackController {
    @Autowired
    private LiveRoomService liveRoomService;
    // 推流开始,参数为POST application/x-www-form-urlencoded 格式；参数见doc/images/img.png
    /*
    推流开始,参数为POST application/x-www-form-urlencoded 格式；参数见doc/images/img.png
    流名name参数不包括rtmp请求的查询参数，rtmp://your-server-ip:1935/live/course123?token=abc123 -> name为course123
    这里使用ResponseEntity设置状态码，让nginx辨别是否应该推流
     */
    @PostMapping("/on-publish")
    public ResponseEntity<String> onPublish(@RequestParam String liveId,
                                       @RequestParam String userId,
                                       @RequestParam String name) {
        log.info("开始推流：{}",liveId);
        return liveRoomService.onPublish(liveId,userId,name);
    }

    @PostMapping("/on-publish-done")
    public ResponseEntity<String> onPublishDone(@RequestParam String liveId) {
        log.info("结束推流：{}",liveId);
        liveRoomService.removeById(liveId);
        return ResponseEntity.ok("关流成功");
    }
}
