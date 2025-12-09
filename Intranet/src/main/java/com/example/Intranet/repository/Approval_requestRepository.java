package com.example.Intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Intranet.entity.Approval_request;
import com.example.Intranet.entity.Employee;

public interface Approval_requestRepository extends JpaRepository<Approval_request, Integer>{
    List<Approval_request> findByRequesterIdOrderByApprovalSeqDesc(Employee requesterId);
    List<Approval_request> findAllByOrderByApprovalSeqDesc();
}
