package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsTopicEnterpriseDao;
import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.manager.assist.CmsTopicEnterpriseMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class CmsTopicEnterpriseMngImpl implements CmsTopicEnterpriseMng{
	@Transactional(readOnly = true)
	public List<CmsTopicEnterprise> getListForTag(Integer channelId, boolean recommend,
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
	public CmsTopicEnterprise findById(Integer id) {
		CmsTopicEnterprise entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public CmsTopicEnterprise getId(Integer id) {
		CmsTopicEnterprise entity = dao.getId(id);
		return entity;
	}

	public void save(CmsTopicEnterprise bean, Integer channelId) {
		dao.save(bean);
	}

	public void update(CmsTopicEnterprise bean, Integer channelId) {
		Updater<CmsTopicEnterprise> updater = new Updater<CmsTopicEnterprise>(bean);
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
	
	public List<CmsTopicEnterprise> getListByName(String topicName) {
		return dao.getListByName(topicName);
	}

	public CmsTopicEnterprise findByMeetId(Integer id) {
		return dao.findByMeetId(id);
	}

	private CmsTopicEnterpriseDao dao;

	@Autowired
	public void setDao(CmsTopicEnterpriseDao dao) {
		this.dao = dao;
	}
}