package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.entity.main.HotSpotNavigation;

/**
 * 专家Manager接口
 * 
 */
public interface HotSpotNavigationMng {
	
	public List<HotSpotNavigation> getList();

	public HotSpotNavigation findById(Integer id);
	
	public List<HotSpotNavigation> findByName(String name);
	
	public HotSpotNavigation save(HotSpotNavigation bean);

	public HotSpotNavigation update(HotSpotNavigation bean);

	public HotSpotNavigation deleteById(Integer id);

	public HotSpotNavigation[] deleteByIds(Integer[] ids);
}