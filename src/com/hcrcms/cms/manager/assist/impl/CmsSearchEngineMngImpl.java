package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsSearchEngineDao;
import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.manager.assist.CmsSearchEngineMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsSearchEngineMngImpl implements CmsSearchEngineMng {
	@Transactional(readOnly = true)
	public List<CmsSearchEngine> getList() {
		List<CmsSearchEngine> list = dao.getList();
		return list;
	}

	@Transactional(readOnly = true)
	public CmsSearchEngine findById(Integer id) {
		CmsSearchEngine entity = dao.findById(id);
		return entity;
	}

	public void save(CmsSearchEngine bean) {
		dao.save(bean);
	}

	public void update(CmsSearchEngine bean) {
		Updater<CmsSearchEngine> updater = new Updater<CmsSearchEngine>(bean);
		dao.updateByUpdater(updater);
	}

	public void updateBatch(List<CmsSearchEngine> searchEngineList) {
		dao.updateBatch(searchEngineList);
	}

	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	public void deleteByIds(Integer[] ids) {
		for (int i = 0, len = ids.length; i < len; i++) {
			deleteById(ids[i]);
		}
	}

	public CmsSearchEnginMM getMaxMin() {
		return dao.getMaxMin();
	}

	@Transactional(readOnly = true)
	public Pagination getPage(int pageNo, int pageSize,String companyName) {
		Pagination page = dao.getPage(null, false, pageNo, pageSize, false,companyName);
		return page;
	}
	
	public List<CmsSearchEngine> getListSort() {
		return dao.getListSort();
	}

	private CmsSearchEngineDao dao;

	@Autowired
	public void setDao(CmsSearchEngineDao dao) {
		this.dao = dao;
	}
}