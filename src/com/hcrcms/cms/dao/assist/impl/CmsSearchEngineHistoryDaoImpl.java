package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsSearchEngineHistoryDao;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.assist.CmsSearchEngineHistory;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.core.web.util.DateUtils;

@Repository
public class CmsSearchEngineHistoryDaoImpl extends
		HibernateBaseDao<CmsSearchEngineHistory, Integer> implements CmsSearchEngineHistoryDao {

	public List<CmsSearchEngineHistory> getYestoryList(int day) {
		String[] dates = DateUtils.getYestodayTime(day);
		Finder f = Finder.create("from CmsSearchEngineHistory bean where bean.createtime between '"+dates[0]+"' and '"+dates[1]+"' order by bean.searchId");
		return find(f);
	}

	@Override
	protected Class<CmsSearchEngineHistory> getEntityClass() {
		return CmsSearchEngineHistory.class;
	}
}