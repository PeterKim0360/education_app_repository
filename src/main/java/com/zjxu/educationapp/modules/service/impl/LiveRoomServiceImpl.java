//package com.zjxu.educationapp.modules.service.impl;
//
//import cn.dev33.satoken.stp.StpUtil;
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.zjxu.educationapp.common.constant.RedisConstant;
//import com.zjxu.educationapp.common.utils.Result;
//import com.zjxu.educationapp.modules.dto.LiveRoomDTO;
//import com.zjxu.educationapp.modules.entity.*;
//import com.zjxu.educationapp.modules.mapper.*;
//import com.zjxu.educationapp.modules.service.LiveRoomService;
//import com.zjxu.educationapp.modules.vo.LiveRoomDetailVO;
//import com.zjxu.educationapp.modules.vo.StudentLiveRoomVO;
//import com.zjxu.educationapp.modules.vo.TeacherLiveRoomVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author Kim-Peter
// * @description 针对表【live_room】的数据库操作Service实现
// * @createDate 2025-09-17 21:32:01
// */
//@Service
//@Slf4j
//public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoomEntity>
//        implements LiveRoomService {
//    @Autowired
//    private LiveRoomMapper liveRoomMapper;
//    @Autowired
//    private LiveRoomClassMapper liveRoomClassMapper;
//    @Autowired
//    private StudentClassMapper studentClassMapper;
//    @Autowired
//    private ClassMapper classMapper;
//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private SubjectsMapper subjectsMapper;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    private static final String prefixUrl = "rtmp://121.41.176.238:1935/live/";
////    private static final String prefixUrl = "rtmp://192.168.88.130:1935/live/";
//
//    //    // 线程池管理器，可以开启异步线程执行任务
////    @Autowired
////    private ThreadPoolTaskExecutor asyncExecutor;
//    @Override
//    public Result<?> createLiveRoom(LiveRoomDTO liveRoomDTO) {
//        LiveRoomEntity liveRoomEntity = new LiveRoomEntity();
//        BeanUtil.copyProperties(liveRoomDTO, liveRoomEntity);
//        liveRoomEntity.setUserId(StpUtil.getLoginIdAsLong());
//        this.save(liveRoomEntity);
//
//        // 如果传入了班级ID列表，则批量插入直播间与班级的关联关系
//        List<Long> classIds = liveRoomDTO.getClassIds();
//        if (classIds != null && !classIds.isEmpty()) {
//            List<LiveRoomClassEntity> liveRoomClassList = new ArrayList<>();
//            for (Long classId : classIds) {
//                LiveRoomClassEntity liveRoomClassEntity = new LiveRoomClassEntity();
//                liveRoomClassEntity.setClassId(classId);
//                liveRoomClassEntity.setLiveRoomId(liveRoomEntity.getLiveId());
//                liveRoomClassList.add(liveRoomClassEntity);
//            }
//            // 批量插入所有关联关系，只进行一次数据库通信
//            liveRoomClassMapper.insertBatch(liveRoomClassList);
//        }
//        return Result.ok("创建直播间成功");
//    }
//
//    @Override
//    public Result<String> getStreamKey(int liveId) {
//        LiveRoomEntity isExist = this.getOne(new LambdaUpdateWrapper<LiveRoomEntity>().eq(LiveRoomEntity::getLiveId, liveId));
//        if (isExist == null) {
//            return Result.error("直播间不存在");
//        }
//        String streamKey = updateStreamKey(liveId);
//        return Result.ok(streamKey);
//    }
//
//    private String updateStreamKey(int liveId) {
//        String uuid = UUID.randomUUID().toString();
//        //OBS中推流码最前面不能是'/'
//        String streamKey = uuid + "?" + "userId=" + StpUtil.getLoginId().toString() + "&liveId=" + liveId;
//        LambdaUpdateWrapper<LiveRoomEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(LiveRoomEntity::getLiveId, liveId);
//        //推流码此处存入数据库不包括查询参数，等同于流名
//        lambdaUpdateWrapper.set(LiveRoomEntity::getStreamKey, uuid);
//        lambdaUpdateWrapper.set(LiveRoomEntity::getRtmpUrl, prefixUrl + streamKey);
//        liveRoomMapper.update(null, lambdaUpdateWrapper);
//        return streamKey;
//    }
//
//    @Override
//    public ResponseEntity<String> onPublish(String liveId, String userId, String name) {
//        //需要进行判断流名是否正确，否则推送失败，设置错误响应码
//        long count = this.count(new LambdaUpdateWrapper<LiveRoomEntity>()
//                .eq(LiveRoomEntity::getLiveId, liveId)
//                .eq(LiveRoomEntity::getUserId, userId)
//                .eq(LiveRoomEntity::getStreamKey, name));
////        if (count == 0) {
////            //流名不正确
////            log.info("推流码不正确");
////            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("推流码不正确");
////        }
//        LambdaUpdateWrapper<LiveRoomEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(LiveRoomEntity::getLiveId, liveId);
//        lambdaUpdateWrapper.set(LiveRoomEntity::getStartTime, new Date()).set(LiveRoomEntity::getStatus, 1);
//        this.update(null, lambdaUpdateWrapper);
//        log.info("推流成功");
//        return ResponseEntity.status(HttpServletResponse.SC_OK).body("推流成功");
//    }
//
//    @Override
//    public Result<List<TeacherLiveRoomVO>> getTeacherLiveRooms() {
//        // 获取当前登录老师的ID
//        long teacherId = StpUtil.getLoginIdAsLong();
//
//        // 查询该老师创建的所有直播间
//        List<LiveRoomEntity> liveRoomEntities = this.list(new LambdaQueryWrapper<LiveRoomEntity>()
//                .eq(LiveRoomEntity::getUserId, teacherId)
//                .orderByDesc(LiveRoomEntity::getCreateTime));
//
//        // 转换为VO对象
//        List<TeacherLiveRoomVO> teacherLiveRoomVOS = new ArrayList<>();
//        for (LiveRoomEntity liveRoomEntity : liveRoomEntities) {
//            TeacherLiveRoomVO vo = new TeacherLiveRoomVO();
//            vo.setLiveId(liveRoomEntity.getLiveId());
//            vo.setStatus(liveRoomEntity.getStatus());
//            vo.setRoomName(liveRoomEntity.getRoomName());
//            vo.setDescription(liveRoomEntity.getDescription());
//            vo.setStartTime(liveRoomEntity.getStartTime());
//
//            // 获取学科名称
//            Subjects subject = subjectsMapper.selectById(liveRoomEntity.getSubjectId());
//            if (subject != null) {
//                vo.setSubjectName(subject.getSubjectName());
//            }
//
//            // 获取关联的班级名称集合
//            List<LiveRoomClassEntity> liveRoomClassEntities = liveRoomClassMapper.selectList(
//                    new LambdaQueryWrapper<LiveRoomClassEntity>()
//                            .eq(LiveRoomClassEntity::getLiveRoomId, liveRoomEntity.getLiveId())
//            );
//
//            List<Long> classIds = liveRoomClassEntities.stream()
//                    .map(LiveRoomClassEntity::getClassId)
//                    .collect(Collectors.toList());
//
//            List<String> classNames = new ArrayList<>();
//            if (!classIds.isEmpty()) {
//                List<ClassEntity> classEntities = classMapper.selectBatchIds(classIds);
//                classNames = classEntities.stream()
//                        .map(ClassEntity::getClassName)
//                        .collect(Collectors.toList());
//            }
//            vo.setClassNames(classNames);
//
//            teacherLiveRoomVOS.add(vo);
//        }
//
//        return Result.ok(teacherLiveRoomVOS);
//    }
//
//    @Override
//    public Result<LiveRoomDetailVO> getLiveRoomDetail(Integer liveRoomId) {
//        // 查询直播间信息
//        LiveRoomEntity liveRoomEntity = this.getById(liveRoomId);
//        if (liveRoomEntity == null) {
//            return Result.error("直播间不存在");
//        }
//
//        // 构造返回VO
//        LiveRoomDetailVO liveRoomDetailVO = new LiveRoomDetailVO();
//        liveRoomDetailVO.setLiveId(liveRoomEntity.getLiveId());
//        liveRoomDetailVO.setUserId(liveRoomEntity.getUserId());
//        liveRoomDetailVO.setSubjectId(liveRoomEntity.getSubjectId());
//        liveRoomDetailVO.setStatus(liveRoomEntity.getStatus());
//        liveRoomDetailVO.setRoomName(liveRoomEntity.getRoomName());
//        liveRoomDetailVO.setDescription(liveRoomEntity.getDescription());
//        liveRoomDetailVO.setRtmpUrl(liveRoomEntity.getRtmpUrl());
//        liveRoomDetailVO.setStartTime(liveRoomEntity.getStartTime());
//
//        // 获取老师姓名
//        UserEntity userEntity = userMapper.selectById(liveRoomEntity.getUserId());
//        if (userEntity != null) {
//            liveRoomDetailVO.setTeacherName(userEntity.getUserName());
//        }
//
//        // 获取学科名称
//        Subjects subject = subjectsMapper.selectById(liveRoomEntity.getSubjectId());
//        if (subject != null) {
//            liveRoomDetailVO.setSubjectName(subject.getSubjectName());
//        }
//
//        // 获取直播间关联的班级信息
//        List<LiveRoomClassEntity> liveRoomClassEntities = liveRoomClassMapper.selectList(
//                new LambdaQueryWrapper<LiveRoomClassEntity>()
//                        .eq(LiveRoomClassEntity::getLiveRoomId, liveRoomId)
//        );
//
//        // 提取班级ID集合
//        List<Long> classIds = liveRoomClassEntities.stream()
//                .map(LiveRoomClassEntity::getClassId)
//                .collect(Collectors.toList());
//        liveRoomDetailVO.setClassIds(classIds);
//
//        // 获取班级名称集合
//        List<String> classNames = new ArrayList<>();
//        if (!classIds.isEmpty()) {
//            List<ClassEntity> classEntities = classMapper.selectBatchIds(classIds);
//            classNames = classEntities.stream()
//                    .map(ClassEntity::getClassName)
//                    .collect(Collectors.toList());
//        }
//        liveRoomDetailVO.setClassNames(classNames);
//
//        return Result.ok(liveRoomDetailVO);
//    }
//
//    /*
//    此处两种方案，一种多表联查（6张表），一种分开通过代码形式单独查
//     */
//    @Override
//    public Result<List<StudentLiveRoomVO>> getLiveRoom() {
//        long userId = StpUtil.getLoginIdAsLong();
//        // 使用多表联查直接获取直播间信息
//        List<StudentLiveRoomVO> studentLiveRoomVOS = liveRoomMapper.getLiveRoomsByStudentId(userId);
//        return Result.ok(studentLiveRoomVOS);
//    }
//
//
////    @Override
////    public Result<List<StudentLiveRoomVO>> getLiveRoom() {
////        long userId = StpUtil.getLoginIdAsLong();
////        //获取班级，从而获取当前班级所对应的所有直播间，一个直播间可能对应多个班级
////        StudentClassEntity studentClassEntity = studentClassMapper.selectOne(new LambdaUpdateWrapper<StudentClassEntity>().eq(StudentClassEntity::getStudentId, userId));
////        long classId = studentClassEntity.getClassId();
////        List<LiveRoomClassEntity> liveRoomClassEntities = liveRoomClassMapper.selectList(new LambdaQueryWrapper<LiveRoomClassEntity>().eq(LiveRoomClassEntity::getClassId, classId));
////        List<Integer> LiveRoomIds = liveRoomClassEntities.stream().map(LiveRoomClassEntity::getLiveRoomId).toList();
////        //当前班级对应的所有直播间
////        List<LiveRoomEntity> liveRoomEntities = liveRoomMapper.selectBatchIds(LiveRoomIds);
////        String className = classMapper.selectOne(new LambdaQueryWrapper<ClassEntity>().eq(ClassEntity::getClassId, classId)).getClassName();
////        List<StudentLiveRoomVO> studentLiveRoomVOS = new ArrayList<>();
////        for (LiveRoomEntity liveRoomEntity : liveRoomEntities) {
////            String teacherName = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, liveRoomEntity.getUserId())).getUserName();
////            String subjectName = subjectsMapper.selectOne(new LambdaQueryWrapper<Subjects>().eq(Subjects::getSubjectId, liveRoomEntity.getSubjectId())).getSubjectName();
////
////            StudentLiveRoomVO studentLiveRoomVO = new StudentLiveRoomVO();
////            studentLiveRoomVO.setLiveId(liveRoomEntity.getLiveId());
////            studentLiveRoomVO.setRoomName(liveRoomEntity.getRoomName());
////            studentLiveRoomVO.setClassName(className);
////            studentLiveRoomVO.setTeacherName(teacherName);
////            studentLiveRoomVO.setSubjectName(subjectName);
////            studentLiveRoomVO.setStartTime(liveRoomEntity.getStartTime());
////            studentLiveRoomVO.setStatus(liveRoomEntity.getStatus());
////            studentLiveRoomVOS.add(studentLiveRoomVO);
////        }
////        return Result.ok(studentLiveRoomVOS);
////    }
//
//    @Override
//    public Result<String> studentExitLiveRoom(Integer liveRoomId) {
//        // 获取当前登录用户ID
//        long userId = StpUtil.getLoginIdAsLong();
//        // 检查直播间是否存在
//        LiveRoomEntity liveRoom = this.getById(liveRoomId);
//        if (liveRoom == null) {
//            return Result.error("直播间不存在");
//        }
//        // 构造Redis key
//        String onlineUsersKey = RedisConstant.LIVE_ROOM_ONLINE_USERS + liveRoomId;
//        // 从直播间在线用户集合中移除用户ID
//        stringRedisTemplate.opsForSet().remove(onlineUsersKey, String.valueOf(userId));
//        return Result.ok("退出直播间成功");
//    }
//}
//
//
//
//
