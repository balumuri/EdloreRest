package com.edlore.box.util;

/**
 * @author Sowjanya B
 *
 */
public class Track {
	
	private String title;
	private String singer;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Track [title=" + title + ", singer=" + singer + "]";
	}

}