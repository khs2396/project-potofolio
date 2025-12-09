package com.example.KG25.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ordering {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, 
    generator = "ORDERING_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "ORDERING_SEQUENCE_GENERATOR", 
    sequenceName = "seq_ordering", initialValue = 1, allocationSize = 1)
	private int seq;
	private String pdname;
	private String id;
	private String addr;
	private int total;
	private Date logdate;
	private String ing;
}
