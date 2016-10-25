package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.ArticleBottomLinkDao;
import com.hcrcms.cms.entity.main.ArticleBottomLink;
import com.hcrcms.cms.manager.main.ArticleBottomLinkMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class ArticleBottomLinkMngImpl implements ArticleBottomLinkMng {
	@Transactional(readOnly = true)
	public List<ArticleBottomLink> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public ArticleBottomLink findById(Integer id) {
		ArticleBottomLink entity = dao.findById(id);
		return entity;
	}
	
	public ArticleBottomLink save(ArticleBottomLink bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public ArticleBottomLink update(ArticleBottomLink bean) {
		Updater<ArticleBottomLink> updater = new Updater<ArticleBottomLink>(bean);
		ArticleBottomLink entity = dao.updateByUpdater(updater);
		return entity;
	}

	public ArticleBottomLink deleteById(Integer id) {
		ArticleBottomLink bean = dao.deleteById(id);
		return bean;
	}

	public ArticleBottomLink[] deleteByIds(Integer[] ids) {
		ArticleBottomLink[] beans = new ArticleBottomLink[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<ArticleBottomLink> findByName(String name) {
		return dao.findByName(name);
	}

	private ArticleBottomLinkDao dao;

	@Autowired
	public void setDao(ArticleBottomLinkDao dao) {
		this.dao = dao;
	}
}