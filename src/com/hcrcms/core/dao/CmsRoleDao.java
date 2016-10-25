package com.hcrcms.core.dao;

import java.util.List;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.CmsRole;

public interface CmsRoleDao {
	public List<CmsRole> getList();

	public CmsRole findById(Integer id);

	public CmsRole save(CmsRole bean);

	public CmsRole updateByUpdater(Updater<CmsRole> updater);

	public CmsRole deleteById(Integer id);
}