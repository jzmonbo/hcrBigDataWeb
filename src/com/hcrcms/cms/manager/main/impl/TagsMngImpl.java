package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.TagsDao;
import com.hcrcms.cms.entity.main.CmsTags;
import com.hcrcms.cms.manager.main.TagsMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class TagsMngImpl implements TagsMng {
	@Transactional(readOnly = true)
	public List<CmsTags> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public CmsTags findById(Integer id) {
		CmsTags entity = dao.findById(id);
		return entity;
	}
	
	public CmsTags save(CmsTags bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public CmsTags update(CmsTags bean) {
		Updater<CmsTags> updater = new Updater<CmsTags>(bean);
		CmsTags entity = dao.updateByUpdater(updater);
		return entity;
	}

	public CmsTags deleteById(Integer id) {
		CmsTags bean = dao.deleteById(id);
		return bean;
	}

	public CmsTags[] deleteByIds(Integer[] ids) {
		CmsTags[] beans = new CmsTags[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<CmsTags> findByName(String name) {
		return dao.findByName(name);
	}

	private TagsDao dao;

	@Autowired
	public void setDao(TagsDao dao) {
		this.dao = dao;
	}
}