package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
import com.zjxu.educationapp.modules.entity.LiveRoomEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.LiveRoomDetailVO;
import com.zjxu.educationapp.modules.vo.OnlineUserVO;
import com.zjxu.educationapp.modules.vo.TeacherLiveRoomVO;
import org.springframework.http.ResponseEntity;

import java.util.List;

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

    /**
     * 老师获取其创建的所有直播间
     * @return 直播间列表
     */
    Result<List<TeacherLiveRoomVO>> getTeacherLiveRooms();

    /**
     * 获取直播间详细信息（通用接口，适用于老师和学生）
     * @param liveRoomId 直播间ID
     * @return 直播间详细信息
     */
    Result<LiveRoomDetailVO> getLiveRoomDetail(Integer liveRoomId);

    /**
     * 学生退出直播间，减少在线人数
     * @param liveRoomId 直播间ID
     * @return 在线人数
     */
    Result<String> studentExitLiveRoom(Integer liveRoomId);
    
    /**
     * 获取直播间在线用户列表
     * @param liveRoomId 直播间ID
     * @return 在线用户列表
     */
    Result<List<OnlineUserVO>> getOnlineUsers(Integer liveRoomId);
}
