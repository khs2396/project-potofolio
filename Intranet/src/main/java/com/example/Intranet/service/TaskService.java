package com.example.Intranet.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Intranet.dao.TaskDAO;
import com.example.Intranet.dto.TaskDTO;
import com.example.Intranet.entity.Task;

@Service
public class TaskService {

	@Autowired
	private TaskDAO dao;

	@Value("${project.upload.path}")
	private String uploadpath;

	@Transactional
	public Task createTask(TaskDTO dto, MultipartFile uploadFile) throws Exception {
		if (uploadFile != null && !uploadFile.isEmpty()) {
			String fileName = uploadFile.getOriginalFilename();
			dto.setFile_name(fileName);

			// ensure upload directory exists
			File folder = new File(uploadpath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File file = new File(uploadpath, fileName);
			uploadFile.transferTo(file);
		} else {
			// 파일이 없을 때 빈 문자열로 설정
			dto.setFile_name("");
		}

		dto.setCreate_at(new Date());
		return dao.taskWrite(dto);
	}

	public Task taskView(Integer taskSeq) {
		return dao.taskView(taskSeq);
	}

	public List<Task> listByAssignee(Integer assigneeId) {
		return dao.listByAssignee(assigneeId);
	}

	public List<Task> listByRequester(Integer requesterId) {
		return dao.listByRequester(requesterId);
	}

	public List<Task> tasksAll() {
		return dao.listAll();
	}
}
