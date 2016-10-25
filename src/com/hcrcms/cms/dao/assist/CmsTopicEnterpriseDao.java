package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsTopicEnterpriseDao {
	public List<CmsTopicEnterprise> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable);

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable);

	public CmsTopicEnterprise findById(Integer id);
	
	public CmsTopicEnterprise getId(Integer id);

	public void save(CmsTopicEnterprise bean);

	public CmsTopicEnterprise updateByUpdater(Updater<CmsTopicEnterprise> updater);

	public void deleteById(Integer id);
	
	public List<CmsTopicEnterprise> getListByName(String topicName);
	
	public CmsTopicEnterprise findByMeetId(Integer id);
}