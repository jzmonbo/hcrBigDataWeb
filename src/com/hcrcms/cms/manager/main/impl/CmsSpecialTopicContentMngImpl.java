package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.CmsSpecialTopicContentDao;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.manager.main.CmsSpecialTopicContentMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsSpecialTopicContentMngImpl implements CmsSpecialTopicContentMng{
	@Transactional(readOnly = true)
	public List<CmsSpecialTopicContent> getListForTag(Integer channelId, boolean recommend,
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
	public CmsSpecialTopicContent findById(Integer id) {
		CmsSpecialTopicContent entity = dao.findById(id);
		return entity;
	}

	public void save(CmsSpecialTopicContent bean, Integer channelId) {
		dao.save(bean);
	}

	public void update(CmsSpecialTopicContent bean, Integer channelId) {
		Updater<CmsSpecialTopicContent> updater = new Updater<CmsSpecialTopicContent>(bean);
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
	
	public CmsSpecialTopicContent findByTitle(String title) {
		return dao.findByTitle(title);
	}

	private CmsSpecialTopicContentDao dao;

	@Autowired
	public void setDao(CmsSpecialTopicContentDao dao) {
		this.dao = dao;
	}
}