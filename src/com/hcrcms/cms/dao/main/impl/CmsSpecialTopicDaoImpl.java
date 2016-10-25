package com.hcrcms.cms.dao.main.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.CmsSpecialTopicDao;
import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsSpecialTopicDaoImpl extends HibernateBaseDao<CmsSpecialTopic, Integer>
		implements CmsSpecialTopicDao {
	@SuppressWarnings("unchecked")
	public List<CmsSpecialTopic> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable) {
		Finder f = Finder.create("from CmsSpecialTopic bean where isUse=1");
		f.setCacheable(cacheable);
		return find(f);
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable) {
		Finder f = Finder.create("from CmsSpecialTopic bean where 1=1");
		return find(f, pageNo, pageSize);
	}

	public CmsSpecialTopic findById(Integer id) {
		CmsSpecialTopic entity = get(id);
		return entity;
	}

	public CmsSpecialTopic getId(Integer id) {
		Finder f = Finder.create("from CmsSpecialTopic bean where bean.id="+id);
		List<CmsSpecialTopic> lists = find(f);
		return lists.get(0);
	}

	public void save(CmsSpecialTopic bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsSpecialTopic entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public CmsSpecialTopic updateByUpdater(Updater<CmsSpecialTopic> updater) {
		return super.updateByUpdater(updater);
	}

	public List<CmsSpecialTopic> getListByName(String topicName) {
		Finder f = Finder.create("from CmsSpecialTopic bean where bean.isUse=1 and bean.topicName like '%"+topicName+"%'");
		return find(f);
		
	}

	@Override
	protected Class<CmsSpecialTopic> getEntityClass() {
		return CmsSpecialTopic.class;
	}
}