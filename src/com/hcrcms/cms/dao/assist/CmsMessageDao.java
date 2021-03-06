package com.hcrcms.cms.dao.assist;

import java.util.Date;

import com.hcrcms.cms.entity.assist.CmsMessage;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsMessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
			int pageNo, int pageSize);

	public CmsMessage findById(Integer id);

	public CmsMessage save(CmsMessage bean);

	public CmsMessage updateByUpdater(Updater<CmsMessage> updater);
	
	public CmsMessage update(CmsMessage bean);

	public CmsMessage deleteById(Integer id);

	public CmsMessage[] deleteByIds(Integer[] ids);
}