package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.assist.CmsReviewHead;

public interface CmsIpAddressDao{
	
	public List<CmsIpAddress> getList(double param);
	
	public CmsReviewHead getCmsReviewHead(int id);
	
}