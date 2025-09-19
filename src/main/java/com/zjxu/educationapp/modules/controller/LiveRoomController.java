package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
import com.zjxu.educationapp.modules.service.LiveRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live/room")
@Tag(name = "直播间相关接口")
@Slf4j
public class LiveRoomController {
    @Autowired
    private LiveRoomService liveRoomService;
    @PostMapping("/teacher/create")
    @Operation(summary = "老师创建直播间",description = "参数见下\"请求参数\"")
    public Result<?> createLiveRoom(@RequestBody LiveRoomDTO liveRoomDTO) {
        log.info("创建直播间：{}",liveRoomDTO);
        return liveRoomService.createLiveRoom(liveRoomDTO);
    }

    @GetMapping("/teacher/stream/key/{liveId}")
    @Operation(summary = "老师获取直播间推流码")
    public Result<String> getStreamKey(@PathVariable int liveId) {
        log.info("获取直播间推流码：{}",liveId);
        return liveRoomService.getStreamKey(liveId);
    }

    @GetMapping("/student/page")
    @Operation(summary = "学生获取其班级所有学科直播间")
    public Result<?> getLiveRoom() {
        log.info("学生获取其班级所有学科直播间");
        return liveRoomService.getLiveRoom();
    }

}
