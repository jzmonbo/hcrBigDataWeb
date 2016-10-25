package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEngineLog;
import com.hcrcms.common.page.Pagination;

public interface CmsSearchEngineLogMng {
	public List<CmsSearchEngineLog> getList();

	public CmsSearchEngineLog findById(Integer id);

	public void save(CmsSearchEngineLog bean);

	public void saveBatch(List<CmsSearchEngineLog> seLogs);
	
	public void update(CmsSearchEngineLog bean);

	public void updateBatch(List<CmsSearchEngineLog> searchEngineList);
	
	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public Pagination getPage(int pageNo, int pageSize,String companyName,String startTime,String endTime);
	
	public List<CmsSearchEngineLog> getListSort();
	
	public List<CmsSearchEngineLog> getListByStartEndTime(String startTime,String endTime);
}