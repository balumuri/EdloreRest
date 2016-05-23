package com.edlore.util;

public class Asset {
	private String id;
	private String upload_file_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpload_file_name() {
		return upload_file_name;
	}

	public void setUpload_file_name(String upload_file_name) {
		this.upload_file_name = upload_file_name;
	}

	@Override
	public String toString() {
		return " [id=" + id + ", upload_file_name=" + upload_file_name
				+ "]";
	}

}
