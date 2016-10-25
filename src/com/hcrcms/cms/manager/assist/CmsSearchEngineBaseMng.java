package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEngineBase;

public interface CmsSearchEngineBaseMng {
	public List<CmsSearchEngineBase> getList();

	public List<CmsSearchEngineBase> getListSort();
}