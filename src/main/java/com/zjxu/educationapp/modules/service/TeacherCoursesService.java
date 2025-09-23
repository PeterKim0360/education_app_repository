package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;

import java.util.List;

public interface TeacherCoursesService {
    /**
     * 上传文件
     * @param teacherSendFileDTO
     * @return
     */
    Result<?> uploadFile(TeacherSendFileDTO teacherSendFileDTO);

    /**
     * 获取课程对应的班级
     * @param subjectId
     * @return
     */
    Result<List<TeacherClassVO>> getClazz(Integer subjectId);
}
