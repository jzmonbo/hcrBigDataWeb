package com.hcrcms.cms.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.hcrcms.cms.manager.assist.CmsTaskMng;
import com.hcrcms.cms.task.job.ClearBaiduRelateMapJob;
import com.hcrcms.cms.task.job.CompareIndexAbnormalJob;
import com.hcrcms.cms.task.job.CreateKeywordRelationJob;
import com.hcrcms.cms.task.job.CreateLuceneIndexJob;
import com.hcrcms.cms.task.job.FreshWeixinAccessTokenJob;
import com.hcrcms.cms.task.job.GenerateStaticPageJob;
import com.hcrcms.cms.task.job.GenerateXmlJob;
import com.hcrcms.cms.task.job.IndexRankingJob;
import com.hcrcms.cms.task.job.InsteadHotRightLinkJob;
import com.hcrcms.cms.task.job.LoadExpertsJob;
import com.hcrcms.cms.task.job.LoadKeywordJob;
import com.hcrcms.cms.task.job.LoadSensitivityJob;
import com.hcrcms.cms.task.job.PushBaiduArticleJob;
import com.hcrcms.core.web.util.Constant;

/**
 * @author Zhang
 */
public class LoadTask{
	/**
	 * 系统初始加载任务
	 */
	public void loadTask(){
		calculateIndexRanking();
		generateStaticPage();
		//generatexml();
		//pushBaiduArticle();
		loadKeyword();
		loadSensitivity();
		compareIndexAbnormal();
		initRedisInstance();
		insteadHotRightLink();
		loadExperts();
		//freshWeixinAccessToken();
		createLuceneIndex();
		clearBaiduRelateMap();
		createKeywordRelation();
	}
	
