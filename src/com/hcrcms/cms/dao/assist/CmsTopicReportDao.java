package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsTopicReport;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsTopicReportDao {
	public List<CmsTopicReport> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable);

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable);

	public CmsTopicReport findById(Integer id);
	
	public CmsTopicReport getId(Integer id);

	public void save(CmsTopicReport bean);

	public CmsTopicReport updateByUpdater(Updater<CmsTopicReport> updater);

	public void deleteById(Integer id);
	
	public List<CmsTopicReport> getListByName(String topicName);
}