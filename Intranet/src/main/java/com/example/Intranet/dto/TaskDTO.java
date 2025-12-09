package com.example.Intranet.dto;

import java.util.Date;

import com.example.Intranet.entity.Task;

import lombok.Data;

@Data
public class TaskDTO {
  private Integer taskId;
  private String request_id;     // String으로 받아서 Integer/이메일 모두 처리
  private String assignee_id;    // String으로 받아서 Integer/이메일 모두 처리
  private String subject;
  private String taskContent;
  private String fileName;
  private Date createAt;
  private Integer statusId;

  // Integer로 파싱 시도
  public Integer getRequest_id() {
    if (request_id == null || request_id.isEmpty()) return null;
    try {
      return Integer.parseInt(request_id);
    } catch (NumberFormatException e) {
      return null;  // 이메일인 경우
    }
  }

  public Integer getAssignee_id() {
    if (assignee_id == null || assignee_id.isEmpty()) return null;
    try {
      return Integer.parseInt(assignee_id);
    } catch (NumberFormatException e) {
      return null;  // 이메일인 경우
    }
  }

  // 이메일/문자열로 가져오기
  public String getRequestIdStr() {
    return request_id;
  }

  public String getAssigneeIdStr() {
    return assignee_id;
  }
  public String getTask_content() { return taskContent; }
  public void setTask_content(String taskContent) { this.taskContent = taskContent; }
  public void setFile_name(String fileName) { this.fileName = fileName; }
  public void setCreate_at(Date d) { this.createAt = d; }
  public Date getCreate_at() { return createAt; }

  // 엔티티 변환
  public Task toEntity(com.example.Intranet.entity.Employee requester,
                      com.example.Intranet.entity.Employee assignee,
                      com.example.Intranet.entity.Workflow status) {
      Task t = new Task(requester, assignee, subject, taskContent, fileName, createAt, status);
      return t;
  }
}
