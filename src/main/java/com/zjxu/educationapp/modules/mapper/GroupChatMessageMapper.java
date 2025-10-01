package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.GroupChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huawei
* @description 针对表【group_chat_message(小组讨论区消息表)】的数据库操作Mapper
* @createDate 2025-10-01 19:22:50
* @Entity com.zjxu.educationapp.modules.entity.GroupChatMessage
*/
@Mapper
public interface GroupChatMessageMapper extends BaseMapper<GroupChatMessage> {

}




