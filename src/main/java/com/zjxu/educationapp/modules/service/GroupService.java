package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.*;
import com.zjxu.educationapp.modules.vo.GroupTeamVO;

import java.util.List;

public interface GroupService {

//    /**
//     * 创建小组规则
//     */
//    Result<?> createGroupingPlan(GroupingPlanDTO groupingPlanDTO);

    /**
     * 生成小组
     */
    Result<?> generateGroups(GenerateGroupDTO generateGroupDTO);

    /**
     * 获取小组列表
     */
    Result<List<GroupTeamVO>> getMemberList(Integer subjectId,Long createdBy);


//    Result<List<GroupTeamVO>> getGroupList(GroupTeamDTO groupTeamDTO);

    /**
     * 学生自由加入（先到先得）
     */
    Result<?> joinFree(JoinStuDTO joinStuDTO);

    /**
     * 学生自由分配
     */
    Result<?> freeAssign(GroupNoDTO groupNoDTO);

//    /**
//     * 保存小组
//     */
//    Result<?> saveGroup(SaveGroupDTO saveGroupDTO);

    /**
     * 学生退出
     */
    Result<?> quit(StudentQuitDTO studentQuitDTO);

    /**
     * 结束分组
     */
    Result<?> end(Integer subjectId,Long createdBy);

}
