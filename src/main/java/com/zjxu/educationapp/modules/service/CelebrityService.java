package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Celebrity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.CelebrityDetailVO;
import com.zjxu.educationapp.modules.vo.CelebritySimpleVO;
import com.zjxu.educationapp.modules.vo.UserFavoriteCelebrityVO;

/**
* @author huawei
* @description 针对表【celebrity(名人表)】的数据库操作Service
* @createDate 2025-09-10 17:57:17
*/
public interface CelebrityService extends IService<Celebrity> {
    /**
     * 名人分页查询（含根据职位/身份模糊查询）
     * @return
     */
    Result<IPage<CelebritySimpleVO>> queryCelebritySimpleInfo(String profession, Integer page, Integer size);

    /**
     * 根据ID查名人详情
     * @param celebrityId
     * @return
     */
    Result<CelebrityDetailVO> queryCelebrityDetailInfo(Long celebrityId);

    /**
     * 用户关注的名人列表
     * @param page
     * @param size
     * @return
     */
    Result<IPage<UserFavoriteCelebrityVO>> queryFavorites(Integer page, Integer size);

    /**
     * 加入当前用户关注列表
     * @param celebrityId
     * @return
     */
    Result<?> addFavorite(Long celebrityId);

    /**
     * 取消关注
     * @param celebrityId
     * @return
     */
    Result<?> unFollow(Long celebrityId);
}
