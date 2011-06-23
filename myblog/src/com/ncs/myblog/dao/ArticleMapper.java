/**
 * 
 */
package com.ncs.myblog.dao;

import java.util.List;

import com.ncs.myblog.domain.Article;

/**
 * @author nangong
 */
public interface ArticleMapper {

	void save(Article article);

	Article getById(Long id);

	List<Article> getAll();
}
