package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.common.page.Pagination;

public interface CmsSpecialTopicContentMng {

	public List<CmsSpecialTopicContent> getListForTag(Integer channelId, boolean recommend,
			Integer count);

	public Pagination getPageForTag(Integer channelId, boolean recommend,
			int pageNo, int pageSize);

	public Pagination getPage(int pageNo, int pageSize);

	public CmsSpecialTopicContent findById(Integer id);

	public void save(CmsSpecialTopicContent bean, Integer channelId);

	public void update(CmsSpecialTopicContent bean, Integer channelId);

	public void deleteById(Integer id);

	public void deleteByIds(Integer[] ids);
	
	public CmsSpecialTopicContent findByTitle(String title);
}