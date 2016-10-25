package com.hcrcms.cms.task.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 定时更新专家列表信息
 * @author jingrun.zhang
 *
 */
public class LoadExpertsJob extends QuartzJobBean{

	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			ExpertMng expertMng = (ExpertMng) appCtx.getBean("expertMng");
			List<Expert> experts = expertMng.getList();
			if (experts != null && experts.size() > 0){
				synchronized (ChannelCacheUtils.expertString) {
					for (Expert expert : experts){
						ChannelCacheUtils.exoertMap.put(expert.getAuthor(),expert);
					}
				}
			}
			System.out.println("update experts finish!");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("LoadExpertsJob is error:");
			e1.printStackTrace();
		}
	}
}
