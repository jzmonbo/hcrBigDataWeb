package com.hcrcms.cms.manager.main;

import java.util.List;

import com.hcrcms.cms.entity.main.JingPinSportSet;

/**
 * 专家Manager接口
 * 
 */
public interface BoutiqueColumnMng {
	
	public List<JingPinSportSet> getList();

	public JingPinSportSet findById(Integer id);
	
	public JingPinSportSet findByPath(String path);

	public JingPinSportSet save(JingPinSportSet bean);

	public JingPinSportSet update(JingPinSportSet jingPinSportSet);

	public JingPinSportSet deleteById(Integer id);
}