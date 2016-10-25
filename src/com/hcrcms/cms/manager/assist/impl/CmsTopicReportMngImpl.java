package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsTopicReportDao;
import com.hcrcms.cms.entity.assist.CmsTopicReport;
import com.hcrcms.cms.manager.assist.CmsTopicReportMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsTopicReportMngImpl implements CmsTopicReportMng{
	@Transactional(readOnly = true)
	public List<CmsTopicReport> getListForTag(Integer channelId, boolean recommend,
			Integer count) {
		return dao.getList(channelId, recommend, count, true);
	}

	@Transactional(readOnly = true)
	public Pagination getPageForTag(Integer channelId, boolean recommend,
			int pageNo, int pageSize) {
		return dao.getPage(channelId, recommend, pageNo, pageSize, true);
	}

	@Transactional(readOnly = true)
	public Pagination getPage(int pageNo, int pageSize) {
		Pagination page = dao.getPage(null, false, pageNo, pageSize, false);
		return page;
	}

	@Transactional(readOnly = true)
	public CmsTopicReport findById(Integer id) {
		CmsTopicReport entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public CmsTopicReport getId(Integer id) {
		CmsTopicReport entity = dao.getId(id);
		return entity;
	}

	public void save(CmsTopicReport bean, Integer channelId) {
		dao.save(bean);
	}

	public void update(CmsTopicReport bean, Integer channelId) {
		Updater<CmsTopicReport> updater = new Updater<CmsTopicReport>(bean);
		dao.updateByUpdater(updater);
	}

	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	public void deleteByIds(Integer[] ids) {
		for (int i = 0, len = ids.length; i < len; i++) {
			deleteById(ids[i]);
		}
	}
	
	public List<CmsTopicReport> getListByName(String topicName) {
		return dao.getListByName(topicName);
	}

	private CmsTopicReportDao dao;

	@Autowired
	public void setDao(CmsTopicReportDao dao) {
		this.dao = dao;
	}
}