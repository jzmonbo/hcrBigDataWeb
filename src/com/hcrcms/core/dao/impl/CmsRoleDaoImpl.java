package com.hcrcms.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.core.dao.CmsRoleDao;
import com.hcrcms.core.entity.CmsRole;

@Repository
public class CmsRoleDaoImpl extends HibernateBaseDao<CmsRole, Integer>
		implements CmsRoleDao {
	@SuppressWarnings("unchecked")
	public List<CmsRole> getList() {
		String hql = "from CmsRole bean ";
		hql+=" order by bean.priority asc";
		return find(hql);
	}

	public CmsRole findById(Integer id) {
		CmsRole entity = get(id);
		return entity;
	}

	public CmsRole save(CmsRole bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsRole deleteById(Integer id) {
		CmsRole entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsRole> getEntityClass() {
		return CmsRole.class;
	}
}