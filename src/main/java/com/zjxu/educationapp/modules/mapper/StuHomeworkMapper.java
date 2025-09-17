package com.zjxu.educationapp.modules.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author huawei
 * @description 针对表【stu_homework(学生作业信息表)】的数据库操作Mapper
 * @createDate 2025-09-12 13:22:17
 * @Entity com.zjxu.educationapp.modules.entity.StuHomework
 */
@Mapper
public interface StuHomeworkMapper extends BaseMapper<StuHomework> {
    Page<StuHomework> selectUnComplete(@Param("stuHomeworkPage") Page<StuHomework> stuHomeworkPage,
                                       @Param("userId") long userId,
                                       @Param("subjectId") Integer subjectId);

    List<StuHomework> select(Long homeworkId,Long userId);

    int update(Long homeworkId,Long userId);
    @Select("select * from stu_homework where homework_id = #{homeworkId} and user_id = #{userId}")
    StuHomework selectByUser(Long homeworkId, Long userId);

    void updateALL(StuHomework stuHomework);
}
