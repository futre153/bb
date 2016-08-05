package com.bb.commons;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class Photo {
	private long id;
	private long articleId;
	private long galleryId;
	private Object data;
	private String description;
	private String mime;
	public Photo(int id) {
		setId(id);
	}
	long getId() {
		return id;
	}
	void setId(long id) {
		this.id = id;
	}
	long getArticleId() {
		return articleId;
	}
	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}
	long getGalleryId() {
		return galleryId;
	}
	public void setGalleryId(long galleryId) {
		this.galleryId = galleryId;
	}
	Object getData() {
		return data;
	}
	public void setData(Object object) {
		this.data = object;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public InputStream getInputStream() throws SQLException {
		if(data instanceof Blob) {
			return ((Blob) data).getBinaryStream();
		}
		else if (data instanceof byte[]) {
			return new ByteArrayInputStream((byte[]) data);
		}
		else {
			return new ByteArrayInputStream((data.toString().getBytes()));
		}
	}
	public void free() throws SQLException {
		if(data instanceof Blob) {
			((Blob) data).free();
		}
	}
}
