package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsSearchEngineDao {
	public List<CmsSearchEngine> getList();

	public CmsSearchEngine findById(Integer id);

	public void save(CmsSearchEngine bean);

	public CmsSearchEngine updateByUpdater(Updater<CmsSearchEngine> updater);
	
	public void updateBatch(List<CmsSearchEngine> searchEngineList);

	public void deleteById(Integer id);
	
	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable,String companyName);
	/**
	 * 获取搜索表中维度最大最小值
	 * @return
	 */
	public CmsSearchEnginMM getMaxMin();
	
	public List<CmsSearchEngine> getListSort();
}