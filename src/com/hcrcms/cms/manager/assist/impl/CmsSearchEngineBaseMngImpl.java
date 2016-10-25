package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsSearchEngineBaseDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngineBase;
import com.hcrcms.cms.manager.assist.CmsSearchEngineBaseMng;

@Service
@Transactional
public class CmsSearchEngineBaseMngImpl implements CmsSearchEngineBaseMng {
	@Transactional(readOnly = true)
	public List<CmsSearchEngineBase> getList() {
		List<CmsSearchEngineBase> list = dao.getList();
		return list;
	}

	public List<CmsSearchEngineBase> getListSort() {
		return dao.getListSort();
	}

	private CmsSearchEngineBaseDao dao;

	@Autowired
	public void setDao(CmsSearchEngineBaseDao dao) {
		this.dao = dao;
	}
}