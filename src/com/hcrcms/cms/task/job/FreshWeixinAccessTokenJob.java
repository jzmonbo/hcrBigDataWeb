package com.hcrcms.cms.task.job;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.common.web.session.SessionProvider;
import com.hcrcms.core.entity.WeixinAccessToken;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.HttpUtil;

/**
 * 加载关键词,用于设置文章划词
 * 思路:先遍历关键词,找到长尾词,长尾词long_tail置为0(表示没有父长尾词),短尾词long_tail设置为父长尾词列表(中间以逗号分开)
 */ 
public class FreshWeixinAccessTokenJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(FreshWeixinAccessTokenJob.class);
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			//CmsConfigMng cmsConfigMng = (CmsConfigMng) appCtx.getBean("cmsConfigMng");
			getOverdueWeixin();
			System.out.println("fresh weixin access_token successful!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    
	/**
     * 找到内存过期微信,然后刷新
     */
    public void getOverdueWeixin(){
    	List<WeixinAccessToken> overduWX = new ArrayList<WeixinAccessToken>();				//找到需要刷新的access_token
    	List<WeixinAccessToken> fOverduWX = new ArrayList<WeixinAccessToken>();				//找到超过30天的access_token
    	//第一步,找到过期的access_token
    	for (String wx : Constant.weixinMap.keySet()){
    		WeixinAccessToken wt = Constant.weixinMap.get(wx);
    		if (wt.getIsOverDueAccessDate() < 0){
    			overduWX.add(wt);
    		}
    		if (wt.getIsFinalOverDueAccessDate() < 0){
    			fOverduWX.add(wt);
    		}
    	}
    	//刷新过期的access_token
    	if (overduWX.size() > 0){
    		for (WeixinAccessToken wt : overduWX){
    			String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+Constant.WEIXIN_APPID+"&grant_type=refresh_token&refresh_token="+wt.getRefresh_token();
    			String result = "";
    			try {
					result = HttpUtil.doHPPost(url,null);
				} catch (Exception e) {
					System.out.println("send weixin fresh access_token is error : ");
					e.printStackTrace();
					//如果获取异常了，再获取10次，如果还异常就返回出错了
					boolean flag = false;
					for (int i=0;i<10;i++){
						try {
							if (flag) break;
							System.out.println("no."+(i+1)+" times get access_token......");
							result = HttpUtil.doHPPost(url,null);
							flag = true;
						} catch (Exception e1) {
							System.out.println("no."+(i+1)+" times is error:");
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException e2) {}
						}
					}
				}
    			System.out.println("weixin fresh access_token [" + wt.getAccess_token() + "] , result [" + result + "]");
    		}
    	}
    	//从session中删除过期的微信
    	if (fOverduWX.size() > 0){
    		synchronized (Constant.weixinMapString){
				for (WeixinAccessToken wt : fOverduWX){
					Constant.weixinMap.remove(wt.getAccess_token());
					session.logout(null,null);
				}
			}
    	}
    }
	
    public static void main(String[] args) {  
        
    }  
    
    @Autowired
	private SessionProvider session;
}
