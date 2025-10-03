package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 小组成员表
 * @TableName group_team_member
 */
@TableName(value ="group_team_member")
@Data
public class GroupTeamMember {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 小组id
     */
    private Long teamId;

    /**
     * 随堂id
     */
    private Long workId;

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 角色：1,队长；2,成员
     */
    private Integer role;

    /**
     * 加入时间
     */
    private Date joinedTime;

    /**
     * 状态：1,有效;0,移除
     */
    private Integer status;

    /**
     * 小组中的位置
     */
    private Integer memberIndex;
}