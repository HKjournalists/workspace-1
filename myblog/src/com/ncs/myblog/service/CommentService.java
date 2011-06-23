package com.ncs.myblog.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.ncs.myblog.dao.CommentMapper;
import com.ncs.myblog.dao.CommentMapperImpl;
import com.ncs.myblog.domain.Comment;
import com.ncs.myblog.utils.SQLMapperFactory;

public class CommentService {

	private static final Logger LOGGER = Logger.getLogger(CommentService.class);

	public Comment creatComment(Comment comment) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		CommentMapper commentMapper = new CommentMapperImpl(session);
		try {
			commentMapper.save(comment);
			session.commit();
		} catch (Exception e) {
			LOGGER.error(comment.getEmail() + "\t评论创建失败，事务回滚。", e);
			session.rollback();
			return null;
		}
		return comment;
	}

	/**
	 * 根据articleId获得评论
	 * 
	 * @param articleId
	 * @return
	 */
	public List<Comment> getCommentByArticleId(final Long articleId) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		CommentMapper commentMapper = new CommentMapperImpl(session);
		List<Comment> comments = new ArrayList<Comment>();
		try {
			comments =  commentMapper.getByArticleId(articleId);
		} catch (Exception e) {
			System.out.println(e);
		}
		return comments;
	}
	
	public static void main(String[] args) {
		CommentService commentService = new CommentService();
		Long id = new Long(7);
		List<Comment> comments = commentService.getCommentByArticleId(id);
		System.out.println(comments);
	}
}
