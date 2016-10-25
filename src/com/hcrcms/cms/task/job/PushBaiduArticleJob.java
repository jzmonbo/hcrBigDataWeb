package com.hcrcms.cms.task.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsSiteMng;

/**
 * 每分钟自动向百度推送新文章
 */ 
public class PushBaiduArticleJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(PushBaiduArticleJob.class);
	private final String BAIDUPUSH = "http://data.zz.baidu.com/urls?site=www.datayuan.cn&token=c2unwwEuYZsQ6b7P";
	private final String URL_PRE = "http://www.datayuan.cn";
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			CmsConfigMng cmsConfigMng = (CmsConfigMng) appCtx.getBean("cmsConfigMng");
			CmsSiteMng cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
			ContentMng contentMng = (ContentMng) appCtx.getBean("contentMng");
			CmsConfig cmsConfig = cmsConfigMng.get();
			List<CmsSite> listSite = cmsSiteMng.getList();
			CmsSite cmsSite = listSite.get(0);
			cmsSite.setConfig(cmsConfig);
			List<Content> contents = contentMng.getNoPushBaiduList();
			if (contents != null && contents.size() > 0){
				//向百度推送文章
				String[] articleArgs = new String[contents.size()];
				for (int i=0;i<contents.size();i++){
					Content content = contents.get(i);
					content.setSite(cmsSite);
					String url = content.getUrl();
					if (url.indexOf("http") > -1){
						articleArgs[i] = content.getUrl();
					}else{
						//articleArgs[i] = URL_PRE + content.getUrl();
						articleArgs[i] = "http://" + content.getUrl();
					}
					System.out.println("push address : [" + articleArgs[i] + "]");
				}
				
				String json = Post(BAIDUPUSH, articleArgs);//执行推送方法  
		        System.out.println(" ------ result is : "+json);  //打印推送结果 
		        String ids = pingJieIds(contents);
		        contentMng.updatePushBaiduArticle(ids);
		        System.out.println("article ids [" + ids + "] update isPushBaidu=1");
			}
			System.out.println(Thread.currentThread().getName() + "    push baidu once!");
		} catch (Exception e1) {
			System.out.println("push baidu article is error:");
			e1.printStackTrace();
		}
	}
	
	public String pingJieIds(List<Content> contents){
		StringBuilder sb = new StringBuilder();
		try {
			for (Content content : contents) {
				sb.append(content.getId()).append(",");
			}
			if (sb.length() > 0){
				sb.setLength(sb.length()-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
    /** 
     * 百度链接实时推送 
     * @param PostUrl 
     * @param Parameters 
     * @return 
     */  
    public String Post(String PostUrl,String[] Parameters){  
        if(null == PostUrl || null == Parameters || Parameters.length ==0){  
            return null;  
        }  
        String result="";  
        PrintWriter out=null;  
        BufferedReader in=null;  
        try {  
            //建立URL之间的连接  
            URLConnection conn=new URL(PostUrl).openConnection();  
            //设置通用的请求属性  
            conn.setRequestProperty("Host","data.zz.baidu.com");  
            conn.setRequestProperty("User-Agent", "curl/7.12.1");  
            conn.setRequestProperty("Content-Length", "83");  
            conn.setRequestProperty("Content-Type", "text/plain");  
               
            //发送POST请求必须设置如下两行  
            conn.setDoInput(true);  
            conn.setDoOutput(true);  
               
            //获取conn对应的输出流  
            out=new PrintWriter(conn.getOutputStream());  
            //发送请求参数  
            String param = "";  
            for(String s : Parameters){  
                param += s+"\n";  
            }  
            out.print(param.trim());  
            //进行输出流的缓冲  
            out.flush();  
            //通过BufferedReader输入流来读取Url的响应  
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line;  
            while((line=in.readLine())!= null){  
                result += line;  
            }  
               
        } catch (Exception e) {  
            System.out.println("发送post请求出现异常！"+e);  
            e.printStackTrace();  
        } finally{  
            try{  
                if(out != null){  
                    out.close();  
                }  
                if(in!= null){  
                    in.close();  
                }  
                   
            }catch(IOException ex){  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
    
    public static void main(String[] args) {  
        /*String url = "http://data.zz.baidu.com/urls?site=www.nbull.cn&token=nN8NWAEuHUtgABCh";//网站的服务器连接  
        String[] param = {  
                "http://www.nbull.cn/stock/article.do?method=article_detail_show&article_id=95"//需要推送的网址  
        };  
        String json = Post(url, param);//执行推送方法  
        System.out.println("结果是"+json);  //打印推送结果  
  		*/
    }  
}
