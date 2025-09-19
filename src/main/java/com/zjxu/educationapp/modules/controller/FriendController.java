package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.modules.service.FriendRelationService;
import com.zjxu.educationapp.modules.dto.FriendDTO;
import com.zjxu.educationapp.modules.vo.FriendApplicationVO;
import com.zjxu.educationapp.modules.vo.FriendVO;
import com.zjxu.educationapp.common.utils.Result;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@Slf4j
@Tag(name = "好友模块")
public class FriendController {

    @Autowired
    private FriendRelationService friendRelationService;

    /**
     * 添加好友
     */
    @Operation(summary = "添加好友", description = "所有必传，remark代表添加好友时的备注验证信息")
    @PostMapping("/add")
    public Result<String> addFriend(@RequestBody FriendDTO friendDTO) {
        log.info("添加好友，好友ID：{}", friendDTO.getFriendId());
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.addFriend(friendDTO, currentUserId);
    }

    /**
     * 查看谁发起了好友申请（查看我的好友申请）
     */
    @Operation(summary = "新的朋友申请", description = "申请状态status：0-待确认，1-已确认，2-已拒绝")
    @GetMapping("/applications")
    public Result<List<FriendApplicationVO>> getFriendApplications() {
        log.info("查看好友申请列表");
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.getFriendApplications(currentUserId);
    }

    /**
     * 查看我的好友
     */
    @Operation(summary = "查看我的好友")
    @GetMapping("/list")
    public Result<List<FriendVO>> getMyFriends() {
        log.info("查看我的好友列表");
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.getMyFriends(currentUserId);
    }

    /**
     * 同意好友申请
     */
    @Operation(summary = "同意好友申请", description = "applicationId:申请ID")
    @PostMapping("/accept/{applicationId}")
    public Result<String> acceptFriendApplication(@PathVariable Long applicationId) {
        log.info("同意好友申请，申请ID：{}", applicationId);
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.acceptFriendApplication(applicationId, currentUserId);
    }

    /**
     * 拒绝好友申请
     */
    @Operation(summary = "拒绝好友申请", description = "applicationId:申请ID")
    @PostMapping("/reject/{applicationId}")
    public Result<String> rejectFriendApplication(@PathVariable Long applicationId) {
        log.info("拒绝好友申请，申请ID：{}", applicationId);
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.rejectFriendApplication(applicationId, currentUserId);
    }

    /**
     * 删除好友
     */
    @Operation(summary = "删除好友", description = "friendId:好友ID")
    @DeleteMapping("/{friendId}")
    public Result<String> deleteFriend(@PathVariable Long friendId) {
        log.info("删除好友，好友ID：{}", friendId);
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return friendRelationService.deleteFriend(friendId, currentUserId);
    }
}
