package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.GroupChatMessageDTO;
import com.zjxu.educationapp.modules.vo.GroupChatHistoryResponseVO;

public interface GroupChatService {
    Result<GroupChatHistoryResponseVO> getHistory(Long teamId, Integer pageNum, Integer pageSize);

    Long saveMessage(GroupChatMessageDTO groupChatMessageDTO);
}
