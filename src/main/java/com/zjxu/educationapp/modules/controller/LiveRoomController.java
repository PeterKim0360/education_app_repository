package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zjxu.educationapp.common.constant.RedisConstant;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
import com.zjxu.educationapp.modules.service.LiveRoomService;
import com.zjxu.educationapp.modules.vo.LiveRoomDetailVO;
import com.zjxu.educationapp.modules.vo.OnlineUserVO;
import com.zjxu.educationapp.modules.vo.TeacherLiveRoomVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/live/room")
@Tag(name = "直播间相关接口")
@Slf4j
public class LiveRoomController {
    @Autowired
    private LiveRoomService liveRoomService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/teacher/create")
    @Operation(summary = "老师创建直播间", description = "参数见下\"请求参数\"")
    public Result<?> createLiveRoom(@RequestBody LiveRoomDTO liveRoomDTO) {
        log.info("创建直播间：{}", liveRoomDTO);
        return liveRoomService.createLiveRoom(liveRoomDTO);
    }

    @GetMapping("/teacher/stream/key/{liveId}")
    @Operation(summary = "老师获取直播间推流码")
    public Result<String> getStreamKey(@PathVariable int liveId) {
        log.info("获取直播间推流码：{}", liveId);
        return liveRoomService.getStreamKey(liveId);
    }

    @GetMapping("/teacher/list")
    @Operation(summary = "老师获取其创建的所有直播间")
    public Result<List<TeacherLiveRoomVO>> getTeacherLiveRooms() {
        log.info("老师获取其创建的所有直播间");
        return liveRoomService.getTeacherLiveRooms();
    }

    @GetMapping("/student/page")
    @Operation(summary = "学生获取其班级所有学科直播间")
    public Result<?> getLiveRoom() {
        log.info("学生获取其班级所有学科直播间");
        return liveRoomService.getLiveRoom();
    }

    @GetMapping("/detail/{liveId}")
    @Operation(summary = "获取直播间详细信息", description = "通用接口，适用于老师和学生")
    public Result<LiveRoomDetailVO> getLiveRoomDetail(@PathVariable Integer liveId) {
        log.info("获取直播间详细信息：{}", liveId);
        return liveRoomService.getLiveRoomDetail(liveId);
    }

    @GetMapping("/student/count/{liveId}")
    @Operation(summary = "统计在线人数")
    public Result<Integer> countOnlineUsers(@PathVariable Integer liveId) {
        log.info("统计在线人数");
        String onlineUsersKey = RedisConstant.LIVE_ROOM_ONLINE_USERS + liveId;
        Long cnt = stringRedisTemplate.opsForSet().size(onlineUsersKey);
        return Result.ok(cnt==null?0:cnt.intValue());
    }

    @PostMapping("/student/enter/{liveId}")
    @Operation(summary = "学生进入直播间", description = "增加在线人数")
    public Result<?> studentEnterLiveRoom(@PathVariable Integer liveId) {
        log.info("学生进入直播间");
        String onlineUsersKey = RedisConstant.LIVE_ROOM_ONLINE_USERS + liveId;
        stringRedisTemplate.opsForSet().add(onlineUsersKey, StpUtil.getLoginIdAsString());
        return Result.ok();
    }

    @PostMapping("/student/exit/{liveId}")
    @Operation(summary = "学生退出直播间", description = "减少在线人数")
    public Result<String> studentExitLiveRoom(@PathVariable Integer liveId) {
        log.info("学生退出直播间：{}", liveId);
        return liveRoomService.studentExitLiveRoom(liveId);
    }

    @GetMapping("/online-users/{liveId}")
    @Operation(summary = "获取直播间在线用户列表", description = "返回当前直播间所有在线用户的详细信息")
    public Result<List<OnlineUserVO>> getOnlineUsers(@PathVariable Integer liveId) {
        log.info("获取直播间在线用户列表，liveId: {}", liveId);
        return liveRoomService.getOnlineUsers(liveId);
    }
}
