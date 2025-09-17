package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.TeacherSubject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huawei
* @description 针对表【teacher_subject(教师 - 学科关联表)】的数据库操作Mapper
* @createDate 2025-09-17 16:37:22
* @Entity com.zjxu.educationapp.modules.entity.TeacherSubject
*/
@Mapper
public interface TeacherSubjectMapper extends BaseMapper<TeacherSubject> {

}




