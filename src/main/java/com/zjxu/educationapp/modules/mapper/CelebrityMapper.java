package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.Celebrity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huawei
* @description 针对表【celebrity(名人表)】的数据库操作Mapper
* @createDate 2025-09-10 17:57:17
* @Entity com.zjxu.educationapp.modules.entity.Celebrity
*/
@Mapper
public interface CelebrityMapper extends BaseMapper<Celebrity> {

}




