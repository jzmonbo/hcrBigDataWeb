package com.hcrcms.core.web.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.cms.entity.main.Expert;

/**
 * 用于缓存频道主键ID，这个应用数据表中创建不同channelId所需要的。
 * @author jingrun.zhang
 *
 */
public class ChannelCacheUtils {

	//第三词库
	public static Set<String> sensitivityWord = new HashSet<String>();
	
	//频道缓存
	public static Map<String,Integer> channelMap = new HashMap<String,Integer>();
	
	//关于
	public static Map<Integer,String> aboutMap = new HashMap<Integer,String>();
	
	//生成静态页面模板
	public static Map<Integer,String> staticPageMap = new HashMap<Integer,String>();
	
	//文章内容划词
	public static Map<Integer,CmsKeyword> contentWordsMap = new LinkedHashMap<Integer,CmsKeyword>();
	
	//生成缩小图片尺寸
	public static Map<String,String> thumbMap = new HashMap<String,String>();
	
	//专家列表
	public static Map<String,Expert> exoertMap = new HashMap<String,Expert>();
	public static String expertString = "";
	
	//月份表
	public static Map<String,String> monthMap = new HashMap<String,String>();
	
	//百度关联词表
	public static Map<String,String> baiduRelateMap = new HashMap<String,String>();
	public static String baiduRelateString = "";
	
	//搜索二级标题关键词相关词
	public static Map<String,List> keywordRelationMap = new HashMap<String,List>();
	public static String keywordRelationString = "";
	
	//数据猿精品活动
	public static Map<String,Integer> jingPinSportMap = new HashMap<String,Integer>();
	
	static{
		channelMap.put("新闻咨讯", 70);
		channelMap.put("大数据快播", 71);
		channelMap.put("专家访谈", 72);
		channelMap.put("活动推荐", 74);
		channelMap.put("人物专访", 75);
		channelMap.put("大数据学堂", 76);
		channelMap.put("大数据企业推荐", 78);
		channelMap.put("热门职业招聘", 79);
		channelMap.put("网站介绍", 82);
		channelMap.put("专题活动",84);
		channelMap.put("指数排行",85);
		
		aboutMap.put(1,"关于数据猿");
		aboutMap.put(2,"成为专栏专家");
		aboutMap.put(3,"好文投递&寻求报道");
		aboutMap.put(4,"广告推广与活动合作");
		aboutMap.put(5,"数据支持&合作");
		
		staticPageMap.put(70,"/WEB-INF/t/cms/www/default/content/news.html");
		staticPageMap.put(72,"/WEB-INF/t/cms/www/default/content/expert.html");
		staticPageMap.put(74,"/WEB-INF/t/cms/www/default/content/recommend.html");
		staticPageMap.put(75,"/WEB-INF/t/cms/www/default/content/profile.html");
		staticPageMap.put(76,"/WEB-INF/t/cms/www/default/content/school.html");
		staticPageMap.put(78,"/WEB-INF/t/cms/www/default/content/business.html");
		staticPageMap.put(79,"/WEB-INF/t/cms/www/default/content/recruit.html");
		staticPageMap.put(82,"/WEB-INF/t/cms/www/default/content/Introduction.html");
		staticPageMap.put(84,"/WEB-INF/t/cms/www/default/topic/specialTopicIndex.html");
		staticPageMap.put(85,"/WEB-INF/t/cms/www/default/index_ranking/indexRanking.html");
		
		thumbMap.put("phone_first_list","640_259");												//手机首页LIST缩略图尺寸
		
		monthMap.put("一月","01");
		monthMap.put("二月","02");
		monthMap.put("三月","03");
		monthMap.put("四月","04");
		monthMap.put("五月","05");
		monthMap.put("六月","06");
		monthMap.put("七月","07");
		monthMap.put("八月","08");
		monthMap.put("九月","09");
		monthMap.put("十月","10");
		monthMap.put("十一月","11");
		monthMap.put("十二月","12");
		
		jingPinSportMap.put("大数据24小时",1);
		jingPinSportMap.put("大数据周周看",2);
		jingPinSportMap.put("大数据投融资",3);
		jingPinSportMap.put("大咖周语录",4);
		jingPinSportMap.put("大数据周聘汇",5);
		jingPinSportMap.put("每周一本书",6);
		jingPinSportMap.put("大数据活动公告",7);
	}
}
