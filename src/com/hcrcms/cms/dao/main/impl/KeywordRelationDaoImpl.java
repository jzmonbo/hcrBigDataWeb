package com.hcrcms.cms.dao.main.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.KeywordRelationDao;
import com.hcrcms.cms.entity.main.KeywordRelation;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class KeywordRelationDaoImpl extends HibernateBaseDao<KeywordRelation, Integer>
		implements KeywordRelationDao {
	@SuppressWarnings("unchecked")
	public List<KeywordRelation> getList() {
		Finder f = Finder.create("from KeywordRelation bean where 1=1 ");
		f.append(" order by bean.id");
		return find(f);
	}

	public KeywordRelation findById(Integer id) {
		KeywordRelation entity = get(id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public KeywordRelation findByPath(String path){
		String hql = "from KeywordRelation bean where bean.path=:path";
		List<KeywordRelation> list = getSession().createQuery(hql).setParameter("path", path).setMaxResults(1).list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public KeywordRelation save(KeywordRelation bean) {
		getSession().save(bean);
		return bean;
	}

	public KeywordRelation deleteById(Integer id) {
		KeywordRelation entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public List<KeywordRelation> findByName(String name) {
		Finder f = Finder.create("from KeywordRelation bean where 1=1 ");
		f.append(" and name='"+name+"'");
		return find(f);
	}

	@Override
	protected Class<KeywordRelation> getEntityClass() {
		return KeywordRelation.class;
	}
}