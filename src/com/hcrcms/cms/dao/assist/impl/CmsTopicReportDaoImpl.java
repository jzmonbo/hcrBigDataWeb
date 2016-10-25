package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsTopicReportDao;
import com.hcrcms.cms.entity.assist.CmsTopicReport;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsTopicReportDaoImpl extends HibernateBaseDao<CmsTopicReport, Integer>
		implements CmsTopicReportDao {
	@SuppressWarnings("unchecked")
	public List<CmsTopicReport> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable) {
		Finder f = Finder.create("from CmsTopicReport bean where 1=1");
		f.setCacheable(cacheable);
		return find(f);
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable) {
		Finder f = Finder.create("from CmsTopicReport bean where 1=1");
		return find(f, pageNo, pageSize);
	}

	public CmsTopicReport findById(Integer id) {
		CmsTopicReport entity = get(id);
		return entity;
	}

	public CmsTopicReport getId(Integer id) {
		Finder f = Finder.create("from CmsTopicReport bean where bean.entId="+id);
		List<CmsTopicReport> lists = find(f);
		return lists.get(0);
	}

	public void save(CmsTopicReport bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsTopicReport entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public CmsTopicReport updateByUpdater(Updater<CmsTopicReport> updater) {
		return super.updateByUpdater(updater);
	}

	public List<CmsTopicReport> getListByName(String topicName) {
		Finder f = Finder.create("from CmsTopicReport bean where bean.isUse=1 and bean.topicName like '%"+topicName+"%'");
		return find(f);
		
	}

	@Override
	protected Class<CmsTopicReport> getEntityClass() {
		return CmsTopicReport.class;
	}
}