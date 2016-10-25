package com.hcrcms.cms.task.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.assist.CmsSearchEngineBase;
import com.hcrcms.cms.entity.assist.CmsSearchEngineLog;
import com.hcrcms.cms.manager.assist.CmsSearchEngineBaseMng;
import com.hcrcms.cms.manager.assist.CmsSearchEngineLogMng;
import com.hcrcms.cms.manager.assist.CmsSearchEngineMng;

/**
 * 比较指数新取到数是否超范围(范围上下浮动50%)
 */ 
public class CompareIndexAbnormalJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(CompareIndexAbnormalJob.class);
	private static final double UPPER_LIMIT = 1.5;		//上限
	private static final double LOWER_LIMIT = 0.5;		//下限
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsSearchEngineMng cmsSearchEngineMng = (CmsSearchEngineMng) appCtx.getBean("cmsSearchEngineMng");
			CmsSearchEngineBaseMng cmsSearchEngineBaseMng = (CmsSearchEngineBaseMng) appCtx.getBean("cmsSearchEngineBaseMng");
			CmsSearchEngineLogMng cmsSearchEngineLogMng = (CmsSearchEngineLogMng) appCtx.getBean("cmsSearchEngineLogMng");
			List<CmsSearchEngine> searchList = cmsSearchEngineMng.getListSort();
			List<CmsSearchEngineBase> searchBaseList = cmsSearchEngineBaseMng.getListSort();
			
			//比较现在获取数据和昨天数据,如果取数超过上下浮动50%,就需要用昨天数据替换现在数据,然后在LOG表中记录相应操作
			if (searchList != null && searchList.size() > 0 && searchBaseList != null && searchBaseList.size() > 0){
				if (searchList.size() == searchBaseList.size()){
					List<CmsSearchEngine> uSearchEngineList = new ArrayList<CmsSearchEngine>();				//需要更新数据列表
					List<CmsSearchEngineLog> searchEngineLogList = new ArrayList<CmsSearchEngineLog>();		//需要写入操作日志
					for (int i=0;i<searchList.size();i++){
						CmsSearchEngine cse = searchList.get(i);
						CmsSearchEngineLog seLog = new CmsSearchEngineLog();
						seLog.setCategory(cse.getCategory());
						seLog.setCompany(cse.getCompany());
						seLog.setCompanyKeyword(cse.getCompanyKeyword());
						seLog.setCreatetime(new Timestamp(System.currentTimeMillis()));
						double bdssUpper = Integer.parseInt(searchBaseList.get(i).getBdss()) * UPPER_LIMIT;
						double bdssLower = Integer.parseInt(searchBaseList.get(i).getBdss()) * LOWER_LIMIT;
						double sgssUpper = Integer.parseInt(searchBaseList.get(i).getSgss()) * UPPER_LIMIT;
						double sgssLower = Integer.parseInt(searchBaseList.get(i).getSgss()) * LOWER_LIMIT;
						double hsUpper = Integer.parseInt(searchBaseList.get(i).getHs()) * UPPER_LIMIT;
						double hsLower = Integer.parseInt(searchBaseList.get(i).getHs()) * LOWER_LIMIT;
						boolean seFlag = false;																//更新标记
						if (Integer.parseInt(cse.getBdss()) >= bdssUpper || Integer.parseInt(cse.getBdss()) <= bdssLower){
							seFlag = true;
							seLog.setBdssAbnormal(cse.getBdss());
							seLog.setBdssYestoday(searchBaseList.get(i).getBdss());
							cse.setBdss(searchBaseList.get(i).getBdss());
							
						}
						if (Integer.parseInt(cse.getSgss()) >= sgssUpper || Integer.parseInt(cse.getSgss()) <= sgssLower){
							seFlag = true;
							seLog.setSgssAbnormal(cse.getSgss());
							seLog.setSgssYestoday(searchBaseList.get(i).getSgss());
							cse.setSgss(searchBaseList.get(i).getSgss());
						}
						if (Integer.parseInt(cse.getHs()) >= hsUpper || Integer.parseInt(cse.getHs()) <= hsLower){
							seFlag = true;
							seLog.setHsAbnormal(cse.getHs());
							seLog.setHsYestoday(searchBaseList.get(i).getHs());
							cse.setHs(searchBaseList.get(i).getHs());
						}
						//System.out.println("bdss["+cse.getBdss()+"],bdssUpper["+bdssUpper+"],bdssLower["+bdssLower+"],flag["+seFlag+"]");
						if (seFlag){
							uSearchEngineList.add(cse);
							searchEngineLogList.add(seLog);
						}
					}
					if (uSearchEngineList.size() > 0){
						cmsSearchEngineMng.updateBatch(uSearchEngineList);
						cmsSearchEngineLogMng.saveBatch(searchEngineLogList);
					}
				}
			}
			System.out.println("Compare now DATA and Yestoday DATA has finished!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    
    public static void main(String[] args) {  
        
    }  
}
