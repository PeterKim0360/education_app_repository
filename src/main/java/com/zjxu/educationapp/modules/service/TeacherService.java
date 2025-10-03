package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.vo.StudentSimpleVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;

import java.util.List;

public interface TeacherService {
    /**
     * 获取老师对应的学科
     * @return
     */
    Result getSubjects();

    /**
     * 获取老师对应学科的班级
     * @return
     */
    Result<List<TeacherClassVO>> getClazz(Integer subjectId);

    /**
     * 创建班级
     *
     * @param className
     * @param subjectId
     * @return
     */
    Result<?> createClass(String className, Integer subjectId);

    /**
     * 查看对应班级的学生
     *
     * @param classId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<StudentSimpleVO>> stuList(Long classId, int page, int size);

    /**
     * 删除学生
     *
     * @param stuIds
     * @return
     */
    Result<?> deleteStus(List<Long> stuIds);

    /**
     * 添加学生
     * @param stuIds
     * @param classId
     * @return
     */
    Result<?> addStus(List<Long> stuIds, Long classId);

    /**
     * 匹配班级
     * @param subjectIds
     * @return
     */
    Result<?> matchClass(List<Integer> subjectIds);
}
