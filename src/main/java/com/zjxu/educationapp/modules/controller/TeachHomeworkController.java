package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeachCreateHomeworkDTO;

import com.zjxu.educationapp.modules.service.TeachHomeworkService;
import com.zjxu.educationapp.modules.vo.TeachCreateHWDetailVO;
import com.zjxu.educationapp.modules.vo.TeachCreateHWSimpleVO;
import com.zjxu.educationapp.modules.vo.TeachSendHWDetailVO;
import com.zjxu.educationapp.modules.vo.TeachSendHWSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老师作业相关接口
 */
@RestController
@RequestMapping("/api/teach/homework")
@Tag(name = "老师作业相关接口")
@Slf4j
public class TeachHomeworkController {
    @Autowired
    private TeachHomeworkService teachHomeworkService;

    /**
     * 创建作业
     */
    @Operation(summary = "创建作业",description = "传参：teachSendHomeworkDTO")
    @PostMapping("/create")
    @Transactional
    public Result<?> createHomework(@RequestBody TeachCreateHomeworkDTO teachCreateHomeworkDTO){
        log.info("创建作业:{}",teachCreateHomeworkDTO);
        return teachHomeworkService.createHomework(teachCreateHomeworkDTO);
    }

    /**
     * 查看已创建的作业
     */
    @Operation(summary = "查看已创建的作业")
    @GetMapping("/create/list")
    public Result<IPage<TeachCreateHWSimpleVO>> queryCreateList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看已创建的作业");
        return teachHomeworkService.queryCreateList(page,size);
    }

    /**
     *查看已创建作业详情
     */
    @Operation(summary = "查看已创建作业详情",description = "传参：homeworkId")
    @GetMapping("/create/find")
    public Result<TeachCreateHWDetailVO> findCreateHW(@RequestParam("homeworkId") Long homeworkId){
        log.info("查看已创建作业详情的作业ID：{}",homeworkId);
        return teachHomeworkService.findCreateHW(homeworkId);
    }

    /**
     * 编辑作业
     */
    @Operation(summary = "编辑作业",description = "传参：teachSendHomeworkDTO")
    @PostMapping("/create/edit")
    @Transactional
    public Result<?> editCreateHW(@RequestBody TeachCreateHomeworkDTO teachCreateHomeworkDTO){
        log.info("编辑作业,ID为:{}",teachCreateHomeworkDTO);
        return teachHomeworkService.editCreateHW(teachCreateHomeworkDTO);
    }

    /**
     *删除已创建的作业 (含批量)
     */
    @Operation(summary = "删除已创建的作业 (含批量)",description = "传参：homeworkId")
    @DeleteMapping("/create/del")
    @Transactional
    public Result<?> delCreateHW(@RequestParam("homeworkId") List<Long> homeworkIds){
        log.info("删除已创建的作业 (含批量),ID为：{}",homeworkIds);
        return teachHomeworkService.delCreateHW(homeworkIds);
    }

    /**
     * 发布作业
     */
    @Operation(summary = "发布作业")
    @PostMapping("/send")
    @Transactional
    public Result<?> sendHW(@RequestParam("homeworkId") Long homeworkId){
        log.info("发布作业");
        return teachHomeworkService.sendHW(homeworkId);
    }

    /**
     * 删除已发布的作业(含批量)
     */
    @Operation(summary = "删除已发布的作业(含批量)")
    @DeleteMapping("/send/del")
    @Transactional
    public Result<?> delSendHW(@RequestParam("homeworkId") List<Long> homeworkIds){
        log.info("删除已发布的作业,ID为：{}",homeworkIds);
        return teachHomeworkService.delSendHW(homeworkIds);
    }

    /**
     * 查询已发布但未截止的作业
     */
    @Operation(summary = "查询已发布但未截止的作业",description = "可选：page,size")
    @GetMapping("/send/list")
    public Result<IPage<TeachSendHWSimpleVO>> querySendList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看已发布的作业,page:{},size:{}",page,size);
        return teachHomeworkService.querySendList(page,size);
    }

    /**
     * 查看已发布的作业详情
     */
    @Operation(summary = "查看已发布的作业详情",description = "传参：homeworkId")
    @GetMapping("/send/find")
    public Result<TeachSendHWDetailVO> findSendHW(@RequestParam("homeworkId") Long homeworkId){
        log.info("查看已发布的作业详情,作业ID为：{}",homeworkId);
        return teachHomeworkService.findSendHW(homeworkId);
    }
    /**
     * 批改作业
     */

}
