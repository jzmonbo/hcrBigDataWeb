package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.assist.CmsSearchEngineHistory;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsSearchEngineHistoryDao {
	public List<CmsSearchEngineHistory> getYestoryList(int day);
}