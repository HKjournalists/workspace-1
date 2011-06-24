package com.ncs.myblog.domain;

import java.util.Date;
import java.util.List;

public class Article {

	private Long id;
	
	private String title;
	
	private String content;
	
	private Date createTime;
	
	private String author;
	
	private String subject;
		
	private List<Comment> comments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", content="
				+ content + ", createTime=" + createTime + ", author=" + author
				+ "]";
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Comment> getComments() {
		return comments;
	}
	
}
