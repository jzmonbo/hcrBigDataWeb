package com.hcrcms.cms.task.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.Constants;
import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.cms.manager.main.ContentWordsMng;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 加载关键词,用于设置文章划词
 * 思路:先遍历关键词,找到长尾词,长尾词long_tail置为0(表示没有父长尾词),短尾词long_tail设置为父长尾词列表(中间以逗号分开)
 */ 
public class LoadKeywordJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(LoadKeywordJob.class);
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsConfigMng cmsConfigMng = (CmsConfigMng) appCtx.getBean("cmsConfigMng");
			CmsSiteMng cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
			ContentWordsMng contentWordsMng = (ContentWordsMng) appCtx.getBean("contentWordsMng");
			CmsConfig cmsConfig = cmsConfigMng.get();
			List<CmsSite> listSite = cmsSiteMng.getList();
			CmsSite cmsSite = listSite.get(0);
			cmsSite.setConfig(cmsConfig);
			List<CmsKeyword> keywords = contentWordsMng.getList();
			if (keywords != null && keywords.size() > 0){
				for (int i=0;i<keywords.size();i++){
					CmsKeyword keyword = keywords.get(i);
					keyword.setSite(cmsSite);
					boolean keyFlag = false;
					StringBuilder sb = new StringBuilder();
					//if (keyword.getLongTail() != null && keyword.getLongTail().length() > 0){
					//	sb.append(keyword.getLongTail()).append(",");
					//}
					for (int j=0;j<keywords.size();j++){
						if (j != i){
							CmsKeyword keywordj = keywords.get(j);
							if (keywordj.getName().contains(keyword.getName())){
								keyFlag = true;
								sb.append(keywordj.getId()).append(",");
							}
						}
					}
					if (keyFlag){
						if (sb.length() > 0){
							sb.setLength(sb.length()-1);
						}
						keywords.get(i).setLongTail(sb.toString());
						//下面是测试代码
						if (sb.toString().length() > 300){
							System.out.println("["+keywords.get(i).getName()+"],["+sb.toString()+"] length is : " + sb.toString().length());
						}
					}
				}
				//把文章内容划词放到内存中
				synchronized(Constants.CONTENT_WORDS){
					ChannelCacheUtils.contentWordsMap.clear();
					for (CmsKeyword keyword : keywords){
						ChannelCacheUtils.contentWordsMap.put(keyword.getId(),keyword);
					}
				}
				//把区分长尾词和短尾词更新数据库
				contentWordsMng.updateBatch(keywords);
			}
			System.out.println("load keyword successful!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    
    public static void main(String[] args) {  
        
    }  
}
