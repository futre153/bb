package com.bb.commons;

import java.sql.Timestamp;

public class TempArticle extends Article {
	
	private long articleId = 1;
	private long created;
	private boolean locked;
	
	public TempArticle(long l) {
		super(l);
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(char l) {
		this.locked = (l == '1' || l == 'Y' || l == 'y');
	}
	public Timestamp getPublished() {
		return articleId > 0 ? new Timestamp (getCreated()) : null;
	}
}
