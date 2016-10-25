package com.hcrcms.cms.manager.main;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentTxt;

public interface ContentTxtMng {
	public ContentTxt save(ContentTxt txt, Content content);

	public ContentTxt update(ContentTxt txt, Content content);
}