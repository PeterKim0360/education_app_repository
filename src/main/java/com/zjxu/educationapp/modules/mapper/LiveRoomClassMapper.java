package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.LiveRoomClassEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Kim-Peter
* @description 针对表【live_room_class】的数据库操作Mapper
* @createDate 2025-09-18 21:12:34
* @Entity com.zjxu.educationapp.modules.entity.LiveRoomClassEntity
*/
public interface LiveRoomClassMapper extends BaseMapper<LiveRoomClassEntity> {

    /**
     * 批量插入直播间与班级的关联关系
     * @param liveRoomClassList 关联关系列表
     * @return 插入的记录数
     */
    int insertBatch(@Param("list") List<LiveRoomClassEntity> liveRoomClassList);
}




