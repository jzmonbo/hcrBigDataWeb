package com.hcrcms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.ChannelExtDao;
import com.hcrcms.cms.entity.main.ChannelExt;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class ChannelExtDaoImpl extends HibernateBaseDao<ChannelExt, Integer>
		implements ChannelExtDao {
	public ChannelExt save(ChannelExt bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ChannelExt> getEntityClass() {
		return ChannelExt.class;
	}
}