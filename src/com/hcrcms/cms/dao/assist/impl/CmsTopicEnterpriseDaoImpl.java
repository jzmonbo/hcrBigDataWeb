package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsTopicEnterpriseDao;
import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsTopicEnterpriseDaoImpl extends HibernateBaseDao<CmsTopicEnterprise, Integer>
		implements CmsTopicEnterpriseDao {
	@SuppressWarnings("unchecked")
	public List<CmsTopicEnterprise> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable) {
		Finder f = Finder.create("from CmsTopicEnterprise bean where 1=1");
		f.setCacheable(cacheable);
		return find(f);
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable) {
		Finder f = Finder.create("from CmsTopicEnterprise bean where 1=1");
		return find(f, pageNo, pageSize);
	}

	public CmsTopicEnterprise findById(Integer id) {
		CmsTopicEnterprise entity = get(id);
		return entity;
	}

	public CmsTopicEnterprise getId(Integer id) {
		Finder f = Finder.create("from CmsTopicEnterprise bean where bean.entId="+id);
		List<CmsTopicEnterprise> lists = find(f);
		return lists.get(0);
	}

	public void save(CmsTopicEnterprise bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsTopicEnterprise entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public CmsTopicEnterprise updateByUpdater(Updater<CmsTopicEnterprise> updater) {
		return super.updateByUpdater(updater);
	}

	public List<CmsTopicEnterprise> getListByName(String topicName) {
		Finder f = Finder.create("from CmsTopicEnterprise bean where bean.isUse=1 and bean.topicName like '%"+topicName+"%'");
		return find(f);
		
	}

	public CmsTopicEnterprise findByMeetId(Integer id) {
		Finder f = Finder.create("from CmsTopicEnterprise bean where bean.meetId="+id);
		List<CmsTopicEnterprise> list = find(f); 
		if (list != null && list.size() > 0){
			return list.get(0);
		}else{
			return new CmsTopicEnterprise();
		}
	}

	@Override
	protected Class<CmsTopicEnterprise> getEntityClass() {
		return CmsTopicEnterprise.class;
	}
}