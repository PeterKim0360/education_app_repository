package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.zjxu.educationapp.modules.service.StuHomeworkService;
import com.zjxu.educationapp.modules.vo.StuHomeWorkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生作业相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/stu/homework")
@Tag(name = "学生作业相关接口")
public class StuHomeworkController {
    @Autowired
    private StuHomeworkService stuHomeworkService;

    /**
     * 未完成作业的分页查询
     */
    @Operation(summary = "未完成作业的分页查询",description = "可选：page,size,subjectId")
    @GetMapping
    public Result<IPage<StuHomeWorkVO>> queryUnComplete(
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("未完成作业的分页查询,page:{},size:{}",page,size);
        return stuHomeworkService.queryUnComplete(subjectId,page,size);
    }

    /**
     * 删除过期的作业(可批量)
     */
    @Operation(summary = "删除过期的作业",description = "传参：homeworkIds")
    @DeleteMapping("/del")
    public Result<?> delOutTime(@RequestParam("homeworkIds") List<Long> homeworkIds){
        log.info("删除过期的作业(可批量)");
        return stuHomeworkService.delOutTime(homeworkIds);
    }

    /**
     * 查看该学科已完成但未批改的作业
     */
    @Operation(summary = "查看该学科已完成但未批改的作业",description = "传参：subjectId,可选：page,size")
    @GetMapping("/cmpl/uncor")
    public Result<IPage<StuHomeWorkVO>> queryCmplUnCor(
            @RequestParam("subjectId") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看该学科已完成但未批改的作业");
        return stuHomeworkService.queryCmplUnCor(subjectId,page,size);
    }

    /**
     * 查看该学科已完成并已批改的作业
     */
    @Operation(summary = "查看该学科已完成并已批改的作业",description = "传参：subjectId,可选：page,size")
    @GetMapping("/cmpl/cor")
    public Result<IPage<StuHomeWorkVO>> queryCmplCor(
            @RequestParam("subjectId") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看该学科已完成并已批改的作业");
        return stuHomeworkService.queryCmplCor(subjectId,page,size);
    }
}
