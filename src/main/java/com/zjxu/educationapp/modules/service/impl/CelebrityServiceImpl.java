package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Celebrity;
import com.zjxu.educationapp.modules.entity.UserFavoriteCelebrity;
import com.zjxu.educationapp.modules.mapper.UserFavoriteCelebrityMapper;
import com.zjxu.educationapp.modules.service.CelebrityService;
import com.zjxu.educationapp.modules.mapper.CelebrityMapper;
import com.zjxu.educationapp.modules.vo.CelebrityDetailVO;
import com.zjxu.educationapp.modules.vo.CelebritySimpleVO;
import com.zjxu.educationapp.modules.vo.UserFavoriteCelebrityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author huawei
* @description 针对表【celebrity(名人表)】的数据库操作Service实现
* @createDate 2025-09-10 17:57:17
*/
@Slf4j
@Service
public class CelebrityServiceImpl extends ServiceImpl<CelebrityMapper, Celebrity>
    implements CelebrityService{
    @Autowired
    private CelebrityMapper celebrityMapper;
    @Autowired
    private UserFavoriteCelebrityMapper userFavoriteCelebrityMapper;
    /**
     * 名人分页查询（含根据职位/身份模糊查询）
     * @return
     */
    @Override
    public Result<IPage<CelebritySimpleVO>> queryCelebritySimpleInfo(String profession, Integer page, Integer size) {
        QueryWrapper<Celebrity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        queryWrapper.orderByDesc("update_time");
        if (profession!=null&&!profession.isEmpty()){
            queryWrapper.like("profession",profession);
        }
        Page<Celebrity> celebrityPage = celebrityMapper.selectPage(new Page<Celebrity>(page, size), queryWrapper);
        IPage<CelebritySimpleVO> simpleVOIPage = celebrityPage.convert(celebrity -> {
            CelebritySimpleVO simpleVO = new CelebritySimpleVO();
            BeanUtils.copyProperties(celebrity, simpleVO);
            return simpleVO;
        });
        return Result.ok(simpleVOIPage);
    }

    /**
     * 根据ID查名人详情
     * @param celebrityId
     * @return
     */
    @Override
    public Result<CelebrityDetailVO> queryCelebrityDetailInfo(Long celebrityId) {
        Celebrity celebrity = celebrityMapper.selectOne(new QueryWrapper<Celebrity>().eq("celebrity_id", celebrityId));
        CelebrityDetailVO celebrityDetailVO = new CelebrityDetailVO();
        BeanUtils.copyProperties(celebrity,celebrityDetailVO);
        return Result.ok(celebrityDetailVO);
    }

    /**
     * 用户关注的名人列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<UserFavoriteCelebrityVO>> queryFavorites(Integer page, Integer size) {
        //获取当前用户的userId
        long userId = StpUtil.getLoginIdAsLong();
        //获取该用户对应的所有关注名人的ID
        List<UserFavoriteCelebrity> userFavoriteCelebrities = userFavoriteCelebrityMapper
                .selectList(new QueryWrapper<UserFavoriteCelebrity>()
                        .eq("user_id", userId));
        List<Long> celebrityIds=new ArrayList<>();
        for (UserFavoriteCelebrity userFavoriteCelebrity : userFavoriteCelebrities) {
            celebrityIds.add(userFavoriteCelebrity.getCelebrityId());
        }
        if (celebrityIds.isEmpty()||celebrityIds.size()==0){
            log.info("{}",celebrityIds);
            return Result.ok();
        }
        Page<Celebrity> celebrityPage = celebrityMapper.selectPage(new Page<Celebrity>(page, size),
                new QueryWrapper<Celebrity>()
                        .in("celebrity_id", celebrityIds)
                        .orderByDesc("create_time"));
        IPage<UserFavoriteCelebrityVO> userFavoriteCelebrityVOIPage = celebrityPage.convert(celebrity -> {
            UserFavoriteCelebrityVO userFavoriteCelebrityVO = new UserFavoriteCelebrityVO();
            BeanUtils.copyProperties(celebrity, userFavoriteCelebrityVO);
            return userFavoriteCelebrityVO;
        });
        return Result.ok(userFavoriteCelebrityVOIPage);
    }

    /**
     * 加入当前用户关注列表
     * @param celebrityId
     * @return
     */
    @Override
    public Result<?> addFavorite(Long celebrityId) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();

        UserFavoriteCelebrity selectOne = userFavoriteCelebrityMapper.selectOne(new QueryWrapper<UserFavoriteCelebrity>()
                .eq("user_id", userId)
                .eq("celebrity_id", celebrityId));
        //如果celebrityId和userId已经有对应，则说明已关注
        if (selectOne!=null){
            log.info("您已关注过了");
            return Result.error(ErrorCode.FOLLOWED);
        }
        //封装为UserFavoriteCelebrity
        UserFavoriteCelebrity userFavoriteCelebrity = UserFavoriteCelebrity.builder()
                .celebrityId(celebrityId)
                .userId(userId)
                .createTime(new Date())
                .build();
        //保存到持久层
        userFavoriteCelebrityMapper.insert(userFavoriteCelebrity);
        return Result.ok();
    }

    /**
     * 取消关注
     * @param celebrityId
     * @return
     */
    @Override
    public Result<?> unFollow(Long celebrityId) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        userFavoriteCelebrityMapper.delete(new QueryWrapper<UserFavoriteCelebrity>()
                .eq("user_id",userId)
                .eq("celebrity_id",celebrityId));
        return Result.ok();
    }


}




