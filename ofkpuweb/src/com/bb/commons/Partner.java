package com.bb.commons;

public class Partner {
	private long id;
	private String type;
	private long photo_id;
	private String name;
	private String url;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getPhoto_id() {
		return photo_id;
	}
	public void setPhoto_id(long photo_id) {
		this.photo_id = photo_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
