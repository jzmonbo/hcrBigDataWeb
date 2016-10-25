package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.TopLink;

/**
 * 专家Manager接口
 * 
 */
public interface TopLinkMng {
	
	public List<TopLink> getList();

	public TopLink findById(Integer id);
	
	public List<TopLink> findByName(String name);
	
	public TopLink save(TopLink bean);

	public TopLink update(TopLink bean);

	public TopLink deleteById(Integer id);

	public TopLink[] deleteByIds(Integer[] ids);
}