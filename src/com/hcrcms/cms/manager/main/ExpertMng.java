package com.hcrcms.cms.manager.main;

import java.util.List;
import java.util.Map;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.common.page.Pagination;

/**
 * 专家Manager接口
 * 
 */
public interface ExpertMng {
	
	public List<Expert> getList();

	public Expert findById(Integer id);
	
	public List<Expert> findByName(String name);
	
	public Expert save(Expert bean);

	public Expert update(Expert bean);

	public Expert deleteById(Integer id);

	public Expert[] deleteByIds(Integer[] ids);
	
	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr, int orderBy, int option,int pageNo, int pageSize,String expertId,String author);
	
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, Integer first, Integer count,String expertId,String author);

	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr,int orderBy, int pageNo, int pageSize,String expertId,String author);
	
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count,String expertId,String author);
	
	public List<Content> getListByName(String username);
	
	public List<Content> getListByTime(String startTime,String endTime);
	
	/**
	 * 按所有专家文章时间倒序查询
	 * @return
	 */
	public List<Content> getListByDate();
}