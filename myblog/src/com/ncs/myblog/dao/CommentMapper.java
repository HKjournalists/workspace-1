package com.ncs.myblog.dao;

import java.util.List;

import com.ncs.myblog.domain.Comment;

public interface CommentMapper {

	void save(Comment comment);

	Comment getById(Long id);

	List<Comment> getByArticleId(Long articleId);
}
