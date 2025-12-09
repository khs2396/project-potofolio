package com.example.Intranet.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Intranet.dto.AttendanceDTO;
import com.example.Intranet.entity.Attendance;
import com.example.Intranet.repository.AttendanceRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository repo;

    private Date todayDate() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Transactional
    public Attendance checkIn(Integer empNo) {
        Date workdate = todayDate();
        Attendance attendance = repo.findByEmpNoAndWorkdate(empNo, workdate).stream().findFirst().orElse(null);
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmpNo(empNo);
            attendance.setWorkdate(workdate);
        }
        LocalDateTime current = now();
        attendance.setClockIn(toDate(current));
        LocalTime nine = LocalTime.of(9, 0);
        boolean late = current.toLocalTime().isAfter(nine);
        attendance.setInStatus(late ? "지각" : "출근");
        return repo.save(attendance);
    }

    @Transactional
    public Attendance checkOut(Integer empNo) {
        Date workdate = todayDate();
        Attendance attendance = repo.findByEmpNoAndWorkdate(empNo, workdate).stream().findFirst().orElse(null);
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmpNo(empNo);
            attendance.setWorkdate(workdate);
        }
        LocalDateTime current = now();
        attendance.setClockOut(toDate(current));
        LocalTime six = LocalTime.of(18, 0);
        boolean early = current.toLocalTime().isBefore(six);
        attendance.setOutStatus(early ? "조퇴" : "퇴근");
        return repo.save(attendance);
    }

    public Page<Attendance> listMy(Integer empNo, int pg, int size) {
        return repo.findByEmpNoOrderByWorkdateDesc(empNo, PageRequest.of(pg, size));
    }

    public Page<Attendance> listAll(int pg, int size) {
        return repo.findAll(PageRequest.of(pg, size));
    }

    @Transactional
    public int updateMemo(Integer attSeq, String memo) {
        Attendance att = repo.findById(attSeq).orElse(null);
        if (att == null) return 0;
        att.setMemo(memo);
        repo.save(att);
        return 1;
    }

    /**
     * 근태 기록 수정: empNo + workdate로 기존 레코드를 찾아 clockIn/clockOut/상태/메모 갱신
     * 없으면 새로 생성 후 값 세팅 (HR/관리자용 편의 upsert)
     */
    @Transactional
    public int attendanceModify(AttendanceDTO dto) {
        if (dto == null || dto.getEmpNo() == null || dto.getWorkdate() == null) return 0;
        Attendance att = repo.findByEmpNoAndWorkdate(dto.getEmpNo(), dto.getWorkdate())
                .stream()
                .findFirst()
                .orElse(null);
        if (att == null) return 0;

        if (dto.getClockIn() != null) att.setClockIn(dto.getClockIn());
        if (dto.getClockOut() != null) att.setClockOut(dto.getClockOut());
        if (dto.getInStatus() != null) att.setInStatus(dto.getInStatus());
        if (dto.getOutStatus() != null) att.setOutStatus(dto.getOutStatus());
        if (dto.getMemo() != null) att.setMemo(dto.getMemo());

        repo.save(att);
        return 1;
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
