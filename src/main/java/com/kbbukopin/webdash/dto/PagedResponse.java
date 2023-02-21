package com.kbbukopin.webdash.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PagedResponse<T> {
	private List<T> data;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean last;

	public PagedResponse() {

	}

	public PagedResponse(List<T> data, int page, int size, long totalElements, int totalPages, boolean last) {
		setData(data);
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.last = last;
	}

	public List<T> getData() {
		return data == null ? null : new ArrayList<>(data);
	}

	public final void setData(List<T> data) {
		if (data == null) {
			this.data = null;
		} else {
			this.data = Collections.unmodifiableList(data);
		}
	}

	public boolean isLast() {
		return last;
	}
}
