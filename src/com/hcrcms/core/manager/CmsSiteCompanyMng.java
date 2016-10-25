package com.hcrcms.core.manager;

import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsSiteCompany;

public interface CmsSiteCompanyMng {
	public CmsSiteCompany save(CmsSite site,CmsSiteCompany bean);

	public CmsSiteCompany update(CmsSiteCompany bean);
}