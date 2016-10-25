package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEngineBase;

public interface CmsSearchEngineBaseDao {
	public List<CmsSearchEngineBase> getList();

	public List<CmsSearchEngineBase> getListSort();
}