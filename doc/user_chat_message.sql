-- 用户聊天消息表
CREATE TABLE `user_chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `to_user_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '消息类型：1-文字消息 2-图片消息 3-文件消息',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '消息状态：0-未读 1-已读',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `conversation_id` varchar(50) NOT NULL COMMENT '会话标识，格式：较小userId:较大userId',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_from_user_id` (`from_user_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户聊天消息记录';

-- 添加复合索引，优化查询性能
ALTER TABLE `user_chat_message` ADD INDEX `idx_conversation_time` (`conversation_id`, `send_time` DESC);
ALTER TABLE `user_chat_message` ADD INDEX `idx_to_user_status` (`to_user_id`, `status`); 