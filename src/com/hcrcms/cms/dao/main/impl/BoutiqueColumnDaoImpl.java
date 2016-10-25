package com.hcrcms.cms.dao.main.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.BoutiqueColumnDao;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.entity.main.JingPinSportSet;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class BoutiqueColumnDaoImpl extends HibernateBaseDao<JingPinSportSet, Integer>
		implements BoutiqueColumnDao {
	@SuppressWarnings("unchecked")
	public List<JingPinSportSet> getList() {
		Finder f = Finder.create("from JingPinSportSet bean where 1=1 ");
		return find(f);
	}

	public JingPinSportSet findById(Integer id) {
		JingPinSportSet entity = get(id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public JingPinSportSet findByPath(String path){
		String hql = "from JingPinSportSet bean where bean.path=:path";
		List<JingPinSportSet> list = getSession().createQuery(hql).setParameter("path", path).setMaxResults(1).list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public JingPinSportSet save(JingPinSportSet bean) {
		getSession().save(bean);
		return bean;
	}

	public JingPinSportSet deleteById(Integer id) {
		JingPinSportSet entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<JingPinSportSet> getEntityClass() {
		return JingPinSportSet.class;
	}
}