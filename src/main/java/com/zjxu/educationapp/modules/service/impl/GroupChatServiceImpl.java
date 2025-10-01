package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.GroupChatMessageDTO;
import com.zjxu.educationapp.modules.entity.GroupChatMessage;
import com.zjxu.educationapp.modules.mapper.GroupChatMessageMapper;
import com.zjxu.educationapp.modules.service.GroupChatService;
import com.zjxu.educationapp.modules.vo.GroupChatHistoryResponseVO;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupChatServiceImpl implements GroupChatService {

    @Autowired
    private GroupChatMessageMapper groupChatMessageMapper;

    /**
     * 获取小组历史消息
     * @param teamId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result<GroupChatHistoryResponseVO> getHistory(Long teamId, Integer pageNum, Integer pageSize) {
        // 创建Page对象
        Page<GroupChatMessage> page = new Page<>(pageNum, pageSize);

        // 使用QueryWrapper进行条件查询
        QueryWrapper<GroupChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId).orderByDesc("send_time");

        // 执行分页查询
        Page<GroupChatMessage> result = groupChatMessageMapper.selectPage(page, queryWrapper);

        // 构建返回结果
        GroupChatHistoryResponseVO historyResponseVO = GroupChatHistoryResponseVO.builder()
                .messages(result.getRecords().stream()
                        .map(m -> GroupChatHistoryResponseVO.MessageVO.builder()
                                .id(m.getId())
                                .fromUserId(m.getFromUserId())
                                .content(m.getContent())
                                .messageType(m.getMessageType())
                                .sendTime(m.getSendTime())
                                .build())
                        .collect(Collectors.toList()))
                .pageNum((int) result.getCurrent())
                .pageSize((int) result.getSize())
                .total(result.getTotal())
                .totalPages((int) result.getPages())
                .hasMore(result.hasNext())
                .build();
        return Result.ok(historyResponseVO);
    }

    /**
     * 保存小组消息
     * @param groupChatMessageDTO
     * @return
     */
    @Override
    public Long saveMessage(GroupChatMessageDTO groupChatMessageDTO) {
        GroupChatMessage message = GroupChatMessage.builder()
                .teamId(groupChatMessageDTO.getTeamId())
                .fromUserId(groupChatMessageDTO.getFromUserId())
                .content(groupChatMessageDTO.getContent())
                .messageType(groupChatMessageDTO.getMessageType() == null ? 1 : groupChatMessageDTO.getMessageType())
                .sendTime(new Date())
                .build();
        groupChatMessageMapper.insert(message);
        return message.getId();
    }
}
