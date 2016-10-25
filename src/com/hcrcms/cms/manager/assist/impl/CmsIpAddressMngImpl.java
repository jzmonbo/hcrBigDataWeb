package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsIpAddressDao;
import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.assist.CmsReviewHead;
import com.hcrcms.cms.manager.assist.CmsIpAddressMng;

@Service
@Transactional
public class CmsIpAddressMngImpl implements CmsIpAddressMng {

	@Transactional(readOnly = true)
	public List<CmsIpAddress> getList(double param) {
		return dao.getList(param);
	}

	@Transactional(readOnly = true)
	public CmsReviewHead getCmsReviewHead(int id) {
		return dao.getCmsReviewHead(id);
	}

	private CmsIpAddressDao dao;
	
	@Autowired
	public void setDao(CmsIpAddressDao dao) {
		this.dao = dao;
	}

}