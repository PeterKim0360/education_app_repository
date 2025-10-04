//package com.zjxu.educationapp.modules.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.github.xiaoymin.knife4j.annotations.Ignore;
//import com.zjxu.educationapp.common.constant.RedisConstant;
//import com.zjxu.educationapp.common.utils.Result;
//import com.zjxu.educationapp.modules.entity.LiveRoomClassEntity;
//import com.zjxu.educationapp.modules.entity.LiveRoomEntity;
//import com.zjxu.educationapp.modules.mapper.LiveRoomClassMapper;
//import com.zjxu.educationapp.modules.service.LiveRoomService;
//import io.swagger.v3.oas.annotations.Hidden;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//
///**
// * nginx的RTMP模块回调接口
// */
//@RestController
//@RequestMapping("/callback/stream")
//@Slf4j
////隐藏回调接口的接口文档
////@Hidden
//@Tag(name = "直播间状态回调接口")
//public class CallbackController {
//    @Autowired
//    private LiveRoomService liveRoomService;
//    @Autowired
//    private LiveRoomClassMapper liveRoomClassMapper;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    // 推流开始,参数为POST application/x-www-form-urlencoded 格式；参数见doc/images/img.png
//    /*
//    推流开始,参数为POST application/x-www-form-urlencoded 格式；参数见doc/images/img.png
//    流名name参数不包括rtmp请求的查询参数，rtmp://your-server-ip:1935/live/course123?token=abc123 -> name为course123
//    这里使用ResponseEntity设置状态码，让nginx辨别是否应该推流
//     */
//    @Operation(summary = "推流开始")
//    @PostMapping("/on-publish")
//    public ResponseEntity<String> onPublish(@RequestParam String liveId,
//                                       @RequestParam String userId,
//                                       @RequestParam String name) {
//        log.info("开始推流：{}",liveId);
//        return liveRoomService.onPublish(liveId,userId,name);
//    }
//
//    @PostMapping("/on-publish-done")
//    @Transactional
//    @Operation(summary = "结束推流或删除当前直播间")
//    public ResponseEntity<String> onPublishDone(@RequestParam String liveId) {
//        // 此处需要删除关联表内容（live_room_class）
//        log.info("结束推流：{}",liveId);
//        liveRoomService.removeById(liveId);
//        LambdaQueryWrapper<LiveRoomClassEntity> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(LiveRoomClassEntity::getLiveRoomId, liveId);
//        liveRoomClassMapper.delete(queryWrapper);
//        //删除当前直播redis存储的所有在线用户
//        String onlineUsersKey = RedisConstant.LIVE_ROOM_ONLINE_USERS + liveId;
//        stringRedisTemplate.delete(onlineUsersKey);
//        return ResponseEntity.ok("关流成功");
//    }
//}
