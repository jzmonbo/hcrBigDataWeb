package com.hcrcms.core.manager;

import java.util.Date;

import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsConfigAttr;
import com.hcrcms.core.entity.MarkConfig;
import com.hcrcms.core.entity.MemberConfig;

public interface CmsConfigMng {
	public CmsConfig get();

	public void updateCountCopyTime(Date d);

	public void updateCountClearTime(Date d);

	public CmsConfig update(CmsConfig bean);

	public MarkConfig updateMarkConfig(MarkConfig mark);

	public void updateMemberConfig(MemberConfig memberConfig);
	
	public void updateConfigAttr(CmsConfigAttr configAttr);
}