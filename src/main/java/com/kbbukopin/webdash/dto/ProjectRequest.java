package com.kbbukopin.webdash.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProjectRequest {

	@NotBlank
	@Size(min = 3)
	private String title;

	@NotBlank
	@Size(min = 10)
	private String description;

}
