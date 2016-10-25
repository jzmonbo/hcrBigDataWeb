package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.KeywordRelation;
import com.hcrcms.common.hibernate3.Updater;

public interface KeywordRelationDao {
	public List<KeywordRelation> getList();

	public KeywordRelation findById(Integer id);
	
	public List<KeywordRelation> findByName(String name);
	
	public KeywordRelation findByPath(String path);

	public KeywordRelation save(KeywordRelation bean);

	public KeywordRelation updateByUpdater(Updater<KeywordRelation> updater);

	public KeywordRelation deleteById(Integer id);
}