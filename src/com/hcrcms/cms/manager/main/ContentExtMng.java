package com.hcrcms.cms.manager.main;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentExt;

public interface ContentExtMng {
	public ContentExt save(ContentExt ext, Content content);

	public ContentExt update(ContentExt ext);
	
}