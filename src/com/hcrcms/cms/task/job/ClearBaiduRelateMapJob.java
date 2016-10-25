package com.hcrcms.cms.task.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.core.web.util.ChannelCacheUtils;

/**
 * 定时创建索引(便于搜索)
 * @author jingrun.zhang
 *
 */
public class ClearBaiduRelateMapJob extends QuartzJobBean{

	public static long baseCacheTime = 2592000000L;					//缓存时间为1个月
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			synchronized (ChannelCacheUtils.baiduRelateString) {
				if (!ChannelCacheUtils.baiduRelateMap.isEmpty()){
					List<String> rLists = new ArrayList<String>();
					Date date = new Date();
					for (String key : ChannelCacheUtils.baiduRelateMap.keySet()){
						String val = ChannelCacheUtils.baiduRelateMap.get(key);
						long oldTime = Long.parseLong(val.split("_")[1]);
						long subTime = date.getTime() - oldTime;
						if (subTime >= baseCacheTime){
							rLists.add(key);
						}
					}
					if (rLists.size() > 0){
						for (String rl : rLists){
							ChannelCacheUtils.baiduRelateMap.remove(rl);
						}
					}
				}
			}
			System.out.println("clear baidu relate success!");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("ClearBaiduRelateMapJob is error:");
			e1.printStackTrace();
		}
	}
}
