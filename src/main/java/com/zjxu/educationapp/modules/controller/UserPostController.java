package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.UserPostCommentDTO;
import com.zjxu.educationapp.modules.dto.UserPostDTO;
import com.zjxu.educationapp.modules.service.UserPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 广场动态
 */
@Tag(name = "广场动态相关接口")
@RestController
@RequestMapping("/square")
@Slf4j
public class UserPostController {
    @Autowired
    private UserPostService userPostService;

    @Operation(summary = "分页查询动态",description = "分页参数可选：page默认值1，size默认值5")
    @GetMapping("/post")
    public Result getPost(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        log.info("分页查询动态");
        //按时间进行分页查询
        return userPostService.getPost(page, size);
    }
    /*
    "/post/detail","/post/comment" 会被拦截，动态详情页需要登录后才能查看
     */

    /**
     * 查询指定动态
     * @param postId
     * @return
     */
    @Operation(summary = "查询指定动态(动态详情)",description = "postId必传")
    @GetMapping("/post/detail/{postId}")
    public Result postDetail(@PathVariable Integer postId) {
        log.info("查询动态详情");
        return userPostService.postDetail(postId);
    }

    @Operation(summary = "分页查询评论",description = "postId必传；分页参数可选：page默认值1，size默认值5")
    @GetMapping("/post/comment")
    public Result postComment(Integer postId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "5") Integer size) {
        log.info("postId:{},page:{},size:{}", postId, page, size);
        log.info("查询动态评论");
        return userPostService.postCommentByGet(postId, page, size);
    }


    @Operation(summary = "发布动态", description = "所有参数必传")
    @PostMapping("/post")
    public Result<?> postByPost(@RequestBody UserPostDTO userPostDTO) {
        log.info("发布动态:{}",userPostDTO);
        return userPostService.postByPost(userPostDTO);
    }

    @Operation(summary = "发布评论", description = "所有参数必传")
    @PostMapping("/post/comment")
    public Result<?> postComment(@RequestBody UserPostCommentDTO userPostDTO) {
        log.info("发布评论:{}",userPostDTO);
        return userPostService.postCommentByPost(userPostDTO);
    }
}
