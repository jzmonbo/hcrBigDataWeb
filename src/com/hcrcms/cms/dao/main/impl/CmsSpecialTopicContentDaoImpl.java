package com.hcrcms.cms.dao.main.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.CmsSpecialTopicContentDao;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsSpecialTopicContentDaoImpl extends HibernateBaseDao<CmsSpecialTopicContent, Integer>
		implements CmsSpecialTopicContentDao {
	@SuppressWarnings("unchecked")
	public List<CmsSpecialTopicContent> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable) {
		Finder f = Finder.create("from CmsSpecialTopicContent bean where 1=1");
		f.setCacheable(cacheable);
		return find(f);
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable) {
		Finder f = Finder.create("from CmsSpecialTopicContent bean where 1=1");
		return find(f, pageNo, pageSize);
	}

	public CmsSpecialTopicContent findById(Integer id) {
		CmsSpecialTopicContent entity = get(id);
		return entity;
	}

	public void save(CmsSpecialTopicContent bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsSpecialTopicContent entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public CmsSpecialTopicContent updateByUpdater(Updater<CmsSpecialTopicContent> updater) {
		return super.updateByUpdater(updater);
	}

	public CmsSpecialTopicContent findByTitle(String title) {
		Finder f = Finder.create("from CmsSpecialTopicContent bean where title='" + title + "'");
		List<CmsSpecialTopicContent> lists = find(f);
		if (lists != null && lists.size() > 0){
			return lists.get(0);
		}else{
			return null;
		}
	}

	@Override
	protected Class<CmsSpecialTopicContent> getEntityClass() {
		return CmsSpecialTopicContent.class;
	}
}