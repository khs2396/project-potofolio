package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Intranet.entity.Employee;
import com.example.Intranet.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer>{
    // Employee 객체가 아닌 emp_no로 직접 조회
    @Query("SELECT t FROM Task t WHERE t.assigneeId.empNo = :empNo ORDER BY t.taskSeq DESC")
    List<Task> findByAssigneeIdOrderByTaskSeqDesc(@Param("empNo") Integer empNo);

    @Query("SELECT t FROM Task t WHERE t.requestId.empNo = :empNo ORDER BY t.taskSeq DESC")
    List<Task> findByRequestIdOrderByTaskSeqDesc(@Param("empNo") Integer empNo);

    List<Task> findAllByOrderByTaskSeqDesc();


}
