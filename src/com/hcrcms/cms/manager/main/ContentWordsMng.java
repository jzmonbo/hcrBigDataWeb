package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsKeyword;

/**
 * 文章内容划词
 * 
 */
public interface ContentWordsMng {
	
	public List<CmsKeyword> getList();

	public CmsKeyword findById(Integer id);
	
	public List<CmsKeyword> findByName(String name);
	
	public CmsKeyword save(CmsKeyword bean);

	public CmsKeyword update(CmsKeyword bean);
	
	public void updateBatch(List<CmsKeyword> lists);

	public CmsKeyword deleteById(Integer id);

	public CmsKeyword[] deleteByIds(Integer[] ids);
}