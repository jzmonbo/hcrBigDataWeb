package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.common.hibernate3.Updater;

public interface ArticleTopLinkDao {
	public List<ArticleTopLink> getList();

	public ArticleTopLink findById(Integer id);
	
	public List<ArticleTopLink> findByName(String name);
	
	public ArticleTopLink findByPath(String path);

	public ArticleTopLink save(ArticleTopLink bean);

	public ArticleTopLink updateByUpdater(Updater<ArticleTopLink> updater);

	public ArticleTopLink deleteById(Integer id);
}