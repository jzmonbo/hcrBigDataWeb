package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.common.hibernate3.Updater;

public interface SiteRightLinkDao {
	public List<SiteRightLink> getList();

	public SiteRightLink findById(Integer id);
	
	public List<SiteRightLink> findByName(String name);
	
	public SiteRightLink findByPath(String path);

	public SiteRightLink save(SiteRightLink bean);

	public SiteRightLink updateByUpdater(Updater<SiteRightLink> updater);

	public SiteRightLink deleteById(Integer id);
	
	public List<SiteRightLink> findBySort(Integer start,Integer end);
}