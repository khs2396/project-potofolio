package com.example.Intranet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Intranet.dao.TaskHistoryDAO;
import com.example.Intranet.dto.TaskHistoryDTO;
import com.example.Intranet.entity.Task_history;

@Service
public class TaskHistoryService {
	@Autowired
	TaskHistoryDAO dao;
	
	public Task_history historyWrite(TaskHistoryDTO dto) {
		return dao.historyWrite(dto);
	}
}
