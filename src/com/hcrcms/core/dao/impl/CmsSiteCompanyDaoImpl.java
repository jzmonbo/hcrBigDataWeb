package com.hcrcms.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.core.dao.CmsSiteCompanyDao;
import com.hcrcms.core.entity.CmsSiteCompany;

@Repository
public class CmsSiteCompanyDaoImpl extends
		HibernateBaseDao<CmsSiteCompany, Integer> implements CmsSiteCompanyDao {

	public CmsSiteCompany save(CmsSiteCompany bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<CmsSiteCompany> getEntityClass() {
		return CmsSiteCompany.class;
	}
}