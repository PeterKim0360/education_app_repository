package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.LiveRoomEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjxu.educationapp.modules.vo.StudentLiveRoomVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Kim-Peter
* @description 针对表【live_room】的数据库操作Mapper
* @createDate 2025-09-17 21:32:01
* @Entity com.zjxu.educationapp.modules.entity.LiveRoomEntity
*/
@Mapper
public interface LiveRoomMapper extends BaseMapper<LiveRoomEntity> {

    List<StudentLiveRoomVO> getLiveRoomsByStudentId(long userId);
}




