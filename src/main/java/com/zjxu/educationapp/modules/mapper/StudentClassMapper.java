package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.StudentClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author huawei
* @description 针对表【student_class(学生班级表)】的数据库操作Mapper
* @createDate 2025-09-17 16:31:28
* @Entity com.zjxu.educationapp.modules.entity.StudentClass
*/
public interface StudentClassMapper extends BaseMapper<StudentClass> {
    @Select("select student_id from student_class where class_id = #{classId}")
    List<Long> getStudentId(Long classId);
}




