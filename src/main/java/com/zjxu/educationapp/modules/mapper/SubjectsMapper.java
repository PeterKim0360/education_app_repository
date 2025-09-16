package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.Subjects;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author huawei
* @description 针对表【subjects】的数据库操作Mapper
* @createDate 2025-09-08 00:59:25
* @Entity com.zjxu.educationapp.modules.entity.Subjects
*/
@Mapper
public interface SubjectsMapper extends BaseMapper<Subjects> {
    @Select("select class_id from subject_class where subject_id=#{subjectId}")
    List<Long> selectClass(Integer subjectId);
}




