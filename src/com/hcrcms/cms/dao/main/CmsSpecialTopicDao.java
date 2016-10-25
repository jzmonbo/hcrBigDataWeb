package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsSpecialTopicDao {
	public List<CmsSpecialTopic> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable);

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable);

	public CmsSpecialTopic findById(Integer id);
	
	public CmsSpecialTopic getId(Integer id);

	public void save(CmsSpecialTopic bean);

	public CmsSpecialTopic updateByUpdater(Updater<CmsSpecialTopic> updater);

	public void deleteById(Integer id);
	
	public List<CmsSpecialTopic> getListByName(String topicName);
}