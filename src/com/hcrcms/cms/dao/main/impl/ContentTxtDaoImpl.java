package com.hcrcms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.ContentTxtDao;
import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class ContentTxtDaoImpl extends HibernateBaseDao<ContentTxt, Integer>
		implements ContentTxtDao {
	public ContentTxt findById(Integer id) {
		ContentTxt entity = get(id);
		return entity;
	}

	public ContentTxt save(ContentTxt bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ContentTxt> getEntityClass() {
		return ContentTxt.class;
	}
}