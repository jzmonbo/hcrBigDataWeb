package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.KeywordRelation;

/**
 * 二级标题关键词相关词
 * 
 */
public interface KeywordRelationMng {
	
	public List<KeywordRelation> getList();

	public KeywordRelation findById(Integer id);
	
	public List<KeywordRelation> findByName(String name);
	
	public KeywordRelation save(KeywordRelation bean);

	public KeywordRelation update(KeywordRelation bean);

	public KeywordRelation deleteById(Integer id);

	public KeywordRelation[] deleteByIds(Integer[] ids);
}