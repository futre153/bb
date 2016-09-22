package com.bb.commons;

public class Article {
	private long index = -1;
	private String content;
	private String caption;
	private Photo[] photos;
	private long modified;
	private long categoryId;
	private String author;
	public static final char PHOTO_IDS_SEPARATOR = ',';
	
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
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public static String getPhotoIds(Photo[] photos, char photoIdsSeparator) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < photos.length; i ++) {
			if(i > 0) {
				sb.append(photoIdsSeparator);
			}
			sb.append(photos[i].getId());
		}
		return sb.toString();
	}
}
