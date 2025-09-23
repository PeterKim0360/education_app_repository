# 用户聊天功能 API 使用指南

## 概述

本文档介绍用户间一对一聊天功能的API接口使用方法，包括历史消息查询、消息发送和已读状态管理。

## 功能特性

- ✅ 用户间一对一实时聊天（WebSocket）
- ✅ 聊天历史记录存储和查询
- ✅ 分页查询历史消息
- ✅ 消息已读/未读状态管理
- ✅ 支持多种消息类型（文字、图片、文件）
- ✅ 自动生成会话ID确保数据一致性

## 数据库表结构

### user_chat_message 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 消息ID（主键） |
| from_user_id | bigint | 发送者ID |
| to_user_id | bigint | 接收者ID |
| content | text | 消息内容 |
| message_type | tinyint | 消息类型：1-文字 2-图片 3-文件 |
| status | tinyint | 消息状态：0-未读 1-已读 |
| send_time | datetime | 发送时间 |
| conversation_id | varchar(50) | 会话ID（格式：小ID:大ID） |

## API 接口

### 1. 查询聊天历史记录

#### 方法一：POST 请求

**接口地址：** `POST /api/userChat/history`

**请求参数：**
```json
{
  "otherUserId": 123,    // 对方用户ID
  "pageNum": 1,          // 页码，从1开始
  "pageSize": 20         // 每页大小，默认20
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "messages": [
      {
        "id": 1,
        "fromUserId": 456,
        "fromUserName": "张三",
        "fromUserAvatar": "http://example.com/avatar1.jpg",
        "toUserId": 123,
        "toUserName": "李四",
        "toUserAvatar": "http://example.com/avatar2.jpg",
        "content": "你好，最近怎么样？",
        "messageType": 1,
        "status": 1,
        "sendTime": "2025-09-23 14:30:00",
        "isSentByCurrentUser": false
      }
    ],
    "pageNum": 1,
    "pageSize": 20,
    "total": 156,
    "totalPages": 8,
    "hasMore": true,
    "otherUser": {
      "userId": 123,
      "userName": "李四",
      "avatarUrl": "http://example.com/avatar2.jpg",
      "identity": 0
    }
  }
}
```

#### 方法二：GET 请求

**接口地址：** `GET /api/userChat/history`

**请求参数：**
- `otherUserId`：对方用户ID（必填）
- `pageNum`：页码，默认1
- `pageSize`：每页大小，默认20

**示例：**
```
GET /api/userChat/history?otherUserId=123&pageNum=1&pageSize=20
```

### 2. 发送消息（HTTP方式）

**接口地址：** `POST /api/userChat/send`

**请求参数：**
```json
{
  "toUserId": 123,       // 接收者ID
  "content": "你好！",    // 消息内容
  "messageType": 1       // 消息类型，默认1（文字）
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 12345          // 返回消息ID
}
```

### 3. 标记消息为已读

**接口地址：** `POST /api/userChat/markAsRead`

**请求参数：**
- `fromUserId`：发送者ID（将该用户发给当前用户的未读消息标记为已读）

**示例：**
```
POST /api/userChat/markAsRead?fromUserId=123
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 5              // 返回标记为已读的消息数量
}
```

## WebSocket 实时聊天

### 连接地址
```
ws://localhost:8080/single/chat/{toUserId}
```

### 消息格式

#### 发送消息格式

**JSON格式（推荐）：**
```json
{
  "content": "消息内容",
  "messageType": 1
}
```

**纯文本格式（兼容）：**
```
直接发送文本字符串
```

#### 接收消息格式

**成功响应：**
```json
{
  "content": "消息内容",
  "messageType": 1,
  "messageId": 12345,
  "timestamp": 1727075400000,
  "status": "success"
}
```

**错误响应：**
```json
{
  "status": "error",
  "errorMsg": "错误信息",
  "timestamp": 1727075400000
}
```

### 使用说明

1. **建立连接**：客户端连接到WebSocket端点，传入要聊天的目标用户ID
2. **发送消息**：
   - **JSON格式**：发送包含content和messageType的JSON对象，支持不同消息类型
   - **纯文本**：直接发送字符串，会自动识别为文字消息（兼容模式）
3. **接收消息**：
   - **成功响应**：包含messageId、timestamp、status等完整信息的JSON对象
   - **错误响应**：包含错误信息的JSON对象
4. **自动保存**：所有通过WebSocket发送的消息都会自动保存到数据库
5. **消息类型**：支持文字(1)、图片(2)、文件(3)三种类型

### JavaScript 示例

```javascript
// 建立WebSocket连接
const socket = new WebSocket('ws://localhost:8080/single/chat/123');

// 连接打开
socket.onopen = function(event) {
    console.log('WebSocket连接已建立');
};

// 接收消息
socket.onmessage = function(event) {
    console.log('收到消息:', event.data);
    
    try {
        const message = JSON.parse(event.data);
        if (message.status === 'success') {
            console.log('消息发送成功，ID:', message.messageId);
            console.log('消息内容:', message.content);
            console.log('消息类型:', message.messageType);
        } else if (message.status === 'error') {
            console.error('消息发送失败:', message.errorMsg);
        }
    } catch (e) {
        // 兼容旧版本纯文本消息
        console.log('收到文本消息:', event.data);
    }
};

// 发送文字消息
function sendTextMessage(content) {
    if (socket.readyState === WebSocket.OPEN) {
        const message = {
            content: content,
            messageType: 1  // 1-文字消息
        };
        socket.send(JSON.stringify(message));
    }
}

// 发送图片消息
function sendImageMessage(imageUrl) {
    if (socket.readyState === WebSocket.OPEN) {
        const message = {
            content: imageUrl,
            messageType: 2  // 2-图片消息
        };
        socket.send(JSON.stringify(message));
    }
}

// 发送文件消息
function sendFileMessage(fileUrl) {
    if (socket.readyState === WebSocket.OPEN) {
        const message = {
            content: fileUrl,
            messageType: 3  // 3-文件消息
        };
        socket.send(JSON.stringify(message));
    }
}

// 兼容发送纯文本（会自动识别为文字消息）
function sendPlainText(content) {
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(content);  // 直接发送字符串
    }
}

// 连接关闭
socket.onclose = function(event) {
    console.log('WebSocket连接已关闭');
};

// 连接错误
socket.onerror = function(error) {
    console.error('WebSocket错误:', error);
};
```

## 使用场景和建议

### 典型使用流程

1. **进入聊天页面**：
   - 调用历史消息查询API获取历史记录
   - 建立WebSocket连接进行实时通信

2. **发送消息**：
   - 优先使用WebSocket发送（实时性更好）
   - HTTP接口作为备选方案

3. **消息已读**：
   - 用户查看消息后调用标记已读API

### 性能优化建议

1. **分页查询**：建议每页20-50条消息，避免一次加载过多数据
2. **索引优化**：已为常用查询字段添加索引
3. **连接管理**：及时关闭不需要的WebSocket连接
4. **消息缓存**：可以在客户端缓存最近的聊天记录

### 错误处理

- **400错误**：请求参数错误，检查参数格式和必填字段
- **401错误**：用户未登录，需要先进行身份验证
- **500错误**：服务器内部错误，检查日志定位问题

## 注意事项

1. **认证要求**：所有API都需要用户登录认证
2. **会话ID生成**：系统自动生成，确保两用户间会话的唯一性
3. **消息类型**：当前支持文字、图片、文件三种类型
4. **数据一致性**：WebSocket和HTTP接口共享同一套数据存储
5. **并发安全**：支持多用户并发聊天，数据隔离安全 