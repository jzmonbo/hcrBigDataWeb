package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.BoutiqueColumnDao;
import com.hcrcms.cms.entity.main.JingPinSportSet;
import com.hcrcms.cms.manager.main.BoutiqueColumnMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class BoutiqueColumnMngImpl implements BoutiqueColumnMng {
	@Transactional(readOnly = true)
	public List<JingPinSportSet> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public JingPinSportSet findById(Integer id) {
		JingPinSportSet entity = dao.findById(id);
		return entity;
	}
	
	public JingPinSportSet save(JingPinSportSet bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public JingPinSportSet update(JingPinSportSet bean) {
		Updater<JingPinSportSet> updater = new Updater<JingPinSportSet>(bean);
		JingPinSportSet entity = dao.updateByUpdater(updater);
		return entity;
	}

	public JingPinSportSet deleteById(Integer id) {
		JingPinSportSet bean = dao.deleteById(id);
		return bean;
	}

	public JingPinSportSet[] deleteByIds(Integer[] ids) {
		JingPinSportSet[] beans = new JingPinSportSet[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public JingPinSportSet findByPath(String path) {
		return dao.findByPath(path);
	}

	public JingPinSportSet updateByUpdater(Updater<JingPinSportSet> updater) {
		return dao.updateByUpdater(updater);
	}

	private BoutiqueColumnDao dao;

	@Autowired
	public void setDao(BoutiqueColumnDao dao) {
		this.dao = dao;
	}
}