package com.hcrcms.core.dao;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.CmsUserExt;

public interface CmsUserExtDao {
	public CmsUserExt findById(Integer id);

	public CmsUserExt save(CmsUserExt bean);

	public CmsUserExt updateByUpdater(Updater<CmsUserExt> updater);
}