package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEngineLog;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsSearchEngineLogDao {
	public List<CmsSearchEngineLog> getList();

	public CmsSearchEngineLog findById(Integer id);

	public void save(CmsSearchEngineLog bean);
	
	public void saveBatch(List<CmsSearchEngineLog> searchEngineLogList);

	public CmsSearchEngineLog updateByUpdater(Updater<CmsSearchEngineLog> updater);
	
	public void updateBatch(List<CmsSearchEngineLog> searchEngineLogList);

	public void deleteById(Integer id);
	
	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable,String companyName,String startTime,String endTime);
	
	public List<CmsSearchEngineLog> getListSort();
	
	public List<CmsSearchEngineLog> getListByStartEndTime(String startTime,String endTime);
}