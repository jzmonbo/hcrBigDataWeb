package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.SiteRightLinkDao;
import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.cms.manager.main.SiteRightLinkMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class SiteRightLinkMngImpl implements SiteRightLinkMng {
	@Transactional(readOnly = true)
	public List<SiteRightLink> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public SiteRightLink findById(Integer id) {
		SiteRightLink entity = dao.findById(id);
		return entity;
	}
	
	public SiteRightLink save(SiteRightLink bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public SiteRightLink update(SiteRightLink bean) {
		Updater<SiteRightLink> updater = new Updater<SiteRightLink>(bean);
		SiteRightLink entity = dao.updateByUpdater(updater);
		return entity;
	}

	public SiteRightLink deleteById(Integer id) {
		SiteRightLink bean = dao.deleteById(id);
		return bean;
	}

	public SiteRightLink[] deleteByIds(Integer[] ids) {
		SiteRightLink[] beans = new SiteRightLink[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<SiteRightLink> findBySort(Integer start,Integer end) {
		return dao.findBySort(start,end);
	}

	public List<SiteRightLink> findByName(String name) {
		return dao.findByName(name);
	}

	private SiteRightLinkDao dao;

	@Autowired
	public void setDao(SiteRightLinkDao dao) {
		this.dao = dao;
	}
}