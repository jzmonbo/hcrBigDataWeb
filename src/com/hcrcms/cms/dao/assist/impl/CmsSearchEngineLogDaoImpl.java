package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsSearchEngineLogDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngineLog;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.web.util.DateUtils;

@Repository
public class CmsSearchEngineLogDaoImpl extends
		HibernateBaseDao<CmsSearchEngineLog, Integer> implements CmsSearchEngineLogDao {
	@SuppressWarnings("unchecked")
	public List<CmsSearchEngineLog> getList() {
		Finder f = Finder.create("from CmsSearchEngineLog bean where 1=1");
		return find(f);
	}

	public CmsSearchEngineLog findById(Integer id) {
		CmsSearchEngineLog entity = get(id);
		return entity;
	}

	public void save(CmsSearchEngineLog bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsSearchEngineLog entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable,String companyName,String startTime,String endTime) {
		Finder f = Finder.create("from CmsSearchEngineLog bean where 1=1");
		if (companyName != null && companyName.length() > 0){
			f.append(" and bean.company like '%"+companyName+"%' ");
		}
		if (!StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)){
			f.append("and bean.createtime between '" + startTime + "' and '" + endTime + "'");
		}else{
			String[] times = DateUtils.getCurrentDayTime();
			f.append("and bean.createtime between '" + times[0] + "' and '" + times[1] + "'");
		}
		return find(f, pageNo, pageSize);
	}
	

	public void saveBatch(List<CmsSearchEngineLog> searchEngineLogList) {
		if (searchEngineLogList != null && searchEngineLogList.size() > 0){
			for (int i=0;i<searchEngineLogList.size();i++){
				getSession().save(searchEngineLogList.get(i));
			}
		}
	}

	public List<CmsSearchEngineLog> getListSort() {
		Finder f = Finder.create("from CmsSearchEngineLog bean order by bean.slogId");
		return find(f);
	}

	@Override
	protected Class<CmsSearchEngineLog> getEntityClass() {
		return CmsSearchEngineLog.class;
	}

	public void updateBatch(List<CmsSearchEngineLog> searchEngineLogList) {
		if (searchEngineLogList != null && searchEngineLogList.size() > 0){
			for (int i=0;i<searchEngineLogList.size();i++){
				getSession().update(searchEngineLogList.get(i));
			}
		}
	}

	public List<CmsSearchEngineLog> getListByStartEndTime(String startTime,
			String endTime) {
		Finder f = Finder.create("from CmsSearchEngineLog bean where bean.createtime between '"+startTime+"' and '"+endTime+"'");
		return find(f);
	}
	
}