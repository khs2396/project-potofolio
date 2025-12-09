package com.example.Intranet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskViewDTO {
	private Integer taskSeq;
    private String subject;
    private String taskContent;
    private String fileName;
    private String createAt;
    private String requestName;
    private String assigneeName;
    private Integer statusSeq;
}
