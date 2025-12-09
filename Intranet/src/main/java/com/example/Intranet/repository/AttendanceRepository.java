package com.example.Intranet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Intranet.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Page<Attendance> findByEmpNoOrderByWorkdateDesc(Integer empNo, Pageable pageable);
    List<Attendance> findByEmpNoAndWorkdate(Integer empNo, Date workdate);
}
