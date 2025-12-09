package com.example.Intranet.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "IBOARD")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Iboard {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
	generator = "Iboard_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "Iboard_SEQUENCE_GENERATOR",
	sequenceName = "iboard_seq", 
	initialValue = 1,
	allocationSize = 1)
    @Column(name = "BOARD_SEQ")
    private int boardseq;

    @Column(name = "EMP_NO")
    private int depno;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "HIT")
    private int hit;

    @Temporal(TemporalType.DATE)
    @Column(name = "LOGTIME")
    private Date logtime;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();
}
