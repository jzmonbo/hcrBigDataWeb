package com.hcrcms.cms.dao.main.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.ContentTypeDao;
import com.hcrcms.cms.entity.main.ContentType;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;

@Repository
public class ContentTypeDaoImpl extends HibernateBaseDao<ContentType, Integer>
		implements ContentTypeDao {
	@SuppressWarnings("unchecked")
	public List<ContentType> getList(boolean containDisabled) {
		Finder f = Finder.create("from ContentType bean");
		if (!containDisabled) {
			f.append(" where bean.disabled=false");
		}
		f.append(" order by bean.id asc");
		return find(f);
	}

	public ContentType getDef() {
		String hql = "from ContentType bean"
				+ " where bean.disabled=false order by bean.id asc";
		Query query = getSession().createQuery(hql).setMaxResults(1);
		List<?> list = query.list();
		if (list.size() > 0) {
			return (ContentType) list.get(0);
		} else {
			return null;
		}
	}

	public ContentType findById(Integer id) {
		ContentType entity = get(id);
		return entity;
	}

	public ContentType save(ContentType bean) {
		getSession().save(bean);
		return bean;
	}

	public ContentType deleteById(Integer id) {
		ContentType entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<ContentType> getEntityClass() {
		return ContentType.class;
	}
}