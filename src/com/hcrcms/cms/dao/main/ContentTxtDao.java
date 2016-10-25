package com.hcrcms.cms.dao.main;

import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.common.hibernate3.Updater;

public interface ContentTxtDao {
	public ContentTxt findById(Integer id);

	public ContentTxt save(ContentTxt bean);

	public ContentTxt updateByUpdater(Updater<ContentTxt> updater);
}