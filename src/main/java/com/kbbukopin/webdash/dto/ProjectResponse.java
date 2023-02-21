package com.kbbukopin.webdash.dto;

import lombok.Data;

@Data
public class ProjectResponse {
	private Long id;
	private String title;
	private String description;

	public ProjectResponse(Long id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
	}

}