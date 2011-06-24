/**
 * 
 */
package com.ncs.myblog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.ncs.myblog.domain.Article;

/**
 * @author zhangyong
 * 
 */
public class ArticleMapperImpl implements ArticleMapper {

	final String NAMESPACE = "com.ncs.myblog.dao.ArticleMapper";

	private SqlSession session;

	public ArticleMapperImpl(SqlSession session) {
		this.session = session;
	}

	@Override
	public void save(Article article) {
		session.insert(NAMESPACE + ".insert", article);
	}

	@Override
	public Article getById(Long id) {
		return (Article) session.selectOne(NAMESPACE + ".getById", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Article> getAll() {
		return session.selectList(NAMESPACE + ".selectAll");
	}

}
