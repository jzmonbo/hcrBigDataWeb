package com.hcrcms.core.manager;

import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserResume;

public interface CmsUserResumeMng {
	public CmsUserResume save(CmsUserResume ext, CmsUser user);

	public CmsUserResume update(CmsUserResume ext, CmsUser user);
}