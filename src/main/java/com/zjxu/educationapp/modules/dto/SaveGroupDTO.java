package com.zjxu.educationapp.modules.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zjxu.educationapp.modules.entity.Group;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SaveGroupDTO {

    private Integer subjectId;
    private String name;
    private StudentDTO leader;  // 组长信息
    private List<StudentDTO> members;  // 成员列表

}
