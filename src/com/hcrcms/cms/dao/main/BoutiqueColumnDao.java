package com.hcrcms.cms.dao.main;

import java.util.List;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.entity.main.JingPinSportSet;
import com.hcrcms.common.hibernate3.Updater;

/**
 * 精品栏目DAO
 * @author jingrun.zhang
 *
 */
public interface BoutiqueColumnDao {
	public List<JingPinSportSet> getList();

	public JingPinSportSet findById(Integer id);
	
	public JingPinSportSet findByPath(String path);

	public JingPinSportSet save(JingPinSportSet bean);

	public JingPinSportSet updateByUpdater(Updater<JingPinSportSet> updater);

	public JingPinSportSet deleteById(Integer id);
	
}