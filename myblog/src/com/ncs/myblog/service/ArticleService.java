package com.ncs.myblog.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.ncs.myblog.dao.ArticleMapper;
import com.ncs.myblog.dao.ArticleMapperImpl;
import com.ncs.myblog.domain.Article;
import com.ncs.myblog.utils.SQLMapperFactory;

public class ArticleService {

	private static final Logger LOGGER = Logger.getLogger(ArticleService.class);

	public Article creatArticle(Article article) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		ArticleMapper articleMapper = new ArticleMapperImpl(session);
		try {
			articleMapper.save(article);
			session.commit();
		} catch (Exception e) {
			LOGGER.error(article.getTitle() + "\t���´���ʧ�ܣ�����ع���", e);
			session.rollback();
			return null;
		}
		return article;
	}

	/**
	 * ����ID��ö���
	 * 
	 * @param id
	 * @return
	 */
	public Article getArticleById(final Long id) {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		ArticleMapper articleMapper = new ArticleMapperImpl(session);
		return articleMapper.getById(id);
	}
	
	/**
	 * ��ȡȫ������
	 * 
	 * @return
	 */
	public List<Article> getAllArticles() {
		SqlSession session = SQLMapperFactory.getInstance().getSqlMapper().openSession();
		ArticleMapper articleMapper = new ArticleMapperImpl(session);
		return articleMapper.getAll();
	}
	
	public static void main(String[] args) {
		ArticleService articleService = new ArticleService();
		Long id = new Long(7);
		Article article = articleService.getArticleById(id);
		System.out.println(article);
	}
}
