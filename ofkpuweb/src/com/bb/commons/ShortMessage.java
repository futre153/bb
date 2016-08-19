package com.bb.commons;

public class ShortMessage {
	private long id;
	private long inserted;
	private String caption;
	private String text;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getInserted() {
		return inserted;
	}
	public void setInserted(long inserted) {
		this.inserted = inserted;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
