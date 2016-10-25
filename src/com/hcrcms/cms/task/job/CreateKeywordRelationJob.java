package com.hcrcms.cms.task.job;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.main.KeywordRelation;
import com.hcrcms.cms.manager.main.KeywordRelationMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 定时创建索引(便于搜索)
 * @author jingrun.zhang
 *
 */
public class CreateKeywordRelationJob extends QuartzJobBean{

	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			KeywordRelationMng keywordRelationMng = (KeywordRelationMng) appCtx.getBean("keywordRelationMng");
			List<KeywordRelation> lists = keywordRelationMng.getList();
			synchronized (ChannelCacheUtils.keywordRelationString) {
				ChannelCacheUtils.keywordRelationMap.clear();
				if (lists != null && lists.size() > 0){
					List<String> rootKeys = new ArrayList<String>();
					List<String> cacheKeys = new ArrayList<String>();
					//第一步、找出根级关键词
					for (KeywordRelation kr : lists){
						if (kr.getParentId() == 0){
							rootKeys.add(kr.getId() + "_" + kr.getName());
						}
					}
					//第二步、找出每个根级关键词的子关键词，并把这些词集合到List放到Map中
					if (rootKeys.size() > 0){
						for (String rkr : rootKeys){
							cacheKeys = new ArrayList<String>();
							String[] rkrs = rkr.split("_");
							int parentId = Integer.parseInt(rkrs[0]);
							for (KeywordRelation krc : lists){
								if (parentId == krc.getParentId()){
									cacheKeys.add(krc.getName());
								}
							}
							if (cacheKeys.size() > 0){
								ChannelCacheUtils.keywordRelationMap.put(rkrs[1],cacheKeys);
							}
						}
					}
				}
			}
			System.out.println("create keyword relation success!");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("CreateKeywordRelationJob is error:");
			e1.printStackTrace();
		}
	}
}
