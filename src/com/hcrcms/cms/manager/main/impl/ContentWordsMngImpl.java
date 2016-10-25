package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.ContentWordsDao;
import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.cms.manager.main.ContentWordsMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class ContentWordsMngImpl implements ContentWordsMng {
	@Transactional(readOnly = true)
	public List<CmsKeyword> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public CmsKeyword findById(Integer id) {
		CmsKeyword entity = dao.findById(id);
		return entity;
	}
	
	public CmsKeyword save(CmsKeyword bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public CmsKeyword update(CmsKeyword bean) {
		Updater<CmsKeyword> updater = new Updater<CmsKeyword>(bean);
		CmsKeyword entity = dao.updateByUpdater(updater);
		return entity;
	}

	public void updateBatch(List<CmsKeyword> lists) {
		dao.updateBatch(lists);
	}

	public CmsKeyword deleteById(Integer id) {
		CmsKeyword bean = dao.deleteById(id);
		return bean;
	}

	public CmsKeyword[] deleteByIds(Integer[] ids) {
		CmsKeyword[] beans = new CmsKeyword[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<CmsKeyword> findByName(String name) {
		return dao.findByName(name);
	}

	private ContentWordsDao dao;

	@Autowired
	public void setDao(ContentWordsDao dao) {
		this.dao = dao;
	}
}