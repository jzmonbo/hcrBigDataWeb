package com.hcrcms.cms.dao.main;

import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.common.hibernate3.Updater;

public interface ContentCheckDao {
	public ContentCheck findById(Long id);

	public ContentCheck save(ContentCheck bean);

	public ContentCheck updateByUpdater(Updater<ContentCheck> updater);
}