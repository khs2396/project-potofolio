package com.example.Intranet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.Intranet.dto.AttendanceDTO;
import com.example.Intranet.entity.Attendance;
import com.example.Intranet.entity.Employee;
import com.example.Intranet.service.AttendanceService;
import com.example.Intranet.service.EmployeeService;
import com.example.Intranet.controller.SessionConst;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" }, allowCredentials = "true")
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private EmployeeService employeeService;

    private Employee currentUser(HttpSession session) {
        Integer empNo = (Integer) session.getAttribute(SessionConst.LOGIN_EMP_DEP_NO);
        if (empNo == null) empNo = (Integer) session.getAttribute("emp_no");
        if (empNo == null) return null;
        return employeeService.employeeView(empNo);
    }

    private boolean isHrOrManager(Employee user) {
        if (user == null) return false;
        Integer depId = (user.getDepartment() != null) ? user.getDepartment().getDepId() : null;
        String depName = (user.getDepartment() != null) ? user.getDepartment().getDepName() : null;
        Integer roleId = (user.getRole() != null) ? user.getRole().getRoleId() : null;
        String roleName = (user.getRole() != null) ? user.getRole().getRoleName() : null;

        boolean isHr = (depId != null && depId == 1)
                || (depName != null && depName.equalsIgnoreCase("HR"));
        boolean isManager = (roleId != null && roleId == 5)
                || (roleName != null && (roleName.equalsIgnoreCase("GM") || roleName.toLowerCase().contains("manager")));
        return isHr || isManager;
    }

    @PostMapping("/check-in")
    public Map<String, Object> checkIn(HttpSession session) {
        Employee user = currentUser(session);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
        Attendance saved = attendanceService.checkIn(user.getEmpNo());
        Map<String, Object> map = new HashMap<>();
        map.put("rt", "OK");
        map.put("item", saved);
        return map;
    }

    @PostMapping("/check-out")
    public Map<String, Object> checkOut(HttpSession session) {
        Employee user = currentUser(session);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");
        Attendance saved = attendanceService.checkOut(user.getEmpNo());
        Map<String, Object> map = new HashMap<>();
        map.put("rt", "OK");
        map.put("item", saved);
        return map;
    }

    @GetMapping
    public Map<String, Object> myList(@RequestParam(name = "pg", defaultValue = "1") int pg,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      @RequestParam(name = "empNo", required = false) Integer empNo,
                                      HttpSession session) {
        Employee user = currentUser(session);
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");

        Integer targetEmp = user.getEmpNo();
        if (empNo != null) {
            // 다른 사람 근태는 HR/부서장만 조회 허용
            if (!empNo.equals(user.getEmpNo()) && !isHrOrManager(user)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
            }
            targetEmp = empNo;
        }
        if (targetEmp == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "loginRequired");

        int pageIndex = Math.max(pg - 1, 0);
        Page<Attendance> page = attendanceService.listMy(targetEmp, pageIndex, size);
        return pageToMap(page, pg);
    }

    @PatchMapping("/{attSeq}/memo")
    public Map<String, Object> updateMemo(@PathVariable("attSeq") Integer attSeq,
                                          @RequestBody Map<String, String> body,
                                          HttpSession session) {
        Employee user = currentUser(session);
        if (!isHrOrManager(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
        String memo = body != null ? body.get("memo") : null;
        if (memo == null) memo = "";
        int updated = attendanceService.updateMemo(attSeq, memo);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "attendanceNotFound");
        Map<String, Object> map = new HashMap<>();
        map.put("rt", "OK");
        return map;
    }

    @GetMapping("/all")
    public Map<String, Object> all(@RequestParam(name = "pg", defaultValue = "1") int pg,
                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                   HttpSession session) {
        Employee user = currentUser(session);
        if (!isHrOrManager(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
        int pageIndex = Math.max(pg - 1, 0);
        Page<Attendance> page = attendanceService.listAll(pageIndex, size);
        return pageToMap(page, pg);
    }

    /**
     * 근태 기록 수정 (empNo + workdate 기반)
     * HR/관리자만 허용
     */
    @PostMapping("/modify")
    public Map<String, Object> attendanceModify(@RequestBody AttendanceDTO dto, HttpSession session) {
        Employee user = currentUser(session);
        if (!isHrOrManager(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "noPermission");
        int result = attendanceService.attendanceModify(dto);
        Map<String, Object> map = new HashMap<>();
        map.put("rt", result > 0 ? "modifyOK" : "modifyFAIL");
        return map;
    }

    private Map<String, Object> pageToMap(Page<Attendance> page, int pg) {
        int totalP = page.getTotalPages();
        int startPage = ((pg - 1) / 3) * 3 + 1;
        int endPage = Math.min(startPage + 2, totalP);

        Map<String, Object> map = new HashMap<>();
        map.put("rt", "OK");
        map.put("total", page.getTotalElements());
        map.put("pg", pg);
        map.put("totalP", totalP);
        map.put("startPage", startPage);
        map.put("endPage", endPage);
        map.put("items", page.getContent());
        return map;
    }
}
