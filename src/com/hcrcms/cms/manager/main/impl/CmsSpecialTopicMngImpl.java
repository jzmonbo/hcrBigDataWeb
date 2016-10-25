package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.CmsSpecialTopicDao;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsSpecialTopicMngImpl implements CmsSpecialTopicMng{
	@Transactional(readOnly = true)
	public List<CmsSpecialTopic> getListForTag(Integer channelId, boolean recommend,
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
	public CmsSpecialTopic findById(Integer id) {
		CmsSpecialTopic entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public CmsSpecialTopic getId(Integer id) {
		CmsSpecialTopic entity = dao.getId(id);
		return entity;
	}

	public void save(CmsSpecialTopic bean, Integer channelId) {
		dao.save(bean);
	}

	public void update(CmsSpecialTopic bean, Integer channelId) {
		Updater<CmsSpecialTopic> updater = new Updater<CmsSpecialTopic>(bean);
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
	
	public List<CmsSpecialTopic> getListByName(String topicName) {
		return dao.getListByName(topicName);
	}

	private CmsSpecialTopicDao dao;

	@Autowired
	public void setDao(CmsSpecialTopicDao dao) {
		this.dao = dao;
	}
}