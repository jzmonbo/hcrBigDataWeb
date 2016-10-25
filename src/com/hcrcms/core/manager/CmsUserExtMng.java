package com.hcrcms.core.manager;

import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserExt;

public interface CmsUserExtMng {
	public CmsUserExt save(CmsUserExt ext, CmsUser user);

	public CmsUserExt update(CmsUserExt ext, CmsUser user);
}