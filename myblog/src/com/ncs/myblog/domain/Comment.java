package com.ncs.myblog.domain;

import java.util.Date;

public class Comment {
	
	private Long id;
	
	private String username;
	
	private String email;
	
	private String content;
	
	private Date replyTime;
	
	private Long articleId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public Long getArticleId() {
		return articleId;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", username=" + username + ", email=" + email + ", content=" + content + ", replyTime=" + replyTime + ", articleId=" + articleId + "]";
	}

}
