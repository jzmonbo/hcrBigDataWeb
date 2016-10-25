package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.ArticleTopLink;

/**
 * 专家Manager接口
 * 
 */
public interface ArticleTopLinkMng {
	
	public List<ArticleTopLink> getList();

	public ArticleTopLink findById(Integer id);
	
	public List<ArticleTopLink> findByName(String name);
	
	public ArticleTopLink save(ArticleTopLink bean);

	public ArticleTopLink update(ArticleTopLink bean);

	public ArticleTopLink deleteById(Integer id);

	public ArticleTopLink[] deleteByIds(Integer[] ids);
}