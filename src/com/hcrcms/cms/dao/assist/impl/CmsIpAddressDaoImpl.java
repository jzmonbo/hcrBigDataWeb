package com.hcrcms.cms.dao.assist.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsIpAddressDao;
import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.assist.CmsReviewHead;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.core.web.util.RandomUtils;

@Repository
public class CmsIpAddressDaoImpl extends HibernateBaseDao<CmsIpAddress, Integer>
		implements CmsIpAddressDao {

	@SuppressWarnings("unchecked")
	public List<CmsIpAddress> getList(double param) {
		String ipAddress = RandomUtils.getProvinceString(param);
		Finder f = getFinder(ipAddress);
		f.setFirstResult(0);
		f.setMaxResults(10);
		return find(f);
	}

	private Finder getFinder(String param) {
		Finder f = Finder.create("from CmsIpAddress bean where 1=1");
		f.append(" and bean.ip1>="+param);
		return f;
	}

	public CmsReviewHead getCmsReviewHead(int id) {
		Finder f = Finder.create("from CmsReviewHead where id="+id);
		List<CmsReviewHead> reviewHeads = find(f);
		return reviewHeads.get(0);
	}

	protected Class<CmsIpAddress> getEntityClass() {
		return CmsIpAddress.class;
	}
}