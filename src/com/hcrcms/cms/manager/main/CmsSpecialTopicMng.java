package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.common.page.Pagination;

public interface CmsSpecialTopicMng {

	public List<CmsSpecialTopic> getListForTag(Integer channelId, boolean recommend,
			Integer count);

	public Pagination getPageForTag(Integer channelId, boolean recommend,
			int pageNo, int pageSize);

	public Pagination getPage(int pageNo, int pageSize);

	public CmsSpecialTopic findById(Integer id);
	
	public CmsSpecialTopic getId(Integer id);

	public void save(CmsSpecialTopic bean, Integer channelId);

	public void update(CmsSpecialTopic bean, Integer channelId);

	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public List<CmsSpecialTopic> getListByName(String topicName);

}