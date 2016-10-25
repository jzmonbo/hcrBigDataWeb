package com.hcrcms.cms.manager.assist.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsReceiverMessageDao;
import com.hcrcms.cms.entity.assist.CmsReceiverMessage;
import com.hcrcms.cms.manager.assist.CmsReceiverMessageMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;


@Service
@Transactional
public class CmsReceiverMessageMngImpl implements CmsReceiverMessageMng {

	public Pagination getPage(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
			int pageNo, int pageSize) {
		return dao.getPage(siteId, sendUserId, receiverUserId, title,
				sendBeginTime, sendEndTime, status, box, cacheable, pageNo,
				pageSize);
	}
	
	public List<CmsReceiverMessage> getList(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable) {
		return dao.getList(siteId, sendUserId, receiverUserId, title,
				sendBeginTime, sendEndTime, status, box, cacheable);
	}

	public CmsReceiverMessage findById(Integer id) {
		return dao.findById(id);
	}

	public CmsReceiverMessage save(CmsReceiverMessage bean) {
		return dao.save(bean);
	}

	public CmsReceiverMessage update(CmsReceiverMessage bean) {
		return dao.update(bean);
	}

	public CmsReceiverMessage deleteById(Integer id) {
		return dao.deleteById(id);
	}

	public CmsReceiverMessage[] deleteByIds(Integer[] ids) {
		return dao.deleteByIds(ids);
	}

	public List<CmsReceiverMessage> getMyReceiverMessages(CmsSite site,CmsUser user) {
		return dao.getMyReceiverMessages(site,user);
	}

	public void alreadyReadMessages(Integer rmid) {
		dao.alreadyReadMessages(rmid);
	}

	@Autowired
	private CmsReceiverMessageDao dao;

}
