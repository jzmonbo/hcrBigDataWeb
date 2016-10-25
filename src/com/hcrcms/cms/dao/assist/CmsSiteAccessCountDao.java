package com.hcrcms.cms.dao.assist;

import java.util.Date;
import java.util.List;

import com.hcrcms.cms.entity.assist.CmsSiteAccessCount;
import com.hcrcms.common.hibernate3.Updater;

/**
 * @author Zhang
 */
public interface CmsSiteAccessCountDao {

	public List<Object[]> statisticVisitorCountByDate(Integer siteId,Date begin,Date end);
	
	public List<Object[]> statisticVisitorCountByYear(Integer siteId,Integer year);

	public CmsSiteAccessCount save(CmsSiteAccessCount count);

	public CmsSiteAccessCount updateByUpdater(Updater<CmsSiteAccessCount> updater);

}
