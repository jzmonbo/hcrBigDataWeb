package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsKeyword;

public interface CmsKeywordMng {
	
	public List<CmsKeyword> getList(Integer siteId,boolean onlyEnabled, boolean cacheable);
	
	public List<CmsKeyword> getListBySiteId(Integer siteId,
			boolean onlyEnabled, boolean cacheable);

	public String attachKeyword(Integer siteId, String txt);

	public CmsKeyword findById(Integer id);

	public CmsKeyword save(CmsKeyword bean);

	public void updateKeywords(Integer[] ids, String[] names, String[] urls,
			Boolean[] disableds);

	public CmsKeyword deleteById(Integer id);

	public CmsKeyword[] deleteByIds(Integer[] ids);
}