package com.hcrcms.core.dao;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.CmsSiteCompany;

public interface CmsSiteCompanyDao {

	public CmsSiteCompany save(CmsSiteCompany bean);

	public CmsSiteCompany updateByUpdater(Updater<CmsSiteCompany> updater);
}