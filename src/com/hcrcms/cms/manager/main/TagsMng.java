package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.CmsTags;

/**
 * 专家Manager接口
 * 
 */
public interface TagsMng {
	
	public List<CmsTags> getList();

	public CmsTags findById(Integer id);
	
	public List<CmsTags> findByName(String name);
	
	public CmsTags save(CmsTags bean);

	public CmsTags update(CmsTags bean);

	public CmsTags deleteById(Integer id);

	public CmsTags[] deleteByIds(Integer[] ids);
}