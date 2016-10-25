package com.hcrcms.cms.task.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.manager.main.SiteRightLinkMng;
import com.hcrcms.core.web.util.DateUtils;

/**
 * 计算指数排行排名
 * @author jingrun.zhang
 *
 */
public class InsteadHotRightLinkJob extends QuartzJobBean{

	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			ContentMng contentMng = (ContentMng) appCtx.getBean("contentMng");
			SiteRightLinkMng siteRightMng = (SiteRightLinkMng) appCtx.getBean("siteRightLinkMng");
			String afterDate = DateUtils.convertString(new Date());
			String beforeDate = DateUtils.getBeforeHours(24);
			List<Content> contents = contentMng.findNewTenXinwen(beforeDate, afterDate);
			if (contents != null && contents.size() > 0){
				//先判断数据库中是否存在记录,如果不存在则写入,如果存在则更新
				List<SiteRightLink> links = siteRightMng.findBySort(1,10);
				if (links != null){			//更新
					List<SiteRightLink> siteRightLinks = findLinks(contents,links,1);
					for (SiteRightLink link : siteRightLinks){
						siteRightMng.update(link);
					}
				}
				//实际上只有更新的操作，没有写入操作
				/*else{												//写入
					List<SiteRightLink> siteRightLinks = findLinks(contents,null,0);
					for (SiteRightLink link : siteRightLinks){
						siteRightMng.save(link);
					}
				}*/
			}
			System.out.println("update siteRightLink finish!");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("InsteadHotRightLinkJob is error:");
			e1.printStackTrace();
		}
	}
	
	public List<SiteRightLink> findLinks(List<Content> contents,List<SiteRightLink> links,int flag){
		List<SiteRightLink> rightLinks = new ArrayList<SiteRightLink>();
		int count = 0;
		for (Content content : contents){
			if ((count+1) > 10){
				break;
			}
			SiteRightLink link = null;
			if (flag == 1){
				link = links.get(count);
				
			}else{
				link = new SiteRightLink();
			}
			String tag = content.getTagContent().get(0).split(" ")[0];
			String[] tags = tag.split("_");
			link.setText(tags[0]);
			link.setTextUs(tags[1]);
			link.setUrl("http://www.datayuan.cn/s/"+tags[1]+".htm");
			link.setTitleTag(tags[0]);
			//link.setSort(++count);
			rightLinks.add(link);
		}
		return rightLinks;
	}
}
