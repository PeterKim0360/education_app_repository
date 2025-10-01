package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.HomeworkInClassDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassEditDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassSendDTO;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.vo.HomeworkInClassVO;

import java.util.List;

public interface TeacherCoursesService {
    /**
     * 上传文件
     * @param teacherSendFileDTO
     * @return
     */
    Result<?> uploadFile(TeacherSendFileDTO teacherSendFileDTO);

    /**
     * 开始上课
     * @param subjectId
     * @return
     */
    Result<?> startClass(Integer subjectId);

    /**
     * 结束上课
     * @param subjectId
     * @return
     */
    Result<?> endClass(Integer subjectId);

    /**
     * 发布作业
     * @param homeworkInClassSendDTO
     * @return
     */
    Result<?> sendWork(HomeworkInClassSendDTO homeworkInClassSendDTO);

    /**
     * 创建作业
     * @param homeworkInClassDTO
     * @return
     */
    Result<?> createWork(HomeworkInClassDTO homeworkInClassDTO);

    /**
     * 编辑作业
     * @param homeworkInClassEditDTO
     * @return
     */
    Result<?> editWork(HomeworkInClassEditDTO homeworkInClassEditDTO);

    /**
     * 作业列表
     * @param subjectId
     * @return
     */
    Result<List<HomeworkInClassVO>> work(Integer subjectId);


}
