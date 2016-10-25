package com.hcrcms.cms.task.job;

import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.staticpage.StaticPageSvc;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.web.util.DateUtils;

/**
 * 每十分钟自动生成当天十分钟之前的文章静态页面
 */ 
public class GenerateStaticPageJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(GenerateStaticPageJob.class);
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsConfigMng cmsConfigMng = (CmsConfigMng) appCtx.getBean("cmsConfigMng");
			CmsSiteMng cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
			StaticPageSvc staticPageSvc = (StaticPageSvc) appCtx.getBean("staticPageSvc");
			CmsConfig cmsConfig = cmsConfigMng.get();
			List<CmsSite> listSite = cmsSiteMng.getList();
			CmsSite cmsSite = listSite.get(0);
			cmsSite.setConfig(cmsConfig);
			Date startDate = DateUtils.getBeforeTenMinute();
			//第一步,生成首页静态文件
			staticPageSvc.index(cmsSite);
			System.out.println("generate first static page success!");
			//第二步,生成内容页静态文件
			int count = staticPageSvc.content(cmsSite.getId(),null,startDate,null);
			System.out.println("generate content static page is total : " + count + "  nums.");
		} catch (Exception e1) {
			System.out.println("generate static file error:");
			e1.printStackTrace();
		}
	}
	
}
