package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.StudentSubject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huawei
* @description 针对表【student_subject(学生选课表)】的数据库操作Mapper
* @createDate 2025-09-28 14:22:31
* @Entity com.zjxu.educationapp.modules.entity.StudentSubject
*/
@Mapper
public interface StudentSubjectMapper extends BaseMapper<StudentSubject> {

}




