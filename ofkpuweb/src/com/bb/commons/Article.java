package com.bb.commons;

public class Article {
	private long index = -1;
	private String content;
	private String caption;
	private Photo[] photos;
	private long modified;
	
	public Article(long l) {
		setIndex(l);
	}
	public long getIndex() {
		return index;
	}
	void setIndex(long index) {
		this.index = index;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public Photo [] getPhotos() {
		return photos;
	}
	public void setPhotos(Photo[] photos) {
		this.photos = photos;
	}
	public long getModified() {
		return modified;
	}
	public void setModified (long modified) {
		this.modified = modified;
	}
}
