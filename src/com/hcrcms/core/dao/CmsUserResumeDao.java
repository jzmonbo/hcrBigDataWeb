package com.hcrcms.core.dao;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.CmsUserResume;

public interface CmsUserResumeDao {
	public CmsUserResume findById(Integer id);

	public CmsUserResume save(CmsUserResume bean);

	public CmsUserResume updateByUpdater(Updater<CmsUserResume> updater);
}