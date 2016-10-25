package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsModelItem;
import com.hcrcms.common.hibernate3.Updater;

public interface CmsModelItemDao {
	public List<CmsModelItem> getList(Integer modelId, boolean isChannel,
			boolean hasDisabled);

	public CmsModelItem findById(Integer id);

	public CmsModelItem save(CmsModelItem bean);

	public CmsModelItem updateByUpdater(Updater<CmsModelItem> updater);

	public CmsModelItem deleteById(Integer id);
}