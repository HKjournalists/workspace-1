package com.ncs.myblog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.ncs.myblog.domain.Comment;

public class CommentMapperImpl implements CommentMapper {

	final String NAMESPACE = "com.ncs.myblog.dao.CommentMapper";

	private SqlSession session;

	public CommentMapperImpl(SqlSession session) {
		this.session = session;
	}

	@Override
	public void save(Comment article) {
		session.insert(NAMESPACE + ".insert", article);
	}

	@Override
	public Comment getById(Long id) {
		return (Comment) session.selectOne(NAMESPACE + ".getById", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getByArticleId(Long id) {
		return session.selectList(NAMESPACE + ".selectAll", id);
	}
}
