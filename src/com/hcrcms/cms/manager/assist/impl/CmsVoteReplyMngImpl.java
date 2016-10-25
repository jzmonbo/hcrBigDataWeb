package com.hcrcms.cms.manager.assist.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsVoteReplyDao;
import com.hcrcms.cms.entity.assist.CmsVoteReply;
import com.hcrcms.cms.manager.assist.CmsVoteReplyMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsVoteReplyMngImpl implements CmsVoteReplyMng {
	@Transactional(readOnly = true)
	public Pagination getPage(Integer  subTopicId, int pageNo, int pageSize){
		return dao.getPage(subTopicId, pageNo, pageSize);
	}
	@Transactional(readOnly = true)
	public CmsVoteReply findById(Integer id) {
		CmsVoteReply entity = dao.findById(id);
		return entity;
	}

	public CmsVoteReply save(CmsVoteReply bean) {
		dao.save(bean);
		return bean;
	}

	public CmsVoteReply update(CmsVoteReply bean) {
		Updater<CmsVoteReply> updater = new Updater<CmsVoteReply>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}


	public CmsVoteReply deleteById(Integer id) {
		CmsVoteReply bean = dao.deleteById(id);
		return bean;
	}

	public CmsVoteReply[] deleteByIds(Integer[] ids) {
		CmsVoteReply[] beans = new CmsVoteReply[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private CmsVoteReplyDao dao;

	@Autowired
	public void setDao(CmsVoteReplyDao dao) {
		this.dao = dao;
	}


}