package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.ArticleTopLinkDao;
import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.cms.manager.main.ArticleTopLinkMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class ArticleTopLinkMngImpl implements ArticleTopLinkMng {
	@Transactional(readOnly = true)
	public List<ArticleTopLink> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public ArticleTopLink findById(Integer id) {
		ArticleTopLink entity = dao.findById(id);
		return entity;
	}
	
	public ArticleTopLink save(ArticleTopLink bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public ArticleTopLink update(ArticleTopLink bean) {
		Updater<ArticleTopLink> updater = new Updater<ArticleTopLink>(bean);
		ArticleTopLink entity = dao.updateByUpdater(updater);
		return entity;
	}

	public ArticleTopLink deleteById(Integer id) {
		ArticleTopLink bean = dao.deleteById(id);
		return bean;
	}

	public ArticleTopLink[] deleteByIds(Integer[] ids) {
		ArticleTopLink[] beans = new ArticleTopLink[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<ArticleTopLink> findByName(String name) {
		return dao.findByName(name);
	}

	private ArticleTopLinkDao dao;

	@Autowired
	public void setDao(ArticleTopLinkDao dao) {
		this.dao = dao;
	}
}