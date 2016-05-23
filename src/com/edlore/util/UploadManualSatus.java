package com.edlore.util;

/**
 * @author Sowjanya B
 *
 */
public class UploadManualSatus {

	private String file_Id;
	private String status;
	private String message;

	public String getFile_Id() {
		return file_Id;
	}

	public void setFile_Id(String file_Id) {
		this.file_Id = file_Id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "UploadManualSatus [file_Id=" + file_Id + ", status=" + status
				+ ", message=" + message + "]";
	}

}
