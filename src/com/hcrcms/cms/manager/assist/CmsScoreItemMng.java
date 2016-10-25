package com.hcrcms.cms.manager.assist;

import com.hcrcms.cms.entity.assist.CmsScoreItem;
import com.hcrcms.common.page.Pagination;

public interface CmsScoreItemMng {
	public Pagination getPage(Integer groupId,int pageNo, int pageSize);

	public CmsScoreItem findById(Integer id);

	public CmsScoreItem save(CmsScoreItem bean);

	public CmsScoreItem update(CmsScoreItem bean);

	public CmsScoreItem deleteById(Integer id);
	
	public CmsScoreItem[] deleteByIds(Integer[] ids);
}