package com.hcrcms.cms.task.job;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.Constants;
import com.hcrcms.cms.entity.assist.CmsSensitivity;
import com.hcrcms.cms.manager.assist.CmsSensitivityMng;
import com.hcrcms.core.manager.FtpMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 敏感词
 * @author jingrun.zhang
 *
 */
public class LoadSensitivityJob extends QuartzJobBean {

  static Logger logger = Logger.getLogger(LoadSensitivityJob.class);

  protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
	  try {
		  long startTime = System.currentTimeMillis();
		  SchedulerContext schCtx = context.getScheduler().getContext();
		  ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
		  this.sensitivityManager = (CmsSensitivityMng) appCtx.getBean("cmsSensitivityMng");
		  synchronized(Constants.SENSITIVITY_STRING){
			  ChannelCacheUtils.sensitivityWord.clear();
			  List<CmsSensitivity> lists = sensitivityManager.getList(false);
				if (lists != null && lists.size() > 0) {
					for (CmsSensitivity sensitivity : lists) {
						ChannelCacheUtils.sensitivityWord.add(sensitivity.getSearch());
					}
				}
		  }
		  long endTime = System.currentTimeMillis();
		  logger.info("SensitivityTask spend [" + (endTime-startTime) + "] miliseconds.");
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
  @Autowired
  private CmsSensitivityMng sensitivityManager;
}