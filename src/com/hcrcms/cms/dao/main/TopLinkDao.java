package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.TopLink;
import com.hcrcms.common.hibernate3.Updater;

public interface TopLinkDao {
	public List<TopLink> getList();

	public TopLink findById(Integer id);
	
	public List<TopLink> findByName(String name);
	
	public TopLink findByPath(String path);

	public TopLink save(TopLink bean);

	public TopLink updateByUpdater(Updater<TopLink> updater);

	public TopLink deleteById(Integer id);
}