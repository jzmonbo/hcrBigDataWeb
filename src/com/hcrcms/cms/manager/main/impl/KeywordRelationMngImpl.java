package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.KeywordRelationDao;
import com.hcrcms.cms.entity.main.KeywordRelation;
import com.hcrcms.cms.manager.main.KeywordRelationMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class KeywordRelationMngImpl implements KeywordRelationMng {
	@Transactional(readOnly = true)
	public List<KeywordRelation> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public KeywordRelation findById(Integer id) {
		KeywordRelation entity = dao.findById(id);
		return entity;
	}
	
	public KeywordRelation save(KeywordRelation bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public KeywordRelation update(KeywordRelation bean) {
		Updater<KeywordRelation> updater = new Updater<KeywordRelation>(bean);
		KeywordRelation entity = dao.updateByUpdater(updater);
		return entity;
	}

	public KeywordRelation deleteById(Integer id) {
		KeywordRelation bean = dao.deleteById(id);
		return bean;
	}

	public KeywordRelation[] deleteByIds(Integer[] ids) {
		KeywordRelation[] beans = new KeywordRelation[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<KeywordRelation> findByName(String name) {
		return dao.findByName(name);
	}

	private KeywordRelationDao dao;

	@Autowired
	public void setDao(KeywordRelationDao dao) {
		this.dao = dao;
	}
}