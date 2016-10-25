package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.ArticleBottomLink;

/**
 * 专家Manager接口
 * 
 */
public interface ArticleBottomLinkMng {
	
	public List<ArticleBottomLink> getList();

	public ArticleBottomLink findById(Integer id);
	
	public List<ArticleBottomLink> findByName(String name);
	
	public ArticleBottomLink save(ArticleBottomLink bean);

	public ArticleBottomLink update(ArticleBottomLink bean);

	public ArticleBottomLink deleteById(Integer id);

	public ArticleBottomLink[] deleteByIds(Integer[] ids);
}