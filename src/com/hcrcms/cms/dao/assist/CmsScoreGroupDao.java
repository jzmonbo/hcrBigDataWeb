package com.hcrcms.cms.dao.assist;

import com.hcrcms.cms.entity.assist.CmsScoreGroup;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsScoreGroupDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsScoreGroup findById(Integer id);
	
	public CmsScoreGroup findDefault(Integer siteId);

	public CmsScoreGroup save(CmsScoreGroup bean);

	public CmsScoreGroup updateByUpdater(Updater<CmsScoreGroup> updater);

	public CmsScoreGroup deleteById(Integer id);
}