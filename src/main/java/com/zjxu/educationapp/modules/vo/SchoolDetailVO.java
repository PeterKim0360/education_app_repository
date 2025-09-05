package com.zjxu.educationapp.modules.vo;

import lombok.Data;

@Data
public class SchoolDetailVO {
    private Long schoolId;
    private String schoolName;
    private String simpleAddress;
    private String detailedAddress;
    private String emblemUrl;
    private String schoolProfile;
    private Boolean is985;
    private Boolean is211;
    private Integer schoolScore;
    private Integer schoolScoreThisYear;
    private Integer schoolScoreLastYear;
    private Integer schoolScoreLastLastYear;
}
