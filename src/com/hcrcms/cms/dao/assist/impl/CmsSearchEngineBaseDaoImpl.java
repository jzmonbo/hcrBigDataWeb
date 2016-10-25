package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsSearchEngineBaseDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngineBase;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class CmsSearchEngineBaseDaoImpl extends
		HibernateBaseDao<CmsSearchEngineBase, Integer> implements CmsSearchEngineBaseDao {
	@SuppressWarnings("unchecked")
	public List<CmsSearchEngineBase> getList() {
		Finder f = Finder.create("from CmsSearchEngine bean where 1=1");
		f.append(" order by abs(bean.category) asc,abs(bean.total) desc");
		return find(f);
	}

	public List<CmsSearchEngineBase> getListSort() {
		Finder f = Finder.create("from CmsSearchEngineBase bean order by bean.searchId");
		return find(f);
	}

	@Override
	protected Class<CmsSearchEngineBase> getEntityClass() {
		return CmsSearchEngineBase.class;
	}

}