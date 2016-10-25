package com.hcrcms.cms.task.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.web.util.Constant;

/**
 * 生成每天文章xml文件
 * @author jingrun.zhang
 * 思路 : 1.先判断静态变量路径包含文件是否大于50000条，如果没有，则生成xml文件，放到该目录下。
 *       2.如果是，则按序号生成新路径，更新静态变量，生成xml文件，放到新目录下。
 */ 
public class GenerateXmlJob extends QuartzJobBean{

	private static final Logger log = LoggerFactory.getLogger(GenerateXmlJob.class);
	//以下是生成sitemap.xml文件配置
	//本地
	private static String SITEMAPPATH = "d:/sitemap.xml";//xml保存路径
	private static String ERRORFILE = "d:/errorFile.xml";	//文件转换出错日志
	//测试环境124
	//private static String SITEMAPPATH = "/DATA/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap.xml";//xml保存路径
	//private static String ERRORFILE = "/DATA/hcr/apache-tomcat-7.0.63/webapps/ROOT/errorFile.xml";	//文件转换出错日志
	//线上环境83
	//private static String SITEMAPPATH = "/APP/hcr/apache-tomcat-7.0.63/webapps/ROOT/sitemap.xml";//xml保存路径
	//private static String ERRORFILE = "/APP/hcr/apache-tomcat-7.0.63/webapps/ROOT/errorFile.xml";	//文件转换出错日志
	private static int MAXFILES = 50000;				//路径中最大文件数
	private static long FILELENG = 10000000;			//文件最大容量
	private Document document;
	
