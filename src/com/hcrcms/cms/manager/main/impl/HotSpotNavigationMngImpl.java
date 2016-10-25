package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.HotSpotNavigationDao;
import com.hcrcms.cms.entity.main.HotSpotNavigation;
import com.hcrcms.cms.manager.main.HotSpotNavigationMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class HotSpotNavigationMngImpl implements HotSpotNavigationMng {
	@Transactional(readOnly = true)
	public List<HotSpotNavigation> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public HotSpotNavigation findById(Integer id) {
		HotSpotNavigation entity = dao.findById(id);
		return entity;
	}
	
	public HotSpotNavigation save(HotSpotNavigation bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public HotSpotNavigation update(HotSpotNavigation bean) {
		Updater<HotSpotNavigation> updater = new Updater<HotSpotNavigation>(bean);
		HotSpotNavigation entity = dao.updateByUpdater(updater);
		return entity;
	}

	public HotSpotNavigation deleteById(Integer id) {
		HotSpotNavigation bean = dao.deleteById(id);
		return bean;
	}

	public HotSpotNavigation[] deleteByIds(Integer[] ids) {
		HotSpotNavigation[] beans = new HotSpotNavigation[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<HotSpotNavigation> findByName(String name) {
		return dao.findByName(name);
	}

	private HotSpotNavigationDao dao;

	@Autowired
	public void setDao(HotSpotNavigationDao dao) {
		this.dao = dao;
	}
}