package com.example.Intranet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workflow {
	 @Id
	 @Column(name = "status_seq")
	 private int statusseq;
	 
	 @Column(name = "status_name") 
	 private String statusname; 
}
