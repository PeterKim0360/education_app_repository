package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.*;
import com.zjxu.educationapp.modules.service.GroupService;
import com.zjxu.educationapp.modules.vo.GroupTeamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 分组接口
 */
@RestController
@RequestMapping(value = "/group")
@Slf4j
@Tag(name = "分组接口")
public class GroupController {
    @Resource
    private GroupService groupService;

    /**
     * 生成小组
     */
    @Operation(summary = "生成小组", description = "传参：generateGroupDTO")
    @PostMapping("/generate/groups")
    public Result<?> generateGroups(@RequestBody GenerateGroupDTO generateGroupDTO) {
        log.info("生成小组");
        return groupService.generateGroups(generateGroupDTO);
    }

//    /**
//     * 查看小组列表
//     */
//    @Operation(summary = "查看小组列表", description = "传参：groupTeamDTO")
//    @GetMapping("/list")
//    public Result<List<GroupTeamVO>> getGroupList(@RequestBody GroupTeamDTO groupTeamDTO) {
//        log.info("查看小组列表");
//        return groupService.getMemberList(groupTeamDTO);
//    }


    /**
     * 自动分配未入组成员
     */
    @Operation(summary = "自由分配未入组成员", description = "传参：groupNoDTO")
    @PostMapping("/free/assign")
    public Result<?> freeAssign(@RequestBody GroupNoDTO groupNoDTO) {
        log.info("自由分配未入组成员");
        return groupService.freeAssign(groupNoDTO);
    }

    /**
     * 学生退出
     */
    @Operation(summary = "学生退出", description = "传参：studentQuitDTO")
    @PostMapping("/quit")
    public Result<?> quit(@RequestBody StudentQuitDTO studentQuitDTO) {
        log.info("学生退出");
        return groupService.quit(studentQuitDTO);
    }

    /**
     * 学生加入
     */
    @Operation(summary = "学生加入", description = "传参：joinStuDTO")
    @PostMapping("/join/free")
    public Result<?> joinFree(@RequestBody JoinStuDTO joinStuDTO) {
        log.info("学生自由加入");
        return groupService.joinFree(joinStuDTO);
    }

    /**
     * 结束分组
     */
    @Operation(summary = "结束分组", description = "传参：subjectId,createdBy")
    @PostMapping("/end")
    public Result<?> end(@RequestParam("subjectId") Integer subjectId,
                         @RequestParam("createdBy") Long createdBy) {
        log.info("结束分组");
        return groupService.end(subjectId,createdBy);
    }

    /**
     * 获取小组列表
     */
    @Operation(summary = "获取小组列表", description = "传参：subjectId,createdBy")
    @GetMapping("/member/list")
    public Result<List<GroupTeamVO>> getMemberList(@RequestParam("subjectId") Integer subjectId,
                                                       @RequestParam("createdBy") Long createdBy) {
        log.info("查看小组成员信息");
        return groupService.getMemberList(subjectId,createdBy);
    }

//    /**
//     * 创建小组规则
//     */
//    @Operation(summary = "创建小组规则", description = "传参：groupingPlanDTO")
//    @PostMapping("/create/plan")
//    public Result<?> createGroupingPlan(@RequestBody GroupingPlanDTO groupingPlanDTO) {
//        log.info("创建小组规则");
//        return groupService.createGroupingPlan(groupingPlanDTO);
//    }

//    /**
//     * 保存小组（最终结果）
//     */
//    @Operation(summary = "保存小组", description = "传参：saveGroupDTO")
//    @PostMapping("/save")
//    public Result<?> saveGroup(@RequestBody SaveGroupDTO saveGroupDTO) {
//        log.info("保存小组");
//        return groupService.saveGroup(saveGroupDTO);
//    }


}
