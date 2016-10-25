package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.common.hibernate3.Updater;

public interface ContentWordsDao {
	public List<CmsKeyword> getList();

	public CmsKeyword findById(Integer id);
	
	public List<CmsKeyword> findByName(String name);
	
	public CmsKeyword findByPath(String path);

	public CmsKeyword save(CmsKeyword bean);

	public CmsKeyword updateByUpdater(Updater<CmsKeyword> updater);
	
	public void updateBatch(List<CmsKeyword> lists);

	public CmsKeyword deleteById(Integer id);
}