package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.SiteRightLink;

/**
 * 专家Manager接口
 * 
 */
public interface SiteRightLinkMng {
	
	public List<SiteRightLink> getList();

	public SiteRightLink findById(Integer id);
	
	public List<SiteRightLink> findByName(String name);
	
	public SiteRightLink save(SiteRightLink bean);

	public SiteRightLink update(SiteRightLink bean);

	public SiteRightLink deleteById(Integer id);

	public SiteRightLink[] deleteByIds(Integer[] ids);
	
	public List<SiteRightLink> findBySort(Integer start,Integer end);
}