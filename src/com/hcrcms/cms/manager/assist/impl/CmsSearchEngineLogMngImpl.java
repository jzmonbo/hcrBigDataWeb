package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsSearchEngineLogDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.assist.CmsSearchEngineLog;
import com.hcrcms.cms.manager.assist.CmsSearchEngineLogMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsSearchEngineLogMngImpl implements CmsSearchEngineLogMng {
	@Transactional(readOnly = true)
	public List<CmsSearchEngineLog> getList() {
		List<CmsSearchEngineLog> list = dao.getList();
		return list;
	}

	@Transactional(readOnly = true)
	public CmsSearchEngineLog findById(Integer id) {
		CmsSearchEngineLog entity = dao.findById(id);
		return entity;
	}

	public void save(CmsSearchEngineLog bean) {
		dao.save(bean);
	}

	public void update(CmsSearchEngineLog bean) {
		Updater<CmsSearchEngineLog> updater = new Updater<CmsSearchEngineLog>(bean);
		dao.updateByUpdater(updater);
	}

	public void updateBatch(List<CmsSearchEngineLog> searchEngineLogList) {
		dao.updateBatch(searchEngineLogList);
	}

	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	public void deleteByIds(Integer[] ids) {
		for (int i = 0, len = ids.length; i < len; i++) {
			deleteById(ids[i]);
		}
	}

	public void saveBatch(List<CmsSearchEngineLog> seLogs) {
		dao.saveBatch(seLogs);
	}

	@Transactional(readOnly = true)
	public Pagination getPage(int pageNo, int pageSize,String companyName,String startTime,String endTime) {
		Pagination page = dao.getPage(null, false, pageNo, pageSize, false,companyName,startTime,endTime);
		return page;
	}
	
	public List<CmsSearchEngineLog> getListSort() {
		return dao.getListSort();
	}

	public List<CmsSearchEngineLog> getListByStartEndTime(String startTime,
			String endTime) {
		return dao.getListByStartEndTime(startTime, endTime);
	}
	
	private CmsSearchEngineLogDao dao;

	@Autowired
	public void setDao(CmsSearchEngineLogDao dao) {
		this.dao = dao;
	}
}