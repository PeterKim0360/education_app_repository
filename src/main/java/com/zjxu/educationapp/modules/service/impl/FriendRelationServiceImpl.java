package com.zjxu.educationapp.modules.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.modules.entity.FriendRelation;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.mapper.FriendRelationMapper;
import com.zjxu.educationapp.modules.service.FriendRelationService;
import com.zjxu.educationapp.modules.service.UserService;
import com.zjxu.educationapp.modules.dto.FriendDTO;
import com.zjxu.educationapp.modules.vo.FriendApplicationVO;
import com.zjxu.educationapp.modules.vo.FriendVO;
import com.zjxu.educationapp.common.utils.Result;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class FriendRelationServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation>
        implements FriendRelationService {

    @Autowired
    private UserService userService;

    @Override
    public Result<String> addFriend(FriendDTO friendDTO, Long currentUserId) {
        // 不能添加自己为好友
        if (currentUserId.equals(friendDTO.getFriendId())) {
            return Result.error("不能添加自己为好友");
        }

        // 检查用户是否存在
        UserEntity friend = userService.getById(friendDTO.getFriendId());
        if (friend == null) {
            return Result.error("用户不存在");
        }

        // 检查是否已经是好友关系
        LambdaQueryWrapper<FriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendRelation::getUserId, currentUserId)
                .eq(FriendRelation::getFriendId, friendDTO.getFriendId());
        FriendRelation curFriendRelation = this.getOne(queryWrapper);
        if (curFriendRelation != null && curFriendRelation.getStatus() != 2) {
            //有记录并且不是被拒绝
            return Result.error("已经发起了申请好友");
        }
        // 检查对方是否已经申请过我
        LambdaQueryWrapper<FriendRelation> reverseQueryWrapper = new LambdaQueryWrapper<>();
        reverseQueryWrapper.eq(FriendRelation::getUserId, friendDTO.getFriendId())
                .eq(FriendRelation::getFriendId, currentUserId);
        FriendRelation reverseRelation = this.getOne(reverseQueryWrapper);
        if (reverseRelation != null) {
            // 如果对方已经申请过我，直接通过好友申请
            if (reverseRelation.getStatus() == 0) { // 状态为待确认
                // 更新对方的申请状态为已确认
                reverseRelation.setStatus(1);
                this.updateById(reverseRelation);

                // 创建我的好友关系记录，状态也设为已确认
                FriendRelation myRelation = new FriendRelation();
                myRelation.setUserId(currentUserId);
                myRelation.setFriendId(friendDTO.getFriendId());
                myRelation.setRemark(friendDTO.getRemark());
                myRelation.setStatus(1); // 直接设为已确认
                this.save(myRelation);

                return Result.ok("对方也对你发起了好友申请，已自动通过好友申请");
            } else if (reverseRelation.getStatus() == 1) {
                return Result.error("你们已经是好友了");
            }
        }
        // 创建好友申请记录（状态为待确认）
        FriendRelation friendRelation = new FriendRelation();
        friendRelation.setUserId(currentUserId);
        friendRelation.setFriendId(friendDTO.getFriendId());
        friendRelation.setRemark(friendDTO.getRemark());
        friendRelation.setStatus(0); // 0-待确认
        if (curFriendRelation != null && curFriendRelation.getStatus() == 2) {
            update(friendRelation,
                    new LambdaUpdateWrapper<FriendRelation>()
                            .eq(FriendRelation::getUserId, friendRelation.getUserId())
                            .eq(FriendRelation::getFriendId, friendRelation.getFriendId()));
        } else {
            this.save(friendRelation);
        }
        return Result.ok("好友申请已发送");
    }

    @Override
    public Result<List<FriendApplicationVO>> getFriendApplications(Long currentUserId) {
        // 查询所有申请添加当前用户为好友的记录（包括所有状态）
        LambdaQueryWrapper<FriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendRelation::getFriendId, currentUserId).orderByDesc(FriendRelation::getUpdateTime);
        List<FriendRelation> applications = this.list(queryWrapper);

        // 转换为VO对象
        List<FriendApplicationVO> result = applications.stream().map(application -> {
            FriendApplicationVO vo = new FriendApplicationVO();
            vo.setId(application.getId());
            vo.setApplicantId(application.getUserId());
            vo.setRemark(application.getRemark());
            vo.setCreateTime(application.getUpdateTime());
            vo.setStatus(application.getStatus()); // 添加状态字段

            // 获取申请人信息
            UserEntity applicant = userService.getById(application.getUserId());
            if (applicant != null) {
                vo.setApplicantName(applicant.getUserName());
                vo.setApplicantAvatar(applicant.getAvatarUrl());
            }

            return vo;
        }).collect(Collectors.toList());

        result = result.stream().filter(vo -> !StrUtil.isEmpty(vo.getRemark())).toList();
        return Result.ok(result);
    }

    @Override
    public Result<List<FriendVO>> getMyFriends(Long currentUserId) {
        // 查询我的好友（状态为已确认）
        LambdaQueryWrapper<FriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendRelation::getUserId, currentUserId)
                .eq(FriendRelation::getStatus, 1); // 1-已确认
        List<FriendRelation> friends = this.list(queryWrapper);

        // 转换为VO对象
        List<FriendVO> result = friends.stream().map(friendRelation -> {
            FriendVO vo = new FriendVO();
            BeanUtils.copyProperties(friendRelation, vo);
            vo.setFriendId(friendRelation.getFriendId());

            // 获取好友信息
            UserEntity friend = userService.getById(friendRelation.getFriendId());
            if (friend != null) {
                vo.setFriendName(friend.getUserName());
                vo.setFriendAvatar(friend.getAvatarUrl());
            }

            return vo;
        }).collect(Collectors.toList());
        // 获取中文排序器
        Collator collator = Collator.getInstance(Locale.CHINA);
        // 设置排序强度，PRIMARY 表示只考虑基本字母，忽略大小写和音调等差异
        collator.setStrength(Collator.PRIMARY);
        // 按好友名称排序
        //NOTE 此处展示了如何中英文混合排序，并且忽略了大小写
        result.sort(Comparator.comparing(FriendVO::getFriendName, collator));
        return Result.ok(result);
    }

    @Transactional
    @Override
    public Result<String> acceptFriendApplication(Long applicationId, Long currentUserId) {
        // 查询申请记录
        FriendRelation application = this.getById(applicationId);
        if (application == null) {
            return Result.error("申请记录不存在");
        }

        // 检查是否是当前用户的好友申请
        if (!application.getFriendId().equals(currentUserId)) {
            return Result.error("无权限操作");
        }

        // 更新申请状态为已确认
        application.setStatus(1); // 1-已确认
        this.updateById(application);

        FriendRelation reverseFriendRelation = getOne(new LambdaUpdateWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId, application.getFriendId())
                .eq(FriendRelation::getFriendId, application.getUserId()));
        // 创建反向好友关系（我也成为对方的好友）
        FriendRelation reverseRelation = new FriendRelation();
        reverseRelation.setUserId(currentUserId);
        reverseRelation.setFriendId(application.getUserId());
        reverseRelation.setStatus(1); // 1-已确认
        reverseRelation.setRemark(""); // 可以留空或设置默认备注
        if (reverseFriendRelation != null && reverseFriendRelation.getStatus() == 2) {
            update(reverseRelation,
                    new LambdaUpdateWrapper<FriendRelation>()
                            .eq(FriendRelation::getUserId, reverseRelation.getUserId())
                            .eq(FriendRelation::getFriendId, reverseRelation.getFriendId()));
        } else {
            this.save(reverseRelation);
        }
        return Result.ok("已同意好友申请");
    }

    @Override
    public Result<String> rejectFriendApplication(Long applicationId, Long currentUserId) {
        // 查询申请记录
        FriendRelation application = this.getById(applicationId);
        if (application == null) {
            return Result.error("申请记录不存在");
        }

        // 检查是否是当前用户的好友申请
        if (!application.getFriendId().equals(currentUserId)) {
            return Result.error("无权限操作");
        }

        // 更新申请状态为已拒绝
        application.setStatus(2); // 2-已拒绝
        this.updateById(application);

        return Result.ok("已拒绝好友申请");
    }

    @Override
    public Result<String> deleteFriend(Long friendId, Long currentUserId) {
        // 不能删除自己
        if (currentUserId.equals(friendId)) {
            return Result.error("不能删除自己");
        }

        // 检查是否存在好友关系（双向检查）
        int count = this.baseMapper.checkFriendRelation(currentUserId, friendId);

        // 如果没有找到好友关系
        if (count == 0) {
            return Result.error("好友关系不存在");
        }

        // 删除所有相关的好友关系记录
        this.baseMapper.deleteFriendRelation(currentUserId, friendId);

        return Result.ok("删除成功");
    }
}
