package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsTopicReport;
import com.hcrcms.common.page.Pagination;

public interface CmsTopicReportMng {

	public List<CmsTopicReport> getListForTag(Integer channelId, boolean recommend,
			Integer count);

	public Pagination getPageForTag(Integer channelId, boolean recommend,
			int pageNo, int pageSize);

	public Pagination getPage(int pageNo, int pageSize);

	public CmsTopicReport findById(Integer id);
	
	public CmsTopicReport getId(Integer id);

	public void save(CmsTopicReport bean, Integer channelId);

	public void update(CmsTopicReport bean, Integer channelId);

	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public List<CmsTopicReport> getListByName(String topicName);

}