package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.common.page.Pagination;

public interface CmsTopicEnterpriseMng {

	public List<CmsTopicEnterprise> getListForTag(Integer channelId, boolean recommend,
			Integer count);

	public Pagination getPageForTag(Integer channelId, boolean recommend,
			int pageNo, int pageSize);

	public Pagination getPage(int pageNo, int pageSize);

	public CmsTopicEnterprise findById(Integer id);
	
	public CmsTopicEnterprise getId(Integer id);

	public void save(CmsTopicEnterprise bean, Integer channelId);

	public void update(CmsTopicEnterprise bean, Integer channelId);

	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public List<CmsTopicEnterprise> getListByName(String topicName);

	public CmsTopicEnterprise findByMeetId(Integer id);
}