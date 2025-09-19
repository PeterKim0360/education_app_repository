package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
import com.zjxu.educationapp.modules.entity.LiveRoomEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

/**
* @author Kim-Peter
* @description 针对表【live_room】的数据库操作Service
* @createDate 2025-09-17 21:32:01
*/
public interface LiveRoomService extends IService<LiveRoomEntity> {

    Result<?> createLiveRoom(LiveRoomDTO liveRoomDTO);

    Result<String> getStreamKey(int liveId);

    ResponseEntity<String> onPublish(String liveId, String userId, String name);

    Result<?> getLiveRoom();
}
