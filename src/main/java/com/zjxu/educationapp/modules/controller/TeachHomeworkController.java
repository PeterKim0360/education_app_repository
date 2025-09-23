package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.HomeworkSubmissionDTO;
import com.zjxu.educationapp.modules.dto.StuHWSubmitDTO;
import com.zjxu.educationapp.modules.dto.TeachCreateHomeworkDTO;

import com.zjxu.educationapp.modules.service.TeachHomeworkService;
import com.zjxu.educationapp.modules.vo.*;
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
    public Result<?> delCreateHW(@RequestParam("homeworkIds") List<Long> homeworkIds){
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
     * AI 创建作业
     */
    @Operation(summary = "AI 创建作业")
    @PostMapping("/create/ai")
    @Transactional
    public Result<?> createHWByAI(String msg){
        log.info("AI 创建作业");
        return teachHomeworkService.createHWByAI(msg);
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
     * 查看所有待批改作业
     */
    @Operation(summary = "查看所有待批改作业",description = "可选：page,size")
    @GetMapping("/uncorrect/sim/list")
    public Result<List<TeachUnCorrectSimHWVO>> queryUnCorSimList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看待批改作业,page:{},size:{}",page,size);
        return teachHomeworkService.queryUnCorSimList(page,size);
    }

    /**
     * 查看待批改作业详情
     */
    @Operation(summary = "查看待批改作业详情",description = "传参：subjectId,homeworkId")
    @GetMapping("/uncorrect/list")
    public Result<List<HomeworkSubmissionVO>> queryUnCorDetailList(
            @RequestParam("subjectId") Integer subjectId,
            @RequestParam("homeworkId") Long homeworkId){
        log.info("查看待批改作业,subjectId:{},homeworkId:{}",subjectId,homeworkId);
        return teachHomeworkService.queryUnCorDetailList(subjectId,homeworkId);
    }

    /**
     * 批改作业
     */
    @Operation(summary = "批改作业",description = "传参：homeworkSubmissionDTO")
    @PostMapping("/correct")
    @Transactional
    public Result<?> correctHW(@RequestBody HomeworkSubmissionDTO homeworkSubmissionDTO){
        log.info("批改作业,作业ID为：{}",homeworkSubmissionDTO);
        return teachHomeworkService.correctHW(homeworkSubmissionDTO);
    }

    /**
     * 查看已批改作业列表
     */
    @Operation(summary = "查看已批改作业列表",description = "传参：subjectId,homeworkId")
    @GetMapping("/correct/list")
    public Result<List<CorrectVO>> queryCorrectList(
            @RequestParam("subjectId") Integer subjectId,
            @RequestParam("homeworkId") Long homeworkId){
        log.info("查看已批改作业列表,subjectId:{},homeworkId:{}",subjectId,homeworkId);
        return teachHomeworkService.queryCorrectList(subjectId,homeworkId);
    }

    /**
     * AI批改作业（图片提交）
     */
    //TODO 待完善
    @Operation(summary = "AI批改作业", description = "传参：stuHWSubmitDTO")
    @PostMapping("/correct/ai")
    @Transactional
    public Result<?> correctHWByAI(@RequestBody StuHWSubmitDTO stuHWSubmitDTO) {
        log.info("AI批改作业:{}", stuHWSubmitDTO);
        return teachHomeworkService.correctHWByAI(stuHWSubmitDTO);
    }

}
