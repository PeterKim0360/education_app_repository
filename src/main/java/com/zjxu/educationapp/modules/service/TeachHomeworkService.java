package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeachCreateHomeworkDTO;
import com.zjxu.educationapp.modules.entity.TeachHomework;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.TeachCreateHWDetailVO;
import com.zjxu.educationapp.modules.vo.TeachCreateHWSimpleVO;

import java.util.List;

/**
* @author huawei
* @description 针对表【teach_homework(教师作业信息表)】的数据库操作Service
* @createDate 2025-09-12 00:53:02
*/
public interface TeachHomeworkService extends IService<TeachHomework> {
    /**
     * 创建作业
     * @param teachCreateHomeworkDTO
     * @return
     */
    Result<?> createHomework(TeachCreateHomeworkDTO teachCreateHomeworkDTO);

    /**
     * 查看已创建的作业
     * @return
     */
    Result<IPage<TeachCreateHWSimpleVO>> queryCreateList(int page, int size);

    /**
     * 查看已创建作业详情
     *
     * @return
     */
    Result<TeachCreateHWDetailVO> findCreateHW(Long homeworkId);

    /**
     * 编辑作业
     * @param teachCreateHomeworkDTO
     * @return
     */
    Result<?> editCreateHW(TeachCreateHomeworkDTO teachCreateHomeworkDTO);

    /**
     * 删除已创建的作业 (含批量)
     * @param homeworkIds
     * @return
     */
    Result<?> delCreateHW(List<Long> homeworkIds);

    /**
     * 发布作业
     * @return
     */
    Result<?> sendHW(Long homeworkId);

    /**
     * 删除已发布的作业
     * @param homeworkIds
     * @return
     */
    Result<?> delSendHW(List<Long> homeworkIds);
}
