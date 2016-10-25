package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsSearchEngineDao;
import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsSearchEngineDaoImpl extends
		HibernateBaseDao<CmsSearchEngine, Integer> implements CmsSearchEngineDao {
	@SuppressWarnings("unchecked")
	public List<CmsSearchEngine> getList() {
		Finder f = Finder.create("from CmsSearchEngine bean where 1=1");
		f.append(" order by abs(bean.category) asc,abs(bean.total) desc");
		return find(f);
	}

	public CmsSearchEngine findById(Integer id) {
		CmsSearchEngine entity = get(id);
		return entity;
	}

	public void save(CmsSearchEngine bean) {
		getSession().save(bean);
	}

	public void deleteById(Integer id) {
		CmsSearchEngine entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
			int pageSize, boolean cacheable,String companyName) {
		Finder f = Finder.create("from CmsSearchEngine bean where 1=1");
		if (companyName != null && companyName.length() > 0){
			f.append(" and bean.company like '%"+companyName+"%'");
		}
		return find(f, pageNo, pageSize);
	}
	
	public CmsSearchEnginMM getMaxMin() {
		Finder f = Finder.create("select max(cast(e.bdss as int)) as bdssMax,min(cast(e.bdss as int)) as bdssMin,max(cast(e.bdssxw as int)) as bdssxwMax,min(cast(e.bdssxw as int)) as bdssxwMin,max(cast(e.sgss as int)) as sgssMax,min(cast(e.sgss as int)) as sgssMin,max(cast(e.sgssxw as int)) as sgssxwMax,min(cast(e.sgssxw as int)) as sgssxwMin,max(cast(e.sgwxss as int)) as sgwxssMax,min(cast(e.sgwxss as int)) as sgwxssMin,max(cast(e.hs as int)) as hsMax,min(cast(e.hs as int)) as hsMin,max(cast(e.hsxw as int)) as hsxwMax,min(cast(e.hsxw as int)) as hsxwMin  from CmsSearchEngine e");
		Query query = getSession().createQuery(f.getOrigHql());
		List list = query.list();
		Object[] lists = (Object[]) list.get(0);
		CmsSearchEnginMM searchEnginMM = new CmsSearchEnginMM();
		searchEnginMM.setBdssMax(lists[0].toString());
		searchEnginMM.setBdssMin(lists[1].toString());
		searchEnginMM.setBdssxwMax(lists[2].toString());
		searchEnginMM.setBdssxwMin(lists[3].toString());
		searchEnginMM.setSgssMax(lists[4].toString());
		searchEnginMM.setSgssMin(lists[5].toString());
		searchEnginMM.setSgssxwMax(lists[6].toString());
		searchEnginMM.setSgssxwMin(lists[7].toString());
		searchEnginMM.setSgwxssMax(lists[8].toString());
		searchEnginMM.setSgwxssMin(lists[9].toString());
		searchEnginMM.setHsMax(lists[10].toString());
		searchEnginMM.setHsMin(lists[11].toString());
		searchEnginMM.setHsxwMax(lists[12].toString());
		searchEnginMM.setHsxwMin(lists[13].toString());
		return searchEnginMM;
	}

	public List<CmsSearchEngine> getListSort() {
		Finder f = Finder.create("from CmsSearchEngine bean order by bean.searchId");
		return find(f);
	}

	@Override
	protected Class<CmsSearchEngine> getEntityClass() {
		return CmsSearchEngine.class;
	}

	public void updateBatch(List<CmsSearchEngine> searchEngineList) {
		if (searchEngineList != null && searchEngineList.size() > 0){
			for (int i=0;i<searchEngineList.size();i++){
				getSession().update(searchEngineList.get(i));
			}
		}
	}
}