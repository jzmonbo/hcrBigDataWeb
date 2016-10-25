package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.TopLinkDao;
import com.hcrcms.cms.entity.main.TopLink;
import com.hcrcms.cms.manager.main.TopLinkMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class TopLinkMngImpl implements TopLinkMng {
	@Transactional(readOnly = true)
	public List<TopLink> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public TopLink findById(Integer id) {
		TopLink entity = dao.findById(id);
		return entity;
	}
	
	public TopLink save(TopLink bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public TopLink update(TopLink bean) {
		Updater<TopLink> updater = new Updater<TopLink>(bean);
		TopLink entity = dao.updateByUpdater(updater);
		return entity;
	}

	public TopLink deleteById(Integer id) {
		TopLink bean = dao.deleteById(id);
		return bean;
	}

	public TopLink[] deleteByIds(Integer[] ids) {
		TopLink[] beans = new TopLink[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<TopLink> findByName(String name) {
		return dao.findByName(name);
	}

	private TopLinkDao dao;

	@Autowired
	public void setDao(TopLinkDao dao) {
		this.dao = dao;
	}
}