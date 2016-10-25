package com.hcrcms.cms.manager.assist;

import com.hcrcms.cms.entity.assist.CmsGuestbook;
import com.hcrcms.cms.entity.assist.CmsGuestbookExt;

public interface CmsGuestbookExtMng {
	public CmsGuestbookExt save(CmsGuestbookExt ext, CmsGuestbook guestbook);

	public CmsGuestbookExt update(CmsGuestbookExt ext);
}