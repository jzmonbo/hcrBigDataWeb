package com.hcrcms.cms.task.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.assist.CmsSearchEnginMM;
import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.manager.assist.CmsSearchEngineMng;
import com.hcrcms.core.web.util.IndexRankingSort;

/**
 * 计算指数排行排名
 * @author jingrun.zhang
 *
 */
public class IndexRankingJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(IndexRankingJob.class);
	private final float BDSS = (float) 0.296;
	private final float BDSSXW = (float) 0.443;
	private final float SGSS = (float) 0.039;
	private final float SGSSXW = (float) 0.044;
	private final float SGWX = (float) 0.028;
	private final float HS = (float) 0.06;
	private final float HSXW = (float) 0.09;
	private final int BASENUM = 100;
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsSearchEngineMng cmsSearchEngineMng = (CmsSearchEngineMng) appCtx.getBean("cmsSearchEngineMng");
			CmsSearchEnginMM searchEnginMM = cmsSearchEngineMng.getMaxMin();
			List<CmsSearchEngine> searchList = cmsSearchEngineMng.getList();
			List<CmsSearchEngine> list101 = new ArrayList<CmsSearchEngine>();//零售商
			List<CmsSearchEngine> list102 = new ArrayList<CmsSearchEngine>();//零售支持
			List<CmsSearchEngine> list103 = new ArrayList<CmsSearchEngine>();//网上购物
			List<CmsSearchEngine> list201 = new ArrayList<CmsSearchEngine>();//医疗
			List<CmsSearchEngine> list301 = new ArrayList<CmsSearchEngine>();//通讯
			List<CmsSearchEngine> list401 = new ArrayList<CmsSearchEngine>();//征信
			List<CmsSearchEngine> list402 = new ArrayList<CmsSearchEngine>();//支付
			List<CmsSearchEngine> list403 = new ArrayList<CmsSearchEngine>();//金融服务
			List<CmsSearchEngine> list501 = new ArrayList<CmsSearchEngine>();//营销
			List<CmsSearchEngine> list601 = new ArrayList<CmsSearchEngine>();//出行
			List<CmsSearchEngine> list602 = new ArrayList<CmsSearchEngine>();//交通支持
			List<CmsSearchEngine> list701 = new ArrayList<CmsSearchEngine>();//旅行
			List<CmsSearchEngine> list801 = new ArrayList<CmsSearchEngine>();//影视
			List<CmsSearchEngine> list802 = new ArrayList<CmsSearchEngine>();//阅读
			List<CmsSearchEngine> list901 = new ArrayList<CmsSearchEngine>();//地理信息
			List<CmsSearchEngine> list1001 = new ArrayList<CmsSearchEngine>();//农业
			List<CmsSearchEngine> list1101 = new ArrayList<CmsSearchEngine>();//制造业
			List<CmsSearchEngine> list1201 = new ArrayList<CmsSearchEngine>();//政府机构
			List<CmsSearchEngine> list1202 = new ArrayList<CmsSearchEngine>();//政府服务商
			if (searchList != null && searchList.size() > 0){
				for (CmsSearchEngine se : searchList){
					if (se.getCategory() == 101){
						list101.add(se);
					}else if (se.getCategory() == 102){
						list102.add(se);
					}else if (se.getCategory() == 103){
						list103.add(se);
					}else if (se.getCategory() == 201){
						list201.add(se);
					}else if (se.getCategory() == 301){
						list301.add(se);
					}else if (se.getCategory() == 401){
						list401.add(se);
					}else if (se.getCategory() == 402){
						list402.add(se);
					}else if (se.getCategory() == 403){
						list403.add(se);
					}else if (se.getCategory() == 501){
						list501.add(se);
					}else if (se.getCategory() == 601){
						list601.add(se);
					}else if (se.getCategory() == 602){
						list602.add(se);
					}else if (se.getCategory() == 701){
						list701.add(se);
					}else if (se.getCategory() == 801){
						list801.add(se);
					}else if (se.getCategory() == 802){
						list802.add(se);
					}else if (se.getCategory() == 901){
						list901.add(se);
					}else if (se.getCategory() == 1001){
						list1001.add(se);
					}else if (se.getCategory() == 1101){
						list1101.add(se);
					}else if (se.getCategory() == 1201){
						list1201.add(se);
					}else if (se.getCategory() == 1202){
						list1202.add(se);
					}
				}
			}
			if (updateRank(list101,searchEnginMM)){
				updateDb(list101,cmsSearchEngineMng);
			}
			if (updateRank(list102,searchEnginMM)){
				updateDb(list102,cmsSearchEngineMng);
			}
			if (updateRank(list103,searchEnginMM)){
				updateDb(list103,cmsSearchEngineMng);
			}
			if (updateRank(list201,searchEnginMM)){
				updateDb(list201,cmsSearchEngineMng);
			}
			if (updateRank(list301,searchEnginMM)){
				updateDb(list301,cmsSearchEngineMng);
			}
			if (updateRank(list401,searchEnginMM)){
				updateDb(list401,cmsSearchEngineMng);
			}
			if (updateRank(list402,searchEnginMM)){
				updateDb(list402,cmsSearchEngineMng);
			}
			if (updateRank(list403,searchEnginMM)){
				updateDb(list403,cmsSearchEngineMng);
			}
			if (updateRank(list501,searchEnginMM)){
				updateDb(list501,cmsSearchEngineMng);
			}
			if (updateRank(list601,searchEnginMM)){
				updateDb(list601,cmsSearchEngineMng);
			}
			if (updateRank(list602,searchEnginMM)){
				updateDb(list602,cmsSearchEngineMng);
			}
			if (updateRank(list701,searchEnginMM)){
				updateDb(list701,cmsSearchEngineMng);
			}
			if (updateRank(list801,searchEnginMM)){
				updateDb(list801,cmsSearchEngineMng);
			}
			if (updateRank(list802,searchEnginMM)){
				updateDb(list802,cmsSearchEngineMng);
			}
			if (updateRank(list901,searchEnginMM)){
				updateDb(list901,cmsSearchEngineMng);
			}
			if (updateRank(list1001,searchEnginMM)){
				updateDb(list1001,cmsSearchEngineMng);
			}
			if (updateRank(list1101,searchEnginMM)){
				updateDb(list1101,cmsSearchEngineMng);
			}
			if (updateRank(list1201,searchEnginMM)){
				updateDb(list1201,cmsSearchEngineMng);
			}
			if (updateRank(list1202,searchEnginMM)){
				updateDb(list1202,cmsSearchEngineMng);
			}
			System.out.println("==========    calculate total finish!    ==========");
		} catch (Exception e1) {
			// TODO 尚未处理异常
			System.out.println("calculate total is error:");
			e1.printStackTrace();
		}
	}
	
	/**
	 * 更新数据库
	 */
	public void updateDb(List<CmsSearchEngine> list,CmsSearchEngineMng cmsSearchEngineMng){
		if (list != null && list.size() > 0){
			for (CmsSearchEngine se : list){
				cmsSearchEngineMng.update(se);
			}
		}
	}
	
	/**
	 * 第一步,计算积分总和
	 * 第二步,把现在排名赋给之前排名(现在排名需要重新计算)
	 * 第三步,按积分排序
	 * 算总和思路是:
	 * 百度搜索-A,百度新闻-B,搜狗搜索-C,搜狗新闻-D,搜狗微信-E,好搜-F,好搜新闻-G
	 * 标准化公式(0-1)
	 * Ax = [(X-MinA)/(MaxA-MinA)]*100
	 * Bx = [(X-MinB)/(MaxB-MinB)]*100
	 * ......
	 * 算出指数(*10000) = 29.6%*Ax + 44.3%*Bx + 3.9%*Cx + 4.4%*Dx + 2.8%*Ex + 6.0%*Fx + 9.0%*Gx
	 */
	public boolean updateRank(List<CmsSearchEngine> list,CmsSearchEnginMM searchEnginMM){
		boolean flag = false;
		int count = 0;
		if (list != null && list.size() > 0){
			int bdssMax = Integer.parseInt(searchEnginMM.getBdssMax());
			int bdssMin = Integer.parseInt(searchEnginMM.getBdssMin());
			int bdssxwMax = Integer.parseInt(searchEnginMM.getBdssxwMax());
			int bdssxwMin = Integer.parseInt(searchEnginMM.getBdssxwMin());
			int sgssMax = Integer.parseInt(searchEnginMM.getSgssMax());
			int sgssMin = Integer.parseInt(searchEnginMM.getSgssMin());
			int sgssxwMax = Integer.parseInt(searchEnginMM.getSgssxwMax());
			int sgssxwMin = Integer.parseInt(searchEnginMM.getSgssxwMin());
			int sgxwssMax = Integer.parseInt(searchEnginMM.getSgwxssMax());
			int sgwxssMin = Integer.parseInt(searchEnginMM.getSgwxssMin());
			int hsMax = Integer.parseInt(searchEnginMM.getHsMax());
			int hsMin = Integer.parseInt(searchEnginMM.getHsMin());
			int hsxwMax = Integer.parseInt(searchEnginMM.getHsxwMax());
			int hsxwMin = Integer.parseInt(searchEnginMM.getHsxwMin());
			for (CmsSearchEngine se : list){
				se.setPreviousRank(se.getNowrank());
				
				/*long bdss = Long.parseLong(se.getBdss()==null?"0":se.getBdss());
				long bdtb = Long.parseLong(se.getBdtb()==null?"0":se.getBdtb());
				long bdzsWhole = Long.parseLong(se.getBdzsWhole());
				long bdzsMobile = Long.parseLong(se.getBdzsMobile());
				long sgss = Long.parseLong(se.getSgss());
				long sgwxss = Long.parseLong(se.getSgwxss());
				long hs = Long.parseLong(se.getHs());
				long alexa = Long.parseLong(se.getAlexa());
				long wbss = Long.parseLong(se.getWbss());
				long total = bdss + bdtb + bdzsWhole + bdzsMobile + sgss + sgwxss + hs + alexa + wbss;
				se.setTotal(""+total);*/
				
				int bdss = Integer.parseInt(se.getBdss()==null?"0":se.getBdss());
				int bdssxw = Integer.parseInt(se.getBdssxw()==null?"0":se.getBdssxw());
				int sgss = Integer.parseInt(se.getSgss()==null?"0":se.getSgss());
				int sgssxw = Integer.parseInt(se.getSgssxw()==null?"0":se.getSgssxw());
				int sgwxss = Integer.parseInt(se.getSgwxss()==null?"0":se.getSgwxss());
				int hs = Integer.parseInt(se.getHs()==null?"0":se.getHs());
				int hsxw = Integer.parseInt(se.getHsxw()==null?"0":se.getHsxw());
				
				double ax = getDivide((bdss - bdssMin),(bdssMax - bdssMin))*BASENUM;
				double bx = getDivide((bdssxw - bdssxwMin),(bdssxwMax - bdssxwMin))*BASENUM;
				double cx = getDivide((sgss - sgssMin),(sgssMax - sgssMin))*BASENUM;
				double dx = getDivide((sgssxw - sgssxwMin),(sgssxwMax - sgssxwMin))*BASENUM;
				double ex = getDivide((sgwxss - sgwxssMin),(sgxwssMax - sgwxssMin))*BASENUM;
				double fx = getDivide((hs - hsMin),(hsMax - hsMin))*BASENUM;
				double gx = getDivide((hsxw - hsxwMin),(hsxwMax - hsxwMin))*BASENUM;
				double total = this.BDSS * ax + this.BDSSXW * bx + this.SGSS * cx + this.SGSSXW * dx + this.SGWX * ex + this.HS * fx + this.HSXW * gx;
				se.setTotal(""+Math.round(total*10000));
			}
			IndexRankingSort indexRankingSort = new IndexRankingSort();
			Collections.sort(list,indexRankingSort);
			Collections.reverse(list);
			for (int i=0;i<list.size();i++){
				list.get(i).setNowrank(i+1);
				if (list.get(i).getNowrank() == list.get(i).getPreviousRank()){
					count++;
				}
				//System.out.println(list.get(i).getCategory() + "   " + list.get(i).getTotal() + "     " + list.get(i).getNowrank() + "     " + list.get(i).getPreviousRank());
			}
			//System.out.println("-----------------------------------------------------------");
			//如果重新计算排名和上次排名一样(需要这个分类全都一样),如果这样就不用更新数据了,因为这种情况属于积分没有更新重复计算
			if (count != list.size()){
				flag = true;
			}
		}
		return flag;
	}
	
	public double getDivide(int a1,int a2){
		BigDecimal b1 = new BigDecimal(a1);
        BigDecimal b2 = new BigDecimal(a2);
        BigDecimal b3 = b1.divide(b2,2,BigDecimal.ROUND_HALF_EVEN);
        return b3.doubleValue();
	}
}
