package com.hcrcms.cms.task.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.lucene.LuceneContentSvc;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 定时创建索引(便于搜索)
 * @author jingrun.zhang
 *
 */
public class CreateLuceneIndexJob extends QuartzJobBean{

	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsSiteMng cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
			ContentMng contentMng = (ContentMng) appCtx.getBean("contentMng");
			LuceneContentSvc luceneContentSvc = (LuceneContentSvc) appCtx.getBean("luceneContentSvc");
			List<CmsSite> listSite = cmsSiteMng.getList();
			CmsSite cmsSite = listSite.get(0);
			int maxId = contentMng.findMaxArticleNum();
			Integer lastId = luceneContentSvc.createIndex(cmsSite.getId(), null,
					null, null, null, maxId);
			System.out.println("auto create lucene index success!");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("CreateLuceneIndexJob is error:");
			e1.printStackTrace();
		}
	}
}
