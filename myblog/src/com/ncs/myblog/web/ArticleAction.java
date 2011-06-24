package com.ncs.myblog.web;

import java.util.List;

import org.apache.log4j.Logger;

import com.ncs.myblog.domain.Article;
import com.ncs.myblog.domain.Comment;
import com.ncs.myblog.domain.User;
import com.ncs.myblog.service.ArticleService;
import com.ncs.myblog.service.CommentService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author nangong
 * 
 */
public class ArticleAction extends ActionSupport {

	private static final Logger LOGGER = Logger.getLogger(ArticleAction.class);
	private static final long serialVersionUID = -39774832611463810L;

	private User user;
	private Article article;
	private List<Article> articleList;

	private Comment comment;
	private List<Comment> commentList;

	private String message;

	private ArticleService articleService = new ArticleService();
	private CommentService commentService = new CommentService();

	public String renderToCreateArticle() {
		return SUCCESS;
	}

	public String writeArticle() {
		Article newArticle = articleService.creatArticle(article);
		if (newArticle == null)
			return ERROR;
		else {
			LOGGER.info("This article is created successfully!");
			return SUCCESS;
		}
	}

	public String displayArticle() {
		setArticleList(articleService.getAllArticles());
		return SUCCESS;
	}

	public String displayOne() {
		if (comment != null) {
			Comment newComment = commentService.creatComment(comment);
			if (newComment == null) return ERROR;
		}
		Long id = article.getId();
		article = articleService.getArticleById(id);
		if (article == null) {
			System.out.println("文章为空啊~~~");
		}
		//调用commentService.getCommentsByArticleId(id)
		setCommentList(commentService.getCommentByArticleId(id));
		if (!this.commentList.isEmpty()) article.setComments(commentList);
		return SUCCESS;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Article getArticle() {
		return article;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}

	public List<Article> getArticleList() {
		return articleList;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getComment() {
		return comment;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}
}
