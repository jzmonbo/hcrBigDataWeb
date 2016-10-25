package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.assist.CmsReviewHead;

public interface CmsIpAddressMng {
	
	public List<CmsIpAddress> getList(double param);
	
	public CmsReviewHead getCmsReviewHead(int id);
	
}