package com.zjxu.educationapp.modules.vo;

import com.zjxu.educationapp.modules.dto.StudentDTO;
import lombok.Data;

import java.util.List;

@Data
public class GroupTeamVO {
    private Long id;
    private String name;
    private int capacity;
    private int currentCount;

    private List<StudentDTO> students;
}