	/**
	 * 计算指数
	 */
	public void calculateIndexRanking(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("calculateIndexRanking");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(IndexRankingJob.class);
			CronTrigger cronTrigger = new CronTrigger("cron_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/30 * * * ?");
			//cronTrigger.setCronExpression("0/5 * * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成静态页面
	 */
	public void generateStaticPage(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("generateStaticPageTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(GenerateStaticPageJob.class);
			CronTrigger cronTrigger = new CronTrigger("page_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/10 * * * ?");
			//cronTrigger.setCronExpression("0 0/5 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成XML文件
	 */
	public void generatexml(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("generateXmlTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(GenerateXmlJob.class);
			CronTrigger cronTrigger = new CronTrigger("xml_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 30 23 * * ?");
			//cronTrigger.setCronExpression("0 0/5 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 文章推送到百度
	 */
	public void pushBaiduArticle(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("pushBaiduArticleTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(PushBaiduArticleJob.class);
			CronTrigger cronTrigger = new CronTrigger("baidu_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/1 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载划词的关键词
	 */
	public void loadKeyword(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("loadKeywordTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(LoadKeywordJob.class);
			CronTrigger cronTrigger = new CronTrigger("loadkeyword_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/10 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步敏感词汇
	 */
	public void loadSensitivity(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("loadSensitivityTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(LoadSensitivityJob.class);
			CronTrigger cronTrigger = new CronTrigger("loadSensitivity_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0 0/2 * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 比较指数新取到数是否超范围(范围上下浮动50%)
	 */
	public void compareIndexAbnormal(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("compareIndexAbnormalTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(CompareIndexAbnormalJob.class);
			CronTrigger cronTrigger = new CronTrigger("compareIndexAbnormal_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/30 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param params 任务参数
	 * @return
	 */
	private JobDataMap getJobDataMap(Map<String,String> params){
		JobDataMap jdm=new JobDataMap();
		Set<String>keySet=params.keySet();
		Iterator<String>it=keySet.iterator();
		while(it.hasNext()){
			String key=it.next();
			jdm.put(key, params.get(key));
		}
		return jdm;
	}
	
	/**
	 * 初使化Redis实例
	 */
	private void initRedisInstance(){
		readRedisConfig();
		initRedisPool();
	}
	
	/**
	 * 读取Redis配置文件
	 */
	private void readRedisConfig(){
		Properties prop = new Properties();
		try {
			String userdir = getClass().getResource("/").getPath();
			userdir = userdir.substring(0,userdir.indexOf("classes"));
			userdir += "config/redis.properties";
			FileInputStream pis = new FileInputStream(new File(userdir));
			prop.load(pis);
			Constant.ADDR = prop.getProperty("ADDR");
			Constant.PORT = Integer.parseInt(prop.getProperty("PORT"));
			Constant.AUTH = prop.getProperty("AUTH");
			Constant.MAX_ACTIVE = Integer.parseInt(prop.getProperty("MAX_ACTIVE"));
			Constant.MAX_IDLE = Integer.parseInt(prop.getProperty("MAX_IDLE"));
			Constant.MAX_WAIT = Integer.parseInt(prop.getProperty("MAX_WAIT"));
			Constant.TIMEOUT = Integer.parseInt(prop.getProperty("TIMEOUT"));
			Constant.TEST_ON_BORROW = Boolean.parseBoolean(prop.getProperty("TEST_ON_BORROW"));
			pis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redis初使化
	 */
	private void initRedisPool(){
		try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(Constant.MAX_ACTIVE);
            config.setMaxIdle(Constant.MAX_IDLE);
            config.setMaxWait(Constant.MAX_WAIT);
            config.setTestOnBorrow(Constant.TEST_ON_BORROW);
            Constant.jedisPool = new JedisPool(config, Constant.ADDR, Constant.PORT, Constant.TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 刷新微信access_token
	 */
	public void freshWeixinAccessToken(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("freshWeixinAccessTokenTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(FreshWeixinAccessTokenJob.class);
			CronTrigger cronTrigger = new CronTrigger("freshWeixinAccessToken_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/10 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 替换昨天最新10篇文章首个关键词到"大家都在搜"和"热点导航"(文章搜索时间范围:昨天下午3点到今天下午3点)
	 */
	public void insteadHotRightLink(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("insteadHotRightLinkTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(InsteadHotRightLinkJob.class);
			CronTrigger cronTrigger = new CronTrigger("insteadHotRightLink_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0 15 * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 定时更新专家列表信息
	 */
	public void loadExperts(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("loadExpertsTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(LoadExpertsJob.class);
			CronTrigger cronTrigger = new CronTrigger("loadExperts_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/10 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 定时创建索引(便于搜索)
	 */
	public void createLuceneIndex(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("createLuceneIndexTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(CreateLuceneIndexJob.class);
			CronTrigger cronTrigger = new CronTrigger("createLuceneIndex_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0/23 * * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 定时创建索引(便于搜索)
	 */
	public void clearBaiduRelateMap(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("clearBaiduRelateMapTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(ClearBaiduRelateMapJob.class);
			CronTrigger cronTrigger = new CronTrigger("clearBaiduRelateMap_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0 13 * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建二级标题搜索关键词相关词搜索缓存
	 */
	public void createKeywordRelation(){
		try {
			JobDetail jobDetail = new JobDetail();
			//设置任务名称
			jobDetail.setName("createKeywordRelationTask");
			jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
			//设置任务执行类
			jobDetail.setJobClass(CreateKeywordRelationJob.class);
			CronTrigger cronTrigger = new CronTrigger("createKeywordRelationMap_",Scheduler.DEFAULT_GROUP, jobDetail.getName(),Scheduler.DEFAULT_GROUP);
			//设置任务规则
			cronTrigger.setCronExpression("0 0 0/6 * * ?");
			//调度任务
			scheduler.scheduleJob(jobDetail, cronTrigger); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param taskClassName 任务执行类名
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private Class getClassByTask(String taskClassName) throws ClassNotFoundException{
		return Class.forName(taskClassName);
	}
	@Autowired
	private CmsTaskMng taskMng;
	@Autowired
	private Scheduler scheduler;
}
