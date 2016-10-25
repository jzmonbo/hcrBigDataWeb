package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEngineHistory;

public interface CmsSearchEngineHistoryMng {
	
	public List<CmsSearchEngineHistory> getYestoryList(int day);
	
}