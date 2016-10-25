package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.HotSpotNavigation;
import com.hcrcms.common.hibernate3.Updater;

public interface HotSpotNavigationDao {
	public List<HotSpotNavigation> getList();

	public HotSpotNavigation findById(Integer id);
	
	public List<HotSpotNavigation> findByName(String name);
	
	public HotSpotNavigation findByPath(String path);

	public HotSpotNavigation save(HotSpotNavigation bean);

	public HotSpotNavigation updateByUpdater(Updater<HotSpotNavigation> updater);

	public HotSpotNavigation deleteById(Integer id);
}