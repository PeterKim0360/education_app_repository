package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
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
     * @return
     */
    Result<IPage> stuList(Long classId);

}
