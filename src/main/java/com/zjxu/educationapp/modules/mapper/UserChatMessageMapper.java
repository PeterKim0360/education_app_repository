package com.zjxu.educationapp.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjxu.educationapp.modules.entity.UserChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author system
 * @description 针对表【user_chat_message(用户聊天消息记录)】的数据库操作Mapper
 * @Entity com.zjxu.educationapp.modules.entity.UserChatMessage
 */
@Mapper
public interface UserChatMessageMapper extends BaseMapper<UserChatMessage> {

    /**
     * 根据会话ID分页查询聊天记录
     * @param conversationId 会话ID
     * @param offset 偏移量
     * @param pageSize 页面大小
     * @return 聊天记录列表
     */
    List<UserChatMessage> selectByConversationIdWithPaging(@Param("conversationId") String conversationId,
                                                            @Param("offset") Integer offset,
                                                            @Param("pageSize") Integer pageSize);

    /**
     * 统计两用户间的消息总数
     * @param conversationId 会话ID
     * @return 消息总数
     */
    Long countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 标记消息为已读
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 更新的记录数
     */
    Integer markMessagesAsRead(@Param("fromUserId") Long fromUserId, 
                              @Param("toUserId") Long toUserId);

    List<UserChatMessage> selectChatFriendsIdAsTo(@Param("currentUserId") Long currentUserId);

    List<UserChatMessage> selectChatFriendsIdAsFrom(Long currentUserId);

}