package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.LiveRoomService;
import com.zjxu.educationapp.modules.vo.StudentLiveRoomVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kim-Peter
 * @description 针对表【live_room】的数据库操作Service实现
 * @createDate 2025-09-17 21:32:01
 */
@Service
@Slf4j
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoomEntity>
        implements LiveRoomService {
    @Autowired
    private LiveRoomMapper liveRoomMapper;
    @Autowired
    private LiveRoomClassMapper liveRoomClassMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Override
    public Result<?> createLiveRoom(LiveRoomDTO liveRoomDTO) {
        LiveRoomEntity liveRoomEntity = new LiveRoomEntity();
        BeanUtil.copyProperties(liveRoomDTO, liveRoomEntity);
        liveRoomEntity.setUserId(StpUtil.getLoginIdAsLong());
        this.save(liveRoomEntity);

        // 如果传入了班级ID列表，则批量插入直播间与班级的关联关系
        List<Long> classIds = liveRoomDTO.getClassIds();
        if (classIds != null && !classIds.isEmpty()) {
            List<LiveRoomClassEntity> liveRoomClassList = new ArrayList<>();
            for (Long classId : classIds) {
                LiveRoomClassEntity liveRoomClassEntity = new LiveRoomClassEntity();
                liveRoomClassEntity.setClassId(classId);
                liveRoomClassEntity.setLiveRoomId(liveRoomEntity.getLiveId());
                liveRoomClassList.add(liveRoomClassEntity);
            }
            // 批量插入所有关联关系，只进行一次数据库通信
            liveRoomClassMapper.insertBatch(liveRoomClassList);
        }
        return Result.ok("创建直播间成功");
    }

    @Override
    public Result<String> getStreamKey(int liveId) {
        LiveRoomEntity isExist = this.getOne(new LambdaUpdateWrapper<LiveRoomEntity>().eq(LiveRoomEntity::getLiveId, liveId));
        if (isExist == null) {
            return Result.error("直播间不存在");
        }
        String streamKey = updateStreamKey(liveId);
        return Result.ok(streamKey);
    }

    private String updateStreamKey(int liveId) {
        String uuid = UUID.randomUUID().toString();
        //OBS中推流码最前面不能是'/'
        String streamKey = uuid + "?" + "userId=" + StpUtil.getLoginId().toString() + "&liveId=" + liveId;
        LambdaUpdateWrapper<LiveRoomEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(LiveRoomEntity::getLiveId, liveId);
        //推流码此处存入数据库不包括查询参数，等同于流名
        lambdaUpdateWrapper.set(LiveRoomEntity::getStreamKey, uuid);
        liveRoomMapper.update(null, lambdaUpdateWrapper);
        return streamKey;
    }

    @Override
    public ResponseEntity<String> onPublish(String liveId, String userId, String name) {
        //需要进行判断流名是否正确，否则推送失败，设置错误响应码
        long count = this.count(new LambdaUpdateWrapper<LiveRoomEntity>()
                .eq(LiveRoomEntity::getLiveId, liveId)
                .eq(LiveRoomEntity::getUserId, userId)
                .eq(LiveRoomEntity::getStreamKey, name));
        if (count == 0) {
            //流名不正确
            log.info("推流码不正确");
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("推流码不正确");
        }
        LambdaUpdateWrapper<LiveRoomEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(LiveRoomEntity::getLiveId, liveId);
        lambdaUpdateWrapper.set(LiveRoomEntity::getStartTime, new Date()).set(LiveRoomEntity::getStatus, 1);
        this.update(null, lambdaUpdateWrapper);
        log.info("推流成功");
        return ResponseEntity.status(HttpServletResponse.SC_OK).body("推流成功");
    }

    /*
    此处两种方案，一种多表联查（6张表），一种分开通过代码形式单独查
     */
    @Override
    public Result<List<StudentLiveRoomVO>> getLiveRoom() {
        long userId = StpUtil.getLoginIdAsLong();
        // 使用多表联查直接获取直播间信息
        List<StudentLiveRoomVO> studentLiveRoomVOS = liveRoomMapper.getLiveRoomsByStudentId(userId);
        return Result.ok(studentLiveRoomVOS);
    }
//    @Override
//    public Result<List<StudentLiveRoomVO>> getLiveRoom() {
//        long userId = StpUtil.getLoginIdAsLong();
//        //获取班级，从而获取当前班级所对应的所有直播间，一个直播间可能对应多个班级
//        StudentClassEntity studentClassEntity = studentClassMapper.selectOne(new LambdaUpdateWrapper<StudentClassEntity>().eq(StudentClassEntity::getStudentId, userId));
//        long classId = studentClassEntity.getClassId();
//        List<LiveRoomClassEntity> liveRoomClassEntities = liveRoomClassMapper.selectList(new LambdaQueryWrapper<LiveRoomClassEntity>().eq(LiveRoomClassEntity::getClassId, classId));
//        List<Integer> LiveRoomIds = liveRoomClassEntities.stream().map(LiveRoomClassEntity::getLiveRoomId).toList();
//        //当前班级对应的所有直播间
//        List<LiveRoomEntity> liveRoomEntities = liveRoomMapper.selectBatchIds(LiveRoomIds);
//        String className = classMapper.selectOne(new LambdaQueryWrapper<ClassEntity>().eq(ClassEntity::getClassId, classId)).getClassName();
//        List<StudentLiveRoomVO> studentLiveRoomVOS = new ArrayList<>();
//        for (LiveRoomEntity liveRoomEntity : liveRoomEntities) {
//            String teacherName = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, liveRoomEntity.getUserId())).getUserName();
//            String subjectName = subjectsMapper.selectOne(new LambdaQueryWrapper<Subjects>().eq(Subjects::getSubjectId, liveRoomEntity.getSubjectId())).getSubjectName();
//
//            StudentLiveRoomVO studentLiveRoomVO = new StudentLiveRoomVO();
//            studentLiveRoomVO.setLiveId(liveRoomEntity.getLiveId());
//            studentLiveRoomVO.setRoomName(liveRoomEntity.getRoomName());
//            studentLiveRoomVO.setClassName(className);
//            studentLiveRoomVO.setTeacherName(teacherName);
//            studentLiveRoomVO.setSubjectName(subjectName);
//            studentLiveRoomVO.setStartTime(liveRoomEntity.getStartTime());
//            studentLiveRoomVO.setStatus(liveRoomEntity.getStatus());
//            studentLiveRoomVOS.add(studentLiveRoomVO);
//        }
//        return Result.ok(studentLiveRoomVOS);
//    }
}




