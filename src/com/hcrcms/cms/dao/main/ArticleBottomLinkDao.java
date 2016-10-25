package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.ArticleBottomLink;
import com.hcrcms.common.hibernate3.Updater;

public interface ArticleBottomLinkDao {
	public List<ArticleBottomLink> getList();

	public ArticleBottomLink findById(Integer id);
	
	public List<ArticleBottomLink> findByName(String name);
	
	public ArticleBottomLink findByPath(String path);

	public ArticleBottomLink save(ArticleBottomLink bean);

	public ArticleBottomLink updateByUpdater(Updater<ArticleBottomLink> updater);

	public ArticleBottomLink deleteById(Integer id);
}