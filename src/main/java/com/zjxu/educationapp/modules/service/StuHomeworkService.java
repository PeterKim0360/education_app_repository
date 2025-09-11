package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.StuHomeWorkVO;

import java.util.List;

/**
* @author huawei
* @description 针对表【stu_homework(学生作业信息表)】的数据库操作Service
* @createDate 2025-09-11 20:16:53
*/
public interface StuHomeworkService extends IService<StuHomework> {
    /**
     * 未完成作业的分页查询
     *
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<StuHomeWorkVO>> queryUnComplete(Integer subjectId, int page, int size);

    /**
     * 删除过期的作业(可批量)
     * @param homeworkIds
     * @return
     */
    Result<?> delOutTime(List<Long> homeworkIds);

    /**
     * 查看该学科已完成但未批改的作业
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<StuHomeWorkVO>> queryCmplUnCor(int subjectId, int page, int size);

    /**
     * 查看该学科已完成并已批改的作业
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<StuHomeWorkVO>> queryCmplCor(int subjectId, int page, int size);
}
