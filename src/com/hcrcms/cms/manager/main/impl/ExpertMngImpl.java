package com.hcrcms.cms.manager.main.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.ExpertDao;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Service
@Transactional
public class ExpertMngImpl implements ExpertMng {
	@Transactional(readOnly = true)
	public List<Expert> getList() {
		return dao.getList();
	}

	@Transactional(readOnly = true)
	public Expert findById(Integer id) {
		Expert entity = dao.findById(id);
		return entity;
	}
	
	public Expert save(Expert bean) {
		//bean.init();
		dao.save(bean);
		return bean;
	}

	public Expert update(Expert bean) {
		Updater<Expert> updater = new Updater<Expert>(bean);
		Expert entity = dao.updateByUpdater(updater);
		return entity;
	}

	public Expert deleteById(Integer id) {
		Expert bean = dao.deleteById(id);
		return bean;
	}

	public Expert[] deleteByIds(Integer[] ids) {
		Expert[] beans = new Expert[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public List<Expert> findByName(String name) {
		return dao.findByName(name);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int option,Integer first, Integer count,String expertId,String author) {
		return dao.getListByChannelIdsForTag(channelIds, typeIds, titleImg,
				recommend, title,attr, orderBy, option,first, count,expertId,author);
	}
	
	@Transactional(readOnly = true)
	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, int pageNo, int pageSize,String expertId,String author) {
		return dao.getPageByChannelIdsForTag(channelIds, typeIds, titleImg,
				recommend, title,attr, orderBy, option, pageNo, pageSize,expertId,author);
	}
	
	@Transactional(readOnly = true)
	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int pageNo, int pageSize,String expertId,String author) {
		return dao.getPageBySiteIdsForTag(siteIds, typeIds, titleImg,
				recommend, title, attr,orderBy, pageNo, pageSize,expertId,author);
	}
	
	@Transactional(readOnly = true)
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, Integer first, Integer count,String expertId,String author) {
		return dao.getListBySiteIdsForTag(siteIds, typeIds, titleImg,
				recommend, title,attr, orderBy, first, count,expertId,author);
	}
	
	public List<Content> getListByName(String username) {
		return dao.getListByName(username);
	}

	public List<Content> getListByTime(String startTime, String endTime) {
		return dao.getListByTime(startTime,endTime);
	}

	/**
	 * 按所有专家文章时间倒序查询
	 * @return
	 */
	public List<Content> getListByDate() {
		return dao.getListByDate();
	}

	private ExpertDao dao;

	@Autowired
	public void setDao(ExpertDao dao) {
		this.dao = dao;
	}
}