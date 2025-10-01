package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.modules.entity.GroupChatMessage;
import com.zjxu.educationapp.modules.service.GroupChatMessageService;
import com.zjxu.educationapp.modules.mapper.GroupChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author huawei
* @description 针对表【group_chat_message(小组讨论区消息表)】的数据库操作Service实现
* @createDate 2025-10-01 19:22:50
*/
@Service
public class GroupChatMessageServiceImpl extends ServiceImpl<GroupChatMessageMapper, GroupChatMessage>
    implements GroupChatMessageService{

}




