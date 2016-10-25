package com.hcrcms.cms.dao.assist;

import java.util.Date;

import com.hcrcms.cms.entity.assist.CmsSiteAccessPages;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

/**
 * @author Zhang
 */
public interface CmsSiteAccessPagesDao {

	public CmsSiteAccessPages findAccessPage(String sessionId, Integer pageIndex);
	
	public Pagination findPages(Integer siteId,Integer orderBy,Integer pageNo,Integer pageSize);

	public CmsSiteAccessPages save(CmsSiteAccessPages access);

	public CmsSiteAccessPages updateByUpdater(Updater<CmsSiteAccessPages> updater);

	public void clearByDate(Date date);

}
