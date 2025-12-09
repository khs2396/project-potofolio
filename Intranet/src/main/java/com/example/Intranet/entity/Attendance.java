package com.example.Intranet.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ATTENDANCE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(generator = "ATT_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ATT_SEQ_GENERATOR", sequenceName = "att_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "ATT_SEQ")
    private Integer attSeq;

    @Column(name = "EMP_NO", nullable = false)
    private Integer empNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "WORKDATE", nullable = false)
    private Date workdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLOCK_IN")
    private Date clockIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CLOCK_OUT")
    private Date clockOut;

    @Column(name = "IN_STATUS")
    private String inStatus;   // 출근/지각

    @Column(name = "OUT_STATUS")
    private String outStatus;  // 퇴근/조퇴

    @Column(name = "MEMO")
    private String memo;
}
