package com.example.Intranet.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMMENTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Comments_SEQUENCE_GENERATOR")
    @SequenceGenerator(
        name = "Comments_SEQUENCE_GENERATOR",
        sequenceName = "COMMENTS_SEQ",
        initialValue = 1,
        allocationSize = 1
    )
    @Column(name = "COMMENT_SEQ")
    private int commentSeq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOARD_SEQ", nullable = false)
    @JsonIgnore
    private Iboard board;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMP_NO", updatable = false)
    @JsonIgnore
    private Employee employee;

    @Column(name = "CONTENT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOGTIME")
    private Date logtime;
}
