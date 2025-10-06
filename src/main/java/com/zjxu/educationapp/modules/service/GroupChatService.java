package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.GroupChatMessageByTeachDTO;
import com.zjxu.educationapp.modules.dto.GroupChatMessageDTO;
import com.zjxu.educationapp.modules.vo.GroupChatHistoryResponseVO;

import java.util.List;

public interface GroupChatService {
    Result<GroupChatHistoryResponseVO> getHistory(Long teamId, Integer pageNum, Integer pageSize);

    Long saveMessage(GroupChatMessageDTO groupChatMessageDTO);

    List<Long> saveAllMessage(GroupChatMessageByTeachDTO messageByTeachDTO);
}
