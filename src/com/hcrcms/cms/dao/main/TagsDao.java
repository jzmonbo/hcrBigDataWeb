package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsTags;
import com.hcrcms.common.hibernate3.Updater;

public interface TagsDao {
	public List<CmsTags> getList();

	public CmsTags findById(Integer id);
	
	public List<CmsTags> findByName(String name);
	
	public CmsTags findByPath(String path);

	public CmsTags save(CmsTags bean);

	public CmsTags updateByUpdater(Updater<CmsTags> updater);

	public CmsTags deleteById(Integer id);
}