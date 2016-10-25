package com.hcrcms.core.dao;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.CmsConfig;

public interface CmsConfigDao {
	public CmsConfig findById(Integer id);

	public CmsConfig updateByUpdater(Updater<CmsConfig> updater);
}