package com.example.Intranet.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AttendanceDTO {
    private Integer attSeq;
    private Integer empNo;
    private Date workdate;
    private Date clockIn;
    private Date clockOut;
    private String inStatus;
    private String outStatus;
    private String memo;
}
