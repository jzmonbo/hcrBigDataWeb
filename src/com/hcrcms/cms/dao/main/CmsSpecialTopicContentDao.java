package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsSpecialTopicContentDao {
	public List<CmsSpecialTopicContent> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable);

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable);

	public CmsSpecialTopicContent findById(Integer id);

	public void save(CmsSpecialTopicContent bean);

	public CmsSpecialTopicContent updateByUpdater(Updater<CmsSpecialTopicContent> updater);

	public void deleteById(Integer id);
	
	public CmsSpecialTopicContent findByTitle(String title);
}