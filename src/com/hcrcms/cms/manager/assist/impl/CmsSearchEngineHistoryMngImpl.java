package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsSearchEngineHistoryDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngineHistory;
import com.hcrcms.cms.manager.assist.CmsSearchEngineHistoryMng;

@Service
@Transactional
public class CmsSearchEngineHistoryMngImpl implements CmsSearchEngineHistoryMng {
	
	@Transactional(readOnly = true)
	public List<CmsSearchEngineHistory> getYestoryList(int day) {
		return dao.getYestoryList(day);
	}

	private CmsSearchEngineHistoryDao dao;

	@Autowired
	public void setDao(CmsSearchEngineHistoryDao dao) {
		this.dao = dao;
	}
}