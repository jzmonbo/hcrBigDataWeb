package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.common.page.Pagination;

public interface CmsSearchEngineMng {
	public List<CmsSearchEngine> getList();

	public CmsSearchEngine findById(Integer id);

	public void save(CmsSearchEngine bean);

	public void update(CmsSearchEngine bean);

	public void updateBatch(List<CmsSearchEngine> searchEngineList);
	
	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public Pagination getPage(int pageNo, int pageSize,String companyName);
	/**
	 * 获取搜索表中维度最大最小值
	 * @return
	 */
	public CmsSearchEnginMM getMaxMin();
	
	public List<CmsSearchEngine> getListSort();
}