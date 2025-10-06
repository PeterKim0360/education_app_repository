package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.*;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.GroupService;
import com.zjxu.educationapp.modules.vo.GroupTeamVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupingPlanMapper groupingPlanMapper;
    @Autowired
    private SubjectClassTeachMapper subjectClassTeachMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private GroupTeamMapper groupTeamMapper;
    @Autowired
    private GroupTeamMemberMapper groupTeamMemberMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private UserMapper userMapper;

    private final Map<Long,Object> lockMap=new ConcurrentHashMap<>();

    /**
     * 生成小组
     */
    @Override
    @Transactional
    public Result<?> generateGroups(GenerateGroupDTO generateGroupDTO) {
        Integer subjectId = generateGroupDTO.getSubjectId();
        //获取老师id
        long createdBy = StpUtil.getLoginIdAsLong();
        //先判断课程是否已经生成小组
        List<GroupTeam> groupTeams = groupTeamMapper.selectList(new LambdaQueryWrapper<GroupTeam>()
                .eq(GroupTeam::getSubjectId, subjectId)
                .eq(GroupTeam::getCreatedBy, createdBy)
                .eq(GroupTeam::getStatus, 0)
                .eq(GroupTeam::getLogicalDel, 0));
        if (groupTeams!=null && !groupTeams.isEmpty()){
            //说明已经生成了小组，删除之前的小组，进行重新生成
            for (GroupTeam groupTeam : groupTeams){
                groupTeam.setStatus(1);
                groupTeam.setLogicalDel(1);
                groupTeamMapper.updateById(groupTeam);
            }
        }
        //找到课程的班级信息
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, generateGroupDTO.getSubjectId())
                .eq(SubjectClassTeach::getTeachId, createdBy)
                .eq(SubjectClassTeach::getStatus, 1));
        List<Long> classIds = subjectClassTeaches.stream().map(SubjectClassTeach::getClassId).toList();
        //统计这门课程的总人数
        int totalStudentCount = 0;
        for (Long classId : classIds) {
            totalStudentCount+=studentClassMapper.selectCount(new LambdaQueryWrapper<StudentClassEntity>()
                    .eq(StudentClassEntity::getClassId, classId)
                    .eq(StudentClassEntity::getStatus, 1));
        }
        Integer targetTeamCount = generateGroupDTO.getTargetTeamCount();
//        //保存到plan中
//        GroupingPlan groupingPlan = new GroupingPlan();
//        groupingPlan.setTargetTeamCount(targetTeamCount);
//        groupingPlanMapper.insert(groupingPlan);
        //计算小组理想人数（均分）
//        int capacity = Math.max(2, totalStudentCount / targetTeamCount);
        int capacity = totalStudentCount / targetTeamCount;
        if (capacity<2){
            return Result.error("请重新选择目标队伍数量");
        }
        //如果总人数大于分组的组数*capacity，那就加组
        if (totalStudentCount>capacity*targetTeamCount){
           targetTeamCount++;
        }
        Subjects subjects = subjectsMapper.selectById(subjectId);
        String subjectName = subjects.getSubjectName();
        for (int i = 1; i <= targetTeamCount; i++) {
            GroupTeam groupTeam = new GroupTeam();
            groupTeam.setSubjectId(generateGroupDTO.getSubjectId());
            groupTeam.setName(subjectName+"小组"+i);
            groupTeam.setCapacity(capacity);
            groupTeam.setCreatedBy(createdBy);
            groupTeamMapper.insert(groupTeam);
        }
        return Result.ok();
    }



    /**
     * 自由分配未入组成员
     */
    @Override
    public Result<?> freeAssign(GroupNoDTO groupNoDTO) {
        if (groupNoDTO==null){
            log.error("参数不能为空");
            return Result.error("参数不能为空");
        }
        Integer subjectId = groupNoDTO.getSubjectId();
        Long teacherId = groupNoDTO.getCreatedBy();
        //获取未选组的学生
        //1.获取该课程的班级id
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, subjectId)
                .eq(SubjectClassTeach::getTeachId, teacherId)
                .eq(SubjectClassTeach::getStatus, 1));
        List<Long> classIds = subjectClassTeaches.stream().map(SubjectClassTeach::getClassId).toList();
        List<Long> allStudentIds=new ArrayList<>();
        int totalStudentCount = 0;
        //2.获取该课程的学生id
        for (Long classId : classIds) {
            List<StudentClassEntity> studentClassEntities = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClassEntity>()
                    .eq(StudentClassEntity::getClassId, classId)
                    .eq(StudentClassEntity::getStatus,1));
            totalStudentCount+=studentClassEntities.size();
            List<Long> studentIds = studentClassEntities.stream().map(StudentClassEntity::getStudentId).toList();
            allStudentIds.addAll(studentIds);
        }
        log.info("该课程的学生id：{}",allStudentIds);
        //查看未满的小组
        List<GroupTeam> groups = groupTeamMapper.selectList(new LambdaQueryWrapper<GroupTeam>()
                .eq(GroupTeam::getSubjectId, subjectId)
                .eq(GroupTeam::getCreatedBy, teacherId)
                .eq(GroupTeam::getStatus, 0)
                .eq(GroupTeam::getLogicalDel, 0)
                .apply("capacity > current_num"));
        log.info("未满的小组：{}",groups);
        //3.获取未选小组的学生id
        Iterator<Long> iterator = allStudentIds.iterator();
        while (iterator.hasNext()) {
            Long studentId = iterator.next();
            long count = groupTeamMemberMapper.selectCount(new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getUserId, studentId)
                    .eq(GroupTeamMember::getStatus, 1));
            log.info("{}", count);
            if (count != 0) {
                // 使用迭代器的remove方法
                iterator.remove();
            }
        }
        log.info("未选小组的id：{}",allStudentIds);
        //随机打散未入组学生,进行洗牌
        Collections.shuffle(allStudentIds);
        // 将未选小组的id加入到未满小组中
        for (GroupTeam group : groups) {
            Long teamId = group.getId();
            Integer capacity = group.getCapacity();
            if (teamId == null) {
                log.error("小组ID不能为空");
                return Result.error("小组ID不能为空");
            }
            GroupTeam groupTeam = groupTeamMapper.selectById(teamId);
            if (groupTeam.getStatus() == 1) {
                log.error("小组已锁定");
                return Result.error("小组已锁定");
            }

            // 获取当前小组成员信息
            List<GroupTeamMember> groupTeamMembers = groupTeamMemberMapper.selectList(new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getTeamId, teamId)
                    .eq(GroupTeamMember::getStatus, 1)
                    .orderByAsc(GroupTeamMember::getMemberIndex));

            // 创建位置占用映射
            Set<Integer> occupiedPositions = new HashSet<>();
            for (GroupTeamMember member : groupTeamMembers) {
                occupiedPositions.add(member.getMemberIndex());
            }

            // 使用迭代器安全遍历学生列表
            Iterator<Long> iterator1 = allStudentIds.iterator();
            while (iterator1.hasNext() && groupTeamMembers.size() < capacity) {
                Long studentId = iterator1.next();

                // 寻找第一个空缺位置
                for (int i = 0; i < capacity; i++) {
                    if (!occupiedPositions.contains(i)) {
                        // 找到空位置，分配学生
                        groupTeam.setCurrentNum(groupTeam.getCurrentNum() + 1);
                        groupTeamMapper.updateById(groupTeam);

                        // 插入小组成员
                        GroupTeamMember groupTeamMember = new GroupTeamMember();
                        groupTeamMember.setTeamId(teamId);
                        groupTeamMember.setUserId(studentId);
                        groupTeamMember.setWorkId(groupTeam.getWorkId());
                        groupTeamMember.setMemberIndex(i);

                        // 第一个成员设为队长
                        if (groupTeamMembers.isEmpty() && occupiedPositions.isEmpty()) {
                            groupTeamMember.setRole(1);
                            groupTeam.setLeaderId(studentId);
                            groupTeamMapper.updateById(groupTeam);
                        } else {
                            groupTeamMember.setRole(2);
                        }

                        groupTeamMemberMapper.insert(groupTeamMember);
                        occupiedPositions.add(i);
                        groupTeamMembers.add(groupTeamMember);

                        // 从待分配列表中移除
                        iterator1.remove();
                        break;
                    }
                }
            }
        }

        return Result.ok();
    }


    /**
     * 学生退出
     */
    @Override
    public Result<?> quit(StudentQuitDTO studentQuitDTO) {
        Long teamId = studentQuitDTO.getTeamId();
        Long userId = studentQuitDTO.getUserId();
        Boolean isLeader = studentQuitDTO.getIsLeader();
        //获取小组信息
        GroupTeam groupTeam = groupTeamMapper.selectById(teamId);
        //小组当前人数-1
        Integer currentNum = groupTeam.getCurrentNum();
        groupTeam.setCurrentNum(currentNum-1);
        //如果当前用户是队长，修改小组信息表的队长ID为空
        if (isLeader){
            groupTeam.setLeaderId(null);
        }
        log.info( "修改小组信息:{}",groupTeam);
        groupTeamMapper.updateById(groupTeam);
        //将该用户对应的小组信息删除
        GroupTeamMember member = groupTeamMemberMapper.selectOne(new LambdaQueryWrapper<GroupTeamMember>()
                .eq(GroupTeamMember::getTeamId, teamId)
                .eq(GroupTeamMember::getUserId, userId)
                .eq(GroupTeamMember::getStatus, 1));
        member.setStatus(0);
        groupTeamMemberMapper.updateById(member);
        return Result.ok();
    }

    /**
     * 结束分组
     */
    @Override
    @Transactional
    public Result<?> end(Integer subjectId,Long createdBy) {
        // 批量更新小组状态
        groupTeamMapper.update(null, new LambdaUpdateWrapper<GroupTeam>()
                .eq(GroupTeam::getSubjectId, subjectId)
                .eq(GroupTeam::getCreatedBy, createdBy)
                .eq(GroupTeam::getStatus, 0)
                .set(GroupTeam::getStatus, 1));
        // 批量更新小组成员状态
        List<Long> teamIds = groupTeamMapper.selectList(new LambdaQueryWrapper<GroupTeam>()
                        .eq(GroupTeam::getSubjectId, subjectId)
                        .eq(GroupTeam::getCreatedBy, createdBy)
                        .eq(GroupTeam::getStatus, 1))
                .stream()
                .map(GroupTeam::getId)
                .toList();
        if (!teamIds.isEmpty()) {
            groupTeamMemberMapper.update(null, new LambdaUpdateWrapper<GroupTeamMember>()
                    .in(GroupTeamMember::getTeamId, teamIds)
                    .eq(GroupTeamMember::getStatus, 1)
                    .set(GroupTeamMember::getStatus, 0));
        }
        return Result.ok();
    }

    /**
     * 获取小组列表
     */
    @Override
    public Result<List<GroupTeamVO>> getMemberList(Integer subjectId, Long createdBy) {
        //获取小组id
        List<GroupTeam> groupTeams = groupTeamMapper.selectList(new LambdaQueryWrapper<GroupTeam>()
                .eq(GroupTeam::getSubjectId, subjectId)
                .eq(GroupTeam::getCreatedBy, createdBy)
                .eq(GroupTeam::getStatus, 0)
                .eq(GroupTeam::getLogicalDel, 0));
        log.info("小组列表:{}",groupTeams);
        List<Long> teamIds = groupTeams.stream().map(GroupTeam::getId).toList();
        List<GroupTeamVO> list = new ArrayList<>();
        for (Long teamId : teamIds) {
            GroupTeam groupTeam = groupTeamMapper.selectById(teamId);
            GroupTeamVO groupTeamVO = new GroupTeamVO();
            groupTeamVO.setId(teamId);
            groupTeamVO.setName(groupTeam.getName());
            groupTeamVO.setCapacity(groupTeam.getCapacity());
            groupTeamVO.setCurrentCount(groupTeam.getCurrentNum());

            List<GroupTeamMember> groupTeamMembers = groupTeamMemberMapper.selectList(new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getTeamId, teamId)
                    .eq(GroupTeamMember::getStatus, 1)
                    .orderByAsc(GroupTeamMember::getMemberIndex));
//            List<StudentDTO> students = new ArrayList<>(groupTeamMembers.stream().map(groupTeamMember -> {
//                StudentDTO studentDTO = new StudentDTO();
//                UserEntity userEntity = userMapper.selectById(groupTeamMember.getUserId());
//                studentDTO.setId(Math.toIntExact(userEntity.getId()));
//                studentDTO.setName(userEntity.getUserName());
//                studentDTO.setAvatarUrl(userEntity.getAvatarUrl());
//                studentDTO.setIsLeader(groupTeamMember.getRole() == 1);
//                studentDTO.setIndex(groupTeamMember.getIndex());
//                return studentDTO;
//            }).toList());
            List<StudentDTO> students = new ArrayList<>();
            int num=0;
            for (int i = 0; i < groupTeam.getCapacity(); i++) {
                if (num>=groupTeamMembers.size()){
                    students.add(null);
                    num++;
                    continue;
                }
                if (i!=groupTeamMembers.get(num).getMemberIndex()){
                    students.add(null);
                    continue;
                }else {
                    GroupTeamMember groupTeamMember = groupTeamMembers.get(num);
                    StudentDTO studentDTO = new StudentDTO();
                    UserEntity userEntity = userMapper.selectById(groupTeamMember.getUserId());
                    log.info("用户信息:{}",userEntity);
                    studentDTO.setId(Math.toIntExact(userEntity.getId()));
                    studentDTO.setName(userEntity.getUserName());
                    studentDTO.setAvatarUrl(userEntity.getAvatarUrl());
                    studentDTO.setIsLeader(groupTeamMember.getRole() == 1);
                    studentDTO.setMemberIndex(groupTeamMember.getMemberIndex());
                    students.add(studentDTO);
                    num++;
                }
                log.info("小组成员:{}",students);
            }

            groupTeamVO.setStudents(students);
            list.add(groupTeamVO);
        }
        return Result.ok(list);
    }

    /**
     * 学生自由加入（先到先得）
     */
    @Override
    public Result<?> joinFree(JoinStuDTO joinStuDTO) {
        Long teamId = joinStuDTO.getTeamId();
        long studentId = StpUtil.getLoginIdAsLong();
        Long count2 = groupTeamMemberMapper.selectCount(new LambdaQueryWrapper<GroupTeamMember>()
                .eq(GroupTeamMember::getUserId, studentId)
                .eq(GroupTeamMember::getStatus, 1));
        if (count2>0){
            log.error("学生已加入小组");
            return Result.error("学生已加入小组");
        }
        Object lock = lockMap.computeIfAbsent(teamId, k -> new Object());
        synchronized (lock){
            Boolean isLeader = joinStuDTO.getStudent().getIsLeader();
            if (teamId==null){
                log.error("小组ID不能为空");
                return Result.error("小组ID不能为空");
            }
            GroupTeam groupTeam = groupTeamMapper.selectById(teamId);
            if (groupTeam.getLogicalDel()==1){
                log.error("小组已删除");
                return Result.error("小组不存在");
            }
            if (groupTeam.getStatus()==1){
                log.error("小组已锁定");
                return Result.error("小组已锁定");
            }
            Integer currentNum = groupTeam.getCurrentNum();
            if (currentNum>=groupTeam.getCapacity()){
                log.error("小组已满");
                return Result.error("小组已满");
            }
            Integer memberIndex = joinStuDTO.getStudent().getMemberIndex();
            //判断该位置是否有人了
            Long count1 = groupTeamMemberMapper.selectCount(new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getTeamId, teamId)
                    .eq(GroupTeamMember::getMemberIndex, memberIndex)
                    .eq(GroupTeamMember::getStatus, 1));
            if (count1>0){
                log.error("该位置有人了");
                return Result.error("该位置有人了");
            }
            //将小组人数+1
            groupTeam.setCurrentNum(currentNum+1);
            groupTeamMapper.updateById(groupTeam);
            //获取当前用户id
            Integer userId = joinStuDTO.getStudent().getId();
            GroupTeamMember groupTeamMember = new GroupTeamMember();
            groupTeamMember.setTeamId(teamId);
            groupTeamMember.setUserId(Long.valueOf(userId));
            groupTeamMember.setMemberIndex(memberIndex);
            if (isLeader){
                groupTeamMember.setRole(1);
                groupTeam.setLeaderId(Long.valueOf(userId));
                groupTeamMapper.updateById(groupTeam);
            }else {
                groupTeamMember.setRole(2);
            }
//        if (groupTeam.getCurrentNum()==1){
//            //将当前用户匹配为队长
//            groupTeamMember.setRole(1);
//            groupTeam.setLeaderId(userId);
//            groupTeamMapper.updateById(groupTeam);
//        }
            Long count = groupTeamMemberMapper.selectCount(new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getTeamId, teamId)
                    .eq(GroupTeamMember::getUserId, userId));
            if (count==0){
                groupTeamMemberMapper.insert(groupTeamMember);
                return Result.ok();
            }
            groupTeamMember.setStatus(1);
            log.info("修改小组成员信息:{}",groupTeamMember);
            groupTeamMemberMapper.update(groupTeamMember,new LambdaQueryWrapper<GroupTeamMember>()
                    .eq(GroupTeamMember::getTeamId, teamId)
                    .eq(GroupTeamMember::getUserId, userId));
            return Result.ok();
        }
    }

//    /**
//     * 创建小组规则
//     */
//    @Override
//    public Result<?> createGroupingPlan(GroupingPlanDTO groupingPlanDTO) {
//        if (groupingPlanDTO==null) {
//            return Result.error();
//        }
//        GroupingPlan groupingPlan = new GroupingPlan();
//        groupingPlan.setTargetTeamCount(groupingPlanDTO.getTargetTeamCount());
//        groupingPlan.setAllowFreeJoin(groupingPlanDTO.getAllowFreeJoin());
//        groupingPlan.setMinTeamSize(groupingPlanDTO.getMinTeamSize());
//        groupingPlan.setStatus(groupingPlanDTO.getStatus());
//        groupingPlan.setCreatedBy(groupingPlanDTO.getCreatedBy());
//        groupingPlanMapper.insert(groupingPlan);
//        return Result.ok();
//    }

//    /**
//     * 保存小组
//     */
//    @Override
//    @Transactional
//    public Result<?> saveGroup(SaveGroupDTO saveGroupDTO) {
//        if (saveGroupDTO == null) {
//            return Result.error();
//        }
//        String name = saveGroupDTO.getName();
//        List<StudentDTO> members = saveGroupDTO.getMembers();
//        StudentDTO leader = saveGroupDTO.getLeader();
//        Integer subjectId = saveGroupDTO.getSubjectId();
//        if (members == null || members.isEmpty()) {
//            return Result.error("小组成员不能为空");
//        }
//        GroupTeam groupTeam=new GroupTeam();
//        groupTeam.setSubjectId(subjectId);
//        groupTeam.setName(name);
//        groupTeam.setLeaderId(Long.valueOf(leader.getId()));
//        groupTeam.setCurrentNum(members.size());
//        groupTeamMapper.insert(groupTeam);
//        //保存队长到小组成员表
//        GroupTeamMember teamMember = new GroupTeamMember();
//        teamMember.setTeamId(groupTeam.getId());
//        teamMember.setUserId(Long.valueOf(leader.getId()));
//        teamMember.setRole(1);
//        groupTeamMemberMapper.insert(teamMember);
//        //保存小组成员表
//        for (StudentDTO member : members) {
//            GroupTeamMember groupTeamMember = new GroupTeamMember();
//            groupTeamMember.setTeamId(groupTeam.getId());
//            groupTeamMember.setUserId(Long.valueOf(member.getId()));
//            groupTeamMember.setRole(2);
//            groupTeamMemberMapper.insert(groupTeamMember);
//        }
//        return Result.ok();
//    }


}
