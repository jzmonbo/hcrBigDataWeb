package com.hcrcms.cms.task.job;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsSiteMng;

/**
 * 生成每天文章xml文件
 * @author jingrun.zhang
 * 思路 : 1.先判断静态变量路径包含文件是否大于50000条，如果没有，则生成xml文件，放到该目录下。
 *       2.如果是，则按序号生成新路径，更新静态变量，生成xml文件，放到新目录下。
 */
public class GenerateXmlJob_bak extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(GenerateXmlJob_bak.class);
	//本地
	private static String SITEMAPPATH = "d:/sitemap/";//xml保存路径
	private static String ERRORFILE = "d:/sitemap/errorFile.xml";	//文件转换出错日志
	//测试环境124
	//private static String SITEMAPPATH = "/DATA/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap/";//xml保存路径
	//private static String ERRORFILE = "/DATA/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap/errorFile.xml";	//文件转换出错日志
	//线上环境83
	//private static String SITEMAPPATH = "/APP/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap/";//xml保存路径
	//private static String ERRORFILE = "/APP/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap/errorFile.xml";	//文件转换出错日志
	private static int MAXFILES = 50000;				//路径中最大文件数
	//GenerateXmlJob h2x=new GenerateXmlJob();
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			ChannelMng channelMng = (ChannelMng) appCtx.getBean("channelMng");
			CmsConfigMng cmsConfigMng = (CmsConfigMng) appCtx.getBean("cmsConfigMng");
			CmsSiteMng cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
			ContentMng contentMng = (ContentMng) appCtx.getBean("contentMng");
			CmsConfig cmsConfig = cmsConfigMng.get();
			List<CmsSite> listSite = cmsSiteMng.getList();
			CmsSite cmsSite = listSite.get(0);
			cmsSite.setConfig(cmsConfig);
			List<Content> contents = contentMng.getListForCurrentDay();
			if (contents != null && contents.size() > 0){
				for (Content content : contents){
					content.setSite(listSite.get(0));
					File[] lists = new File(SITEMAPPATH).listFiles();
					int filesize = lists.length;
					if (filesize > MAXFILES){
						String[] array1 = SITEMAPPATH.split("[\\D]+");
						if (array1.length == 0){
							SITEMAPPATH = SITEMAPPATH + "1";
						}else{
							int num = Integer.parseInt(array1[array1.length-1]);
							SITEMAPPATH = SITEMAPPATH + (num+1);
							File folder = new File(SITEMAPPATH);
							if (!folder.exists()){
								folder.mkdirs();
							}
							generateFile(content);
						}
					}else{
						generateFile(content);
					}
				}
			}
		} catch (Exception e1) {
			System.out.println("generate xml file error:");
			e1.printStackTrace();
		}
	}
	
	private void generateFile(Content content){
		try {
			String path=content.getUrlDynamic();
			String fromfile = SITEMAPPATH + content.getId() + ".xml";
			String outputfile=getFileName();
			boolean b=Boolean.valueOf(fromfile).booleanValue();
			
			DocumentFragment df=this.getSourceNode(path,b);
			File file=new File(fromfile);
			if(file.exists())
				file.delete();
			this.genXmlFile(df,file);
			System.out.println("generate "+file.getCanonicalPath()+" successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void genXmlFile(Node output,File file) throws Exception,Error{
		TransformerFactory tf=TransformerFactory.newInstance();
		Transformer transformer=tf.newTransformer();
		DOMSource source=new DOMSource(output);
		java.io.FileOutputStream fos=new java.io.FileOutputStream(file);
		StreamResult result=new StreamResult(fos);
		Properties props = new Properties();
		props.setProperty("encoding", "utf-8");
		props.setProperty("method", "xml");
		props.setProperty("omit-xml-declaration", "yes");
		transformer.setOutputProperties(props);
		transformer.transform(source,result);
		fos.close();
	}
	public DocumentFragment getSourceNode(String path,boolean fromfile) throws Exception,Error{
		DOMFragmentParser parser = new DOMFragmentParser();
		HTMLDocument document = new HTMLDocumentImpl();
		DocumentFragment fragment = document.createDocumentFragment();
		if(path!=null&&!path.trim().equals("")){
			String tmp=path;
				if(fromfile){
					File input = new File(path);
					FileReader fr=new FileReader(input);
					InputSource is=new InputSource(fr);
					parser.parse(is,fragment);
					fr.close();
				}else{
					URL url = new URL(tmp);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					InputStream inputs = con.getInputStream();
					InputStreamReader isr=new InputStreamReader(inputs,"utf-8");
					InputSource source=new InputSource(isr);
					parser.parse(source,fragment);
				}
				return fragment;
			}else{
				return null;
			}
	}
	public static String getFileName() throws Exception{
		Calendar c=Calendar.getInstance();
		String name="tmp"+c.get(Calendar.YEAR)+(c.get(Calendar.MONTH)<9?"0":"")+
				(c.get(Calendar.MONTH)+1)+(c.get(Calendar.DAY_OF_MONTH)<10?"0":"")+
				c.get(Calendar.DAY_OF_MONTH)+(c.get(Calendar.HOUR_OF_DAY)<10?"0":"")+
				c.get(Calendar.HOUR_OF_DAY)+(c.get(Calendar.MINUTE)<10?"0":"")+
				c.get(Calendar.MINUTE)+(c.get(Calendar.SECOND)<10?"0":"")+
				c.get(Calendar.SECOND)+(c.get(Calendar.MILLISECOND)<10?"0":"")+
				(c.get(Calendar.MILLISECOND)<100?"0":"")+c.get(Calendar.MILLISECOND);
		return name;
	}
}
