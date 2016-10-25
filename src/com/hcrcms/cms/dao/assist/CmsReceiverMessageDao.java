package com.hcrcms.cms.dao.assist;

import java.util.Date;
import java.util.List;

import com.hcrcms.cms.entity.assist.CmsReceiverMessage;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;

public interface CmsReceiverMessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
			int pageNo, int pageSize);
	
	public List<CmsReceiverMessage> getList(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable);

	public CmsReceiverMessage findById(Integer id);

	public CmsReceiverMessage save(CmsReceiverMessage bean);

	public CmsReceiverMessage update(CmsReceiverMessage bean);

	public CmsReceiverMessage deleteById(Integer id);

	public CmsReceiverMessage[] deleteByIds(Integer[] ids);
	
	/**
	 * 获取用户消息列表
	 * @param site
	 * @param user
	 * @return
	 */
	public List<CmsReceiverMessage> getMyReceiverMessages(CmsSite site,CmsUser user);
	
	/**
	 * 把消息状态置为已读
	 */
	public void alreadyReadMessages(Integer rmid);
}