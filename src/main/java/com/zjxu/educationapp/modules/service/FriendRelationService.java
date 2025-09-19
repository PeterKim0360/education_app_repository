package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.entity.FriendRelation;
import com.zjxu.educationapp.modules.dto.FriendDTO;
import com.zjxu.educationapp.modules.vo.FriendApplicationVO;
import com.zjxu.educationapp.modules.vo.FriendVO;
import com.zjxu.educationapp.common.utils.Result;

import java.util.List;

public interface FriendRelationService extends IService<FriendRelation> {
    /**
     * 添加好友
     */
    Result<String> addFriend(FriendDTO friendDTO, Long currentUserId);

    /**
     * 获取好友申请列表
     */
    Result<List<FriendApplicationVO>> getFriendApplications(Long currentUserId);

    /**
     * 获取我的好友列表
     */
    Result<List<FriendVO>> getMyFriends(Long currentUserId);

    /**
     * 同意好友申请
     */
    Result<String> acceptFriendApplication(Long applicationId, Long currentUserId);

    /**
     * 拒绝好友申请
     */
    Result<String> rejectFriendApplication(Long applicationId, Long currentUserId);

    /**
     * 删除好友
     */
    Result<String> deleteFriend(Long friendId, Long currentUserId);
}