	public GenerateXmlJob(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		}
	}
	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		try {
			SchedulerContext schCtx = context.getScheduler().getContext();
			// 获取Spring中的上下文
			ApplicationContext appCtx = (ApplicationContext) schCtx.get("applicationContext");
			readConfig();
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
				}
				if (!checkFileCapacity(SITEMAPPATH,contents.size())){				//判断文件大小,文件容量大于10M,需要新建文件
					createNewFile(SITEMAPPATH);
					createAndHeadXml(SITEMAPPATH,contents);
				}else if (!checkFileCount(SITEMAPPATH,contents.size())){					//判断结点个数,如果大于50000个,需要新建文件
					createNewFile(SITEMAPPATH);
					createAndHeadXml(SITEMAPPATH,contents);
				}else{
					parseAndAddXml(SITEMAPPATH,contents);
				}
			}
			System.out.println("generate xml file Successful!");
		} catch (Exception e1) {
			System.out.println("generate xml file error:");
			e1.printStackTrace();
		}
	}
	
	/**
	 * 读取生成XML配置文件
	 */
	protected void readConfig(){
		Properties prop = new Properties();
		try {
			String userdir = getClass().getResource("/").getPath();
			userdir = userdir.substring(0,userdir.indexOf("classes"));
			//System.out.println("-------------------------  userdir [" + userdir + "]  ------------------------------------------");
			userdir += "config/config.properties";
			FileInputStream pis = new FileInputStream(new File(userdir));
			//InputStream m = getClass().getResourceAsStream(userdir);
			prop.load(pis);
			this.SITEMAPPATH = prop.getProperty("SITEMAPPATH");
			this.ERRORFILE = prop.getProperty("ERRORFILE");
			Constant.JIYINDASHUJU_TWO_RANKING_INTERFACE = prop.getProperty("JIYINDASHUJU_TWO_RANKING_INTERFACE");
			pis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建新文件
	 * @param filePath
	 * @return
	 */
	public boolean createNewFile(String filePath){
		boolean result = true;
		try {
			String[] array1 = SITEMAPPATH.split("[\\D]+");
			if (array1.length == 0) {
				SITEMAPPATH = SITEMAPPATH.substring(0,SITEMAPPATH.indexOf(".")) + "1.xml";
			} else {
				int num = Integer.parseInt(array1[array1.length - 1]);
				SITEMAPPATH = SITEMAPPATH.substring(0,SITEMAPPATH.indexOf(".")) + (num + 1) + ".xml";
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 创建带文件头xml文件
	 * @param filePath
	 */
	public void createAndHeadXml(String filePath,List<Content> contents){
		Element root = this.document.createElement("urlset");
		root.setAttribute("xmlns","http://www.sitemaps.org/schemas/sitemap/0.9");
		this.document.appendChild(root); 
		for (Content content : contents){
			Element url = this.document.createElement("url"); 
			Element loc = this.document.createElement("loc"); 
			loc.appendChild(this.document.createTextNode(content.getUrlDynamic())); 
			url.appendChild(loc); 
			Element priority = this.document.createElement("priority"); 
			priority.appendChild(this.document.createTextNode("daily")); 
			url.appendChild(priority); 
			Element changefreq = this.document.createElement("changefreq"); 
			changefreq.appendChild(this.document.createTextNode("0.8")); 
			url.appendChild(changefreq); 
			root.appendChild(url); 
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		try{
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			//System.out.println("生成XML文件成功!");
		} catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 创建xml文件
	 * @param fileName
	 */
	public void createXml(String fileName) {
		Element root = this.document.createElement("urlset");
		root.setAttribute("xmlns","http://www.sitemaps.org/schemas/sitemap/0.9");
		this.document.appendChild(root); 
		Element employee = this.document.createElement("employee"); 
		Element name = this.document.createElement("name"); 
		name.appendChild(this.document.createTextNode("wangchenyang")); 
		employee.appendChild(name); 
		Element sex = this.document.createElement("sex"); 
		sex.appendChild(this.document.createTextNode("m")); 
		employee.appendChild(sex); 
		Element age = this.document.createElement("age"); 
		age.appendChild(this.document.createTextNode("26")); 
		employee.appendChild(age); 
		root.appendChild(employee); 
		TransformerFactory tf = TransformerFactory.newInstance();
		try{
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
			//System.out.println("生成XML文件成功!");
		} catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 解析xml文件
	 * @param fileName
	 */
	public void parserXml(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(fileName);

			NodeList employees = document.getChildNodes();
			for (int i = 0; i < employees.getLength(); i++) {
				Node employee = employees.item(i);
				NodeList employeeInfo = employee.getChildNodes();
				for (int j = 0; j < employeeInfo.getLength(); j++) {
					Node node = employeeInfo.item(j);
					NodeList employeeMeta = node.getChildNodes();
					for (int k = 0; k < employeeMeta.getLength(); k++) {
						System.out.println(employeeMeta.item(k).getNodeName() + ":" + employeeMeta.item(k).getTextContent());
					}
				}
			}
			System.out.println("解析完毕");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 解析并添加文件结点
	 * @param filePath
	 */
	public void parseAndAddXml(String filePath,List<Content> contents){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document urlset = db.parse(filePath);
			for (Content content : contents){
				Element url = urlset.createElement("url"); 
				Element loc = urlset.createElement("loc"); 
				loc.appendChild(urlset.createTextNode(content.getUrlDynamic())); 
				url.appendChild(loc); 
				Element priority = urlset.createElement("priority"); 
				priority.appendChild(urlset.createTextNode("daily")); 
				url.appendChild(priority); 
				Element changefreq = urlset.createElement("changefreq"); 
				changefreq.appendChild(urlset.createTextNode("0.8")); 
				url.appendChild(changefreq); 
				urlset.getDocumentElement().appendChild(url); 
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			try{
				Transformer transformer = tf.newTransformer();
				DOMSource source = new DOMSource(urlset);
				transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
				StreamResult result = new StreamResult(pw);
				transformer.transform(source, result);
				//System.out.println("解析并添加XML文件成功!");
			} catch (TransformerConfigurationException e) {
				System.out.println(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (TransformerException e) {
				System.out.println(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检索文件是否满了
	 * @param filePath
	 * @return
	 */
	public boolean checkFile(String filePath){
		boolean result = true;
		//第一步,判断文件大小不能超过10M
		File file = new File(filePath);
		System.out.println("文件容量 : " + file.length());
		if (file.length() > FILELENG){
			result = false;
		}else{
			try {
				//第二步,判断文件个数不能超过50000个
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document document = db.parse(filePath);
				NodeList urls = document.getChildNodes();
				System.out.println("包含结点个数 : " + urls.getLength());
				if (urls.getLength() > MAXFILES){
					result = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 检查文件容量
	 * @param filePath
	 * @return
	 */
	public boolean checkFileCapacity(String filePath,int contentCount){
		boolean result = true;
		//第一步,判断文件大小不能超过10M
		File file = new File(filePath);
		System.out.println(filePath + "文件容量 : " + file.length());
		if ((file.length()+contentCount*1000) > FILELENG){
			result = false;
		}
		return result;
	}
	
	/**
	 * 检索文件结点数量
	 * @param filePath
	 * @return
	 */
	public boolean checkFileCount(String filePath,int contentCount){
		boolean result = true;
		try {
			//第二步,判断文件个数不能超过50000个
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(filePath);
			NodeList urls = document.getChildNodes();
			System.out.println(filePath + "包含结点个数 : " + urls.getLength());
			if ((urls.getLength()+contentCount) > MAXFILES){
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
