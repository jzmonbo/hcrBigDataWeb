package com.hcrcms.cms.action.front;

import static com.hcrcms.common.web.Constants.INDEX;
import static com.hcrcms.common.web.Constants.MOBILE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.assist.CmsSearchWords;
import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.entity.assist.JiYinDaShuJu;
import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.assist.CmsKeywordMng;
import com.hcrcms.cms.manager.assist.CmsSearchEngineMng;
import com.hcrcms.cms.manager.assist.CmsSearchWordsMng;
import com.hcrcms.cms.manager.assist.CmsTopicEnterpriseMng;
import com.hcrcms.cms.manager.main.ArticleTopLinkMng;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.CmsModelItemMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.common.page.Paginable;
import com.hcrcms.common.page.SimplePage;
import com.hcrcms.common.web.RequestUtils;
import com.hcrcms.common.web.springmvc.RealPathResolver;
import com.hcrcms.core.entity.CmsConfig;
import com.hcrcms.core.entity.CmsGroup;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.DateUtils;
import com.hcrcms.core.web.util.FrontUtils;
import com.hcrcms.core.web.util.HttpRequestDeviceUtils;
import com.hcrcms.core.web.util.HttpsUtil;
import com.hcrcms.core.web.util.MobileUtils;
import com.hcrcms.core.web.util.PingyinUtil;
import com.hcrcms.core.web.util.TopicSort;
import com.hcrcms.core.web.util.URLHelper;
import com.hcrcms.core.web.util.URLHelper.PageInfo;
import com.hcrcms.core.web.util.VideoUtils;

@Controller
public class DynamicPageAct {
	private static final Logger log = LoggerFactory
			.getLogger(DynamicPageAct.class);
	/**
	 * 首页模板名称
	 */
	public static final String TPL_INDEX = "tpl.index";
	public static final String GROUP_FORBIDDEN = "login.groupAccessForbidden";
	public static final String CONTENT_STATUS_FORBIDDEN ="content.notChecked";
	
	/**
	 * 列表模板
	 */
	public static final String ABOUT_TEMPLATE = "WEB-INF/t/cms/www/default/content/contact.html";
	public static final String XUETANG_TEMPLATE = "WEB-INF/t/cms/www/default/content/xuetang.html";
	public static final String XUETANG_MORE_TEMPLATE = "WEB-INF/t/cms/www/default/content/xuetangMore.html";
	public static final String TUIJIAN_TEMPLATE = "WEB-INF/t/cms/www/default/content/tuijian.html";
	public static final String EXPERT_TEMPLATE = "WEB-INF/t/cms/www/default/content/expert.html";
	public static final String EXPERTLIST_TEMPLATE = "WEB-INF/t/cms/www/default/content/expertList.html";
	public static final String JOBPOSITION_TEMPLATE = "WEB-INF/t/cms/www/default/content/jobPosition.html";
	public static final String AUTHORSEARCH_TEMPLATE = "WEB-INF/t/cms/www/default/content/authorSearch.html";
	public static final String TIME_TEMPLATE = "WEB-INF/t/cms/www/default/content/timeSearch.html";
	public static final String SPECIAL_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/specialTopicIndex.html";
	public static final String SHANGHAIFENGHUI_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/shanghaifenghui.html";
	public static final String ZGXINXITONGXIN_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/zgxinxitongxin.html";
	public static final String QUANYUDSJ_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/quanyudsj.html";
	public static final String HULIANWANGDAHUI_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/hulianwangdahui.html";
	public static final String YUANLIATAN_ALIBABA_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/zoujinalibaba.html";
	public static final String JIYINDASHUJU_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/jiyindashuju.html";
	public static final String MOFANGDASHUJUYUCE_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/mofangdashujuyuce.html";
	public static final String JIYINDASHUJU_CHILD1_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/jiyindashuju_child1.html";
	public static final String JIYINDASHUJU_CHILD2_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/jiyindashuju_child2.html";
	public static final String JIYINDASHUJU_CHILD3_TOPIC_TEMPLATE = "WEB-INF/t/cms/www/default/topic/jiyindashuju_child3.html";
	public static final String SEARCH_INDEX_TEMPLATE = "WEB-INF/t/cms/www/default/index_ranking/indexRanking.html";
	public static final String GUNDONG_TEMPLATE = "WEB-INF/t/cms/www/default/gundong/list.html";
	public static final String DASHUJUKUAIBO = "WEB-INF/t/cms/www/default/content/dashujukuaibo.html";
	public static final String RENWUZHUANFANG = "WEB-INF/t/cms/www/default/content/renwuzhufang.html";
	public static final String HUODONGTUIJIAN = "WEB-INF/t/cms/www/default/content/huodongtuijian.html";
	public static final String HUODONGTUIJIAN_MOBILE = "WEB-INF/m/t/cms/www/default/content/huodongtuijian.html";
	public static final String JINGPINHUODONG = "WEB-INF/t/cms/www/default/content/jingpinhuodong.html";
	public static final String JINGPINHUODONGDETAIL = "WEB-INF/t/cms/www/default/content/jingpinhuodongDetail.html";
	public static final String JINGPINHUODONGDETAIL_MOBILE = "WEB-INF/m/t/cms/www/default/content/jingpinhuodongDetail.html";
	public static final String SEARCH_ENGIN_PAGE = "WEB-INF/t/cms/www/default/content/search_engines.html";
	public static final String ZHIBO_TEMPLATE = "WEB-INF/t/cms/www/default/content/zhibo.html";
	public static final String REMENZHIYE = "WEB-INF/t/cms/www/default/content/remenzhiye.html";
	public static final String EXPERT_MORE = "WEB-INF/t/cms/www/default/content/expertMore.html";
	
	/**
	 * 手机列表模板
	 */
	public static final String SEARCH_MOBILE_INDEX_TEMPLATE = "WEB-INF/m/t/cms/www/default/index_ranking/indexRanking.html";

	/**
	 * TOMCAT的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//带有其他路径则是非法请求
		String uri=URLHelper.getURI(request);
		if(StringUtils.isNotBlank(uri)&&!uri.equals("/")){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//判断访问来源(是PC端或移动端)
		if (HttpRequestDeviceUtils.isMobileDevice(request)){
			if(site.getStaticIndex()&&new File(realPathResolver.get(MOBILE+site.getStaticDir()+INDEX)).exists()){
				return MOBILE+FrontUtils.getTplPath("", site.getStaticDir(), INDEX);
			}else{
				return site.getMobileTplIndexOrDef();
			}
		}
		//使用静态首页而且静态首页存在
		if(site.getStaticIndex()&&new File(realPathResolver.get(site.getStaticDir()+INDEX)).exists()){
			return FrontUtils.getTplPath("", site.getStaticDir(), INDEX);
		}else{
			return site.getTplIndexOrDef();
		}
	}

	public String aboutIndex(HttpServletRequest request,HttpServletResponse response, ModelMap model,Content content) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//带有其他路径则是非法请求
		String uri=ABOUT_TEMPLATE;
		if(StringUtils.isBlank(uri)){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//使用静态首页而且静态首页存在
		if(site.getStaticIndex()&&new File(realPathResolver.get(site.getStaticDir()+INDEX)).exists()){
			return FrontUtils.getTplPath("", site.getStaticDir(), INDEX);
		}else{
			return uri;
		}
	}
	
	public String listIndex(HttpServletRequest request,HttpServletResponse response, ModelMap model,String templatePath) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//带有其他路径则是非法请求
		String uri=templatePath;
		if(StringUtils.isBlank(uri)){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//使用静态首页而且静态首页存在
		if(site.getStaticIndex()&&new File(realPathResolver.get(site.getStaticDir()+INDEX)).exists()){
			return FrontUtils.getTplPath("", site.getStaticDir(), INDEX);
		}else{
			return uri;
		}
	}
	
	/**
	 * WEBLOGIC的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index.jhtml", method = RequestMethod.GET)
	public String indexForWeblogic(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return index(request,response, model);
	}
	
	/**
	 * 关于数据猿入口
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String aboutVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int param = 1;
		List<Content> contents = contentMng.getListForAbout(ChannelCacheUtils.aboutMap.get(param));
		model.put("content",contents.get(0));
		model.put("local",param);
		return aboutIndex(request,response, model,contents.get(0));
	}
	
	/**
	 * 成为专栏专家入口
	 */
	@RequestMapping(value = "/scexpert", method = RequestMethod.GET)
	public String scexpertVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int param = 2;
		List<Content> contents = contentMng.getListForAbout(ChannelCacheUtils.aboutMap.get(param));
		model.put("content",contents.get(0));
		model.put("local",param);
		return aboutIndex(request,response, model,contents.get(0));
	}
	
	/**
	 *  好文投递&寻求报道入口
	 */
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String reportVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int param = 3;
		List<Content> contents = contentMng.getListForAbout(ChannelCacheUtils.aboutMap.get(param));
		model.put("content",contents.get(0));
		model.put("local",param);
		return aboutIndex(request,response, model,contents.get(0));
	}
	
	/**
	 *  广告推广与活动合作入口
	 */
	@RequestMapping(value = "/extension", method = RequestMethod.GET)
	public String extensionVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int param = 4;
		List<Content> contents = contentMng.getListForAbout(ChannelCacheUtils.aboutMap.get(param));
		model.put("content",contents.get(0));
		model.put("local",param);
		return aboutIndex(request,response, model,contents.get(0));
	}
	
	/**
	 *  数据支持&合作入口
	 */
	@RequestMapping(value = "/cooperation", method = RequestMethod.GET)
	public String cooperationVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		int param = 5;
		List<Content> contents = contentMng.getListForAbout(ChannelCacheUtils.aboutMap.get(param));
		model.put("content",contents.get(0));
		model.put("local",param);
		return aboutIndex(request,response, model,contents.get(0));
	}
	
	/**
	 * 大数据快播入口
	 */
	@RequestMapping(value = "/dashujukuaibo.htm", method = RequestMethod.GET)
	public String dashujukuaiboVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		return listIndex(request,response, model,DASHUJUKUAIBO);
	}
	
	/**
	 * 大数据学堂入口
	 */
	@RequestMapping(value = "/xuetang.htm", method = RequestMethod.GET)
	public String xuetangVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Content> contents = contentMng.findXueTangArticles();
		if (contents != null && contents.size() > 0){
			Content peixunContent = null;
			Content shudanContent = null;
			Content baikeContent = null;
			List<Content> removeContents = new ArrayList<Content>();
			for (Content content : contents){
				if (content.getChannel().getId() == 76 && "培训".equals(content.getClassify())){
					if (peixunContent == null){
						peixunContent = content;
						removeContents.add(content);
					}
				}
				if (content.getChannel().getId() == 76 && "书单".equals(content.getClassify())){
					if (shudanContent == null){
						shudanContent = content;
						removeContents.add(content);
					}
				}
				if (content.getChannel().getId() == 76 && "百科".equals(content.getClassify())){
					if (baikeContent == null){
						baikeContent = content;
						removeContents.add(content);
					}
				}
				if (peixunContent != null && shudanContent != null && baikeContent != null){
					//已经取到三个最新文章,退出循环
					break;
				}
			}
			contents.removeAll(removeContents);
			model.addAttribute("peixunContent",peixunContent);
			model.addAttribute("shudanContent",shudanContent);
			model.addAttribute("baikeContent",baikeContent);
			model.addAttribute("tag_list",contents);
			model.addAttribute("totals",contents.size());
		}
		return listIndex(request,response, model,XUETANG_TEMPLATE);
	}
	
	/**
	 * 大数据学堂入口
	 */
	@RequestMapping(value = "/xuetangMore.htm", method = RequestMethod.GET)
	public String xuetangMoreVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		try {
			ctx = URLDecoder.decode(ctx, "UTF-8");
			String param = ctx.substring(ctx.indexOf("=") + 1);
			List<Content> contents = contentMng.findXueTangByTypeArticls(param);
			model.addAttribute("tag_list",contents);
			model.addAttribute("totals",contents.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listIndex(request,response, model,XUETANG_MORE_TEMPLATE);
	}
	
	/**
	 * 人物专访入口
	 */
	@RequestMapping(value = "/renwuzhuanfang.htm", method = RequestMethod.GET)
	public String renwuzhuanfangVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Content> contents = contentMng.findRenWuZhuanFangArticles();
		if (contents != null && contents.size() > 0){
			List<Content> removeContents = new ArrayList<Content>();
			Content rContent = null;
			for (Content content : contents){
				if (content.getChannel().getId() == 84 && content.getSpecialTopicContent().getTopicType() == 1){
					rContent = content;
					removeContents.add(rContent);
				}
			}
			contents.removeAll(removeContents);
			//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
			if (contents != null && contents.size() > 0){
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents){
					if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
						m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()){
							content.setIncludeLetter(1);
						}else{
							content.setIncludeLetter(0);
						}
					}else{
						content.setIncludeLetter(0);
					}
				}
			}
			List<Content> datuList = new ArrayList<Content>();
			datuList.add(contents.get(0));
			datuList.add(contents.get(1));
			datuList.add(contents.get(2));
			datuList.add(contents.get(3));
			contents.removeAll(datuList);
			model.addAttribute("datuList",datuList);
			model.addAttribute("tag_list",contents);
			model.addAttribute("totals",contents.size());
		}
		return listIndex(request,response, model,RENWUZHUANFANG);
	}
	
	/**
	 * 活动推荐入口
	 */
	@RequestMapping(value = "/huodongtuijian.htm", method = RequestMethod.GET)
	public String huodongtuijianVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		//返回数据
		List<Content> puTongSports = contentMng.findPuTongSports();
		Content daTuSport = null;
		if (puTongSports != null && puTongSports.size() > 0){
			daTuSport = puTongSports.get(0);
			//1.最新活动大图(只返回普通活动最新一篇就可以)
			model.addAttribute("daTuSport",daTuSport);
			//3.普通活动列表
			model.addAttribute("puTongSports",puTongSports);
			model.addAttribute("totals",puTongSports.size());
			model.addAttribute("maxLengthId",puTongSports.get(0).getId());
			model.addAttribute("minLengthId",puTongSports.get(puTongSports.size()-1).getId());
		}
		//2.精品活动最新三篇
		List<Content> mySports = contentMng.findMySport(3);
		model.addAttribute("mySports",mySports);
		String visitPath = "";
		//判断访问来源(是PC端或移动端)
		if (HttpRequestDeviceUtils.isMobileDevice(request)){
			if (puTongSports != null && puTongSports.size() > 30){
				List<Content> mSports = new ArrayList<Content>();
				for (int i=0;i<puTongSports.size();i++){
					mSports.add(puTongSports.get(i));
					if ((i+1) > 30){
						break;
					}
				}
				model.addAttribute("puTongSports",mSports);
			}
			visitPath = HUODONGTUIJIAN_MOBILE;
		}else{
			visitPath = HUODONGTUIJIAN;
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 精品活动入口
	 */
	@RequestMapping(value = "/jingPinHuoDong.htm", method = RequestMethod.GET)
	public String jingPinHuoDongVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Content> mySports = contentMng.findMySport(null);
		if (mySports != null && mySports.size() > 0){
			model.addAttribute("mySports",mySports);
			model.addAttribute("totals",mySports.size());
		}
		return listIndex(request,response, model,JINGPINHUODONG);
	}
	
	/**
	 * 精品活动分类入口
	 */
	@RequestMapping(value = "/jingPinHuoDongCategory.htm", method = RequestMethod.GET)
	public String jingPinHuoDongCategoryVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		String jpType = RequestUtils.getQueryParam(request, "c");
		String range = RequestUtils.getQueryParam(request, "range");
		/*String startTime = "";
		String endTime = "";*/
		String[] ranges = null;
		String st = "";
		String et = "";
		List<Content> mySports = new ArrayList<Content>();
		switch(Integer.parseInt(jpType)){
			case 1 :
				/*startTime = DateUtils.getDateFormatYMD(range,0);
				endTime = DateUtils.getDateFormatYMD(range,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 2 :
				/*ranges = range.split("\\-");
				startTime = DateUtils.getDateFormatYMD(ranges[0],0);
				et = ranges[0].substring(0,ranges[0].lastIndexOf("/")+1) + ranges[1];
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 3 :
				/*ranges = range.split("\\-");
				startTime = DateUtils.getDateFormatYMD(ranges[0],0);
				et = ranges[0].substring(0,ranges[0].lastIndexOf("/")+1) + ranges[1];
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 4 :
				/*ranges = range.split("\\-");
				startTime = DateUtils.getDateFormatYMD(ranges[0],0);
				et = ranges[0].substring(0,ranges[0].lastIndexOf("/")+1) + ranges[1];
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 5 :
				/*ranges = range.split("\\-");
				startTime = DateUtils.getDateFormatYMD(ranges[0],0);
				et = ranges[0].substring(0,ranges[0].lastIndexOf("/")+1) + ranges[1];
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 6 :
				/*ranges = range.split("\\-");
				startTime = DateUtils.getDateFormatYMD(ranges[0],0);
				et = ranges[0].substring(0,ranges[0].lastIndexOf("/")+1) + ranges[1];
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
			case 7 :
				/*ranges = range.split("\\-");
				st = ranges[0] + "/1";
				startTime = DateUtils.getDateFormatYMD(st,0);
				et = ranges[0].substring(0,ranges[0].indexOf("/")+1) + ranges[1] + "/" + getMonthsDay(Integer.parseInt(ranges[1]));
				endTime = DateUtils.getDateFormatYMD(et,1);
				mySports = contentMng.findMySportByCategory(jpType,startTime,endTime);*/
				mySports = contentMng.findMySportByCategory(jpType);
				break;
		}
		/*if (mySports != null && mySports.size() > 0){
			model.addAttribute("mySports",mySports);
			model.addAttribute("totals",mySports.size());
		}
		return listIndex(request,response, model,JINGPINHUODONG);*/
		try {
			response.sendRedirect(mySports.get(0).getUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 精品活动分类细化查询
	 */
	@RequestMapping(value = "/jingPinHuoDongCategoryDetail.htm", method = RequestMethod.GET)
	public String jingPinHuoDongCategoryDetailVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		String jpType = RequestUtils.getQueryParam(request, "c");
		String startId = RequestUtils.getQueryParam(request, "startId");
		String stepLength = RequestUtils.getQueryParam(request,"stepLength");
		List<Content> mySports = new ArrayList<Content>();
		mySports = contentMng.findMySportByCategoryDetail(jpType,startId,stepLength);
		if (mySports != null && mySports.size() > 0){
			model.addAttribute("mySports",mySports);
			model.addAttribute("totals",mySports.size());
			model.addAttribute("maxLengthId",mySports.get(0).getId());
			model.addAttribute("minLengthId",mySports.get(mySports.size()-1).getId());
		}else{
			model.addAttribute("totals",0);
			model.addAttribute("maxLengthId",0);
			model.addAttribute("minLengthId",0);
		}
		model.addAttribute("jpType",jpType);
		if (HttpRequestDeviceUtils.isMobileDevice(request)){
			return listIndex(request,response, model,JINGPINHUODONGDETAIL_MOBILE);
		}else{
			return listIndex(request,response, model,JINGPINHUODONGDETAIL);
		}
	}
	
	/**
	 * 精品活动分类细化查询分页
	 */
	@RequestMapping(value = "/jingPinHuoDongCategoryDetailPage.htm", method = RequestMethod.POST)
	public void jingPinHuoDongCategoryDetailPageVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		String jpType = RequestUtils.getQueryParam(request, "jpType");
		String startId = RequestUtils.getQueryParam(request, "startId");
		String stepLength = RequestUtils.getQueryParam(request,"stepLength");
		String divLocal = RequestUtils.getQueryParam(request,"divLocal");
		startId = startId.substring(startId.lastIndexOf("/")+1,startId.lastIndexOf("."));
		int dLocal = Integer.parseInt(divLocal.substring(divLocal.indexOf("contentdiv") + 10));
		List<Content> mySports = new ArrayList<Content>();
		String result = "";
		mySports = contentMng.findMySportByCategoryDetail(jpType,startId,stepLength);
		if (mySports != null && mySports.size() > 0) {
			//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
			String regex = ".*[a-zA-Z]+.*";
			Matcher m = null;
			for (Content content : mySports) {
				if (content.getContentExt().getShortTitle() != null
						&& !"".equals(content.getContentExt().getShortTitle())) {
					m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
					if (m.matches()) {
						content.setIncludeLetter(1);
					} else {
						content.setIncludeLetter(0);
					}
				} else {
					content.setIncludeLetter(0);
				}
			}
			result = combinationDiv(site, mySports, dLocal + 1);
		}
		response.setCharacterEncoding("utf-8");
		try {
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getMonthsDay(int month){
		String result = "";
		switch(month){
			case 1:
				result = "31";
				break;
			case 2:
				result = "28";
				break;
			case 3:
				result = "31";
				break;
			case 4:
				result = "30";
				break;
			case 5:
				result = "31";
				break;
			case 6:
				result = "30";
				break;
			case 7:
				result = "31";
				break;
			case 8:
				result = "31";
				break;
			case 9:
				result = "30";
				break;
			case 10:
				result = "31";
				break;
			case 11:
				result = "30";
				break;
			case 12:
				result = "31";
				break;
		}
		return result;
	}
	
	/**
	 * 企业推荐入口
	 */
	@RequestMapping(value = "/tuijian.htm", method = RequestMethod.GET)
	public String tuijianVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		return listIndex(request,response, model,TUIJIAN_TEMPLATE);
	}
	
	/**
	 * 热门职业入口
	 */
	@RequestMapping(value = "/remenzhiye.htm", method = RequestMethod.GET)
	public String remenzhiyeVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		return listIndex(request,response, model,REMENZHIYE);
	}
	
	/**
	 * 专家入口
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/expert/*.htm", method = RequestMethod.GET)
	public String expertJumpVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Expert> experts = null;
		String visitPath = "";
		try {
			UrlPathHelper helper = new UrlPathHelper();
			//String ctx = helper.getOriginatingQueryString(request);
			String reqUrl = helper.getRequestUri(request);
			String ctx = reqUrl.substring(reqUrl.lastIndexOf("/")+1,reqUrl.lastIndexOf("."));
			visitPath = "";
			if (ctx != null && !"".equals(ctx)) { //访问单个专家
				try {
					ctx = URLDecoder.decode(ctx, "UTF-8");
					String paramPy = ctx;
					//这里需要把拼音转换为汉字
					Jedis jedis = null;
					String param = "";
					try {
						jedis = Constant.getJedis();
						param = jedis.get(paramPy);
					}catch (Exception e) {
						e.printStackTrace();
					}finally{
						Constant.returnResource(jedis);
					}
					param = URLDecoder.decode(param,"UTF-8");
					experts = expertMng.findByName(param);
					model.put("expert", experts.get(0));
					List<Content> contents = expertMng.getListByName(param);
					//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
					if (contents != null && contents.size() > 0) {
						String regex = ".*[a-zA-Z]+.*";
						Matcher m = null;
						for (Content content : contents) {
							if (content.getContentExt().getShortTitle() != null
									&& !"".equals(content.getContentExt()
											.getShortTitle())) {
								m = Pattern.compile(regex)
										.matcher(
												content.getContentExt()
														.getShortTitle());
								if (m.matches()) {
									content.setIncludeLetter(1);
								} else {
									content.setIncludeLetter(0);
								}
							} else {
								content.setIncludeLetter(0);
							}
						}
					}
					model.put("tag_list", contents);
					model.put("totals", contents == null ? 0 : contents.size());
					String getRelates = HttpsUtil.relatedWords(param);
					model.addAttribute("stitle",getRelates.replaceAll(",","_"));
					model.addAttribute("sname",getRelates);
					visitPath = EXPERT_TEMPLATE;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { //访问全布专家
				experts = expertMng.getList();
				List<Content> contents = expertMng.getListByDate();
				model.put("experts",experts);
				model.put("tag_list", contents);
				model.put("totals", contents == null ? 0 : contents.size());
				visitPath = EXPERTLIST_TEMPLATE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("experts",experts);
		return listIndex(request,response, model,visitPath);
	}
	
	@RequestMapping(value = "/expert.htm", method = RequestMethod.GET)
	public String expertsVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Expert> experts = null;
		String visitPath = "";
		try {
			UrlPathHelper helper = new UrlPathHelper();
			String ctx = helper.getOriginatingQueryString(request);
			String reqUrl = helper.getRequestUri(request);
			visitPath = "";
			if (ctx != null && !"".equals(ctx)) { //访问单个专家
				try {
					ctx = URLDecoder.decode(ctx, "UTF-8");
					String param = ctx.substring(ctx.indexOf("=") + 1);
					//这里把专家名转为拼音,作地址重定向用
					String paramPy = PingyinUtil.getPingYin(param);
					reqUrl = reqUrl.substring(0,reqUrl.indexOf("/expert")+7) + "/" +  paramPy + ".htm";
					response.sendRedirect(reqUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { //访问全布专家
				experts = expertMng.getList();
				List<Content> contents = expertMng.getListByDate();
				model.put("experts",experts);
				model.put("tag_list", contents);
				model.put("totals", contents == null ? 0 : contents.size());
				visitPath = EXPERTLIST_TEMPLATE;
				model.put("experts",experts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 专家更多入口
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/expertMore.htm", method = RequestMethod.GET)
	public String expertMoreVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		List<Expert> experts = expertMng.getList();
		model.put("experts",experts);
		return listIndex(request,response,model,EXPERT_MORE);
	}
	
	/**
	 * 按作者名字搜索文章列表
	 */
	@RequestMapping(value = "/author/*.htm", method = RequestMethod.GET)
	public String authorSearch(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		//String ctx = helper.getOriginatingQueryString(request);
		String ctx = helper.getRequestUri(request);
		String visitPath = "";
		try {
			ctx = URLDecoder.decode(ctx,"UTF-8");
			//String param = ctx.substring(ctx.indexOf("=")+1);
			String paramPy = ctx.substring(ctx.lastIndexOf("/")+1,ctx.lastIndexOf("."));
			Jedis jedis = null;
			String param = "";
			try{
				jedis = Constant.getJedis();
				param = jedis.get(paramPy);
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			List<Content> contents = expertMng.getListByName(param);
			//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
			if (contents != null && contents.size() > 0){
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents){
					if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
						m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()){
							content.setIncludeLetter(1);
						}else{
							content.setIncludeLetter(0);
						}
					}else{
						content.setIncludeLetter(0);
					}
				}
			}
			model.put("tag_list",contents);
			model.put("totals",contents==null?0:contents.size());
			model.put("searhcAuthor", param);
			String getRelates = HttpsUtil.relatedWords(param);
			model.addAttribute("stitle",getRelates.replaceAll(",","_"));
			model.addAttribute("sname",getRelates);
			visitPath = AUTHORSEARCH_TEMPLATE;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return listIndex(request,response, model,visitPath);
	}
	
	@RequestMapping(value = "/author.htm", method = RequestMethod.GET)
	public String authorSearchSearch(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String reqUrl = helper.getRequestUri(request);
		String visitPath = "";
		try {
			ctx = URLDecoder.decode(ctx,"UTF-8");
			String param = ctx.substring(ctx.indexOf("=")+1);
			String paramPy = PingyinUtil.getPingYin(param);
			reqUrl = reqUrl.substring(0,reqUrl.indexOf("/author")+7) + "/" +  paramPy + ".htm";
			response.sendRedirect(reqUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 按时间搜索文章列表
	 */
	@RequestMapping(value = "/searchTime.htm", method = RequestMethod.GET)
	public String timeSearch(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String visitPath = "";
		if (ctx != null && !"".equals(ctx)){			//访问单个专家
			try {
				ctx = URLDecoder.decode(ctx,"UTF-8");
				String param = ctx.substring(ctx.indexOf("=")+1);
				if (param.length() > 10){
					param = param.substring(0,10);
				}
				String startTime = param + " 00:00:00";
				String endTime = param + " 23:59:59";
				List<Content> contents = expertMng.getListByTime(startTime,endTime);
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				if (contents != null && contents.size() > 0){
					String regex = ".*[a-zA-Z]+.*";
					Matcher m = null;
					for (Content content : contents){
						if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
							m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
							if (m.matches()){
								content.setIncludeLetter(1);
							}else{
								content.setIncludeLetter(0);
							}
						}else{
							content.setIncludeLetter(0);
						}
					}
				}
				model.put("tag_list",contents);
				model.put("totals",contents==null?0:contents.size());
				visitPath = TIME_TEMPLATE;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 职位推荐
	 */
	@RequestMapping(value = "/job.htm", method = RequestMethod.GET)
	public String jobPosition(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String visitPath = "";
		if (ctx != null && !"".equals(ctx)){
			String params = ctx.substring(ctx.indexOf("=")+1);			//内容ID
			// 内容
			Content content = contentMng.findById(Integer.parseInt(params));
			//Map<String,String> attrs = content.getAttr();
			model.addAttribute("content",content);
			/*model.addAttribute("category",attrs.get("category"));
			model.addAttribute("nature",attrs.get("nature"));
			model.addAttribute("nums",attrs.get("nums"));
			model.addAttribute("hasmanage",attrs.get("hasmanage"));
			model.addAttribute("workplace",attrs.get("workplace"));
			model.addAttribute("experience",attrs.get("experience"));
			model.addAttribute("salary",attrs.get("salary"));
			model.addAttribute("education",attrs.get("education"));
			model.addAttribute("deadline",attrs.get("deadline"));*/
			visitPath = JOBPOSITION_TEMPLATE;
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 专题
	 */
	@RequestMapping(value = "/topic.htm", method = RequestMethod.GET)
	public String specialTopicDetail(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String visitPath = "";
		try {
			if (ctx != null && !"".equals(ctx)) {
				String[] paramss = ctx.split("\\&");
				//String params = ctx.substring(ctx.indexOf("=")+1);			//内容ID
				String params = paramss[0].substring(paramss[0].indexOf("=") + 1);
				int topicId = Integer.parseInt(params);
				CmsSpecialTopic specialTopic = cmsSpecialTopicMng
						.findById(topicId);
				//取出专题列表，需要根据0.现场专访 1.内容分享来分开列表
				if (specialTopic != null) {
					List<CmsSpecialTopicContent> specialTopicContents = new ArrayList<CmsSpecialTopicContent>(
							specialTopic.getSpecialTopicContents());
					List<CmsSpecialTopicContent> topicContentInterview = new ArrayList<CmsSpecialTopicContent>();
					List<CmsSpecialTopicContent> topicContentShare = new ArrayList<CmsSpecialTopicContent>();
					List<CmsSpecialTopicContent> topicContentShare2 = new ArrayList<CmsSpecialTopicContent>();
					if (specialTopicContents != null
							&& specialTopicContents.size() > 0) {
						for (CmsSpecialTopicContent content : specialTopicContents) {
							if (content.getTopicType() == 0) { //现场专访
								if (content.getContent().getStatus() == 2) {
									topicContentInterview.add(content);
								}
							} else if (content.getTopicType() == 1) { //内容分享
								if (content.getContent().getStatus() == 2) {
									//转换会议位置: 1主会场  2子会场
									if (content.getSiteLocation() == 1) {
										topicContentShare.add(content);
									} else {
										topicContentShare2.add(content);
									}
								}
							}
						}
					}
					TopicSort topicSort = new TopicSort();
					Collections.sort(topicContentInterview, topicSort);
					Collections.sort(topicContentShare, topicSort);
					Collections.sort(topicContentShare2, topicSort);
					model.addAttribute("topicInterview", topicContentInterview);
					model.addAttribute("topicShare", topicContentShare);
					model.addAttribute("topicShareTwo", topicContentShare2);

					//准备企业专题数据
					CmsTopicEnterprise cmsTopicEnterprise = enterpriseMng
							.findByMeetId(topicId);
					if (cmsTopicEnterprise.getShareTopic() != null
							&& cmsTopicEnterprise.getShareTopic().length() > 0) {
						String[] shares = cmsTopicEnterprise.getShareTopic()
								.split(",");
						List<CmsSpecialTopicContent> eShares = new ArrayList<CmsSpecialTopicContent>();
						for (String s : shares) {
							int sh = Integer.parseInt(s);
							for (CmsSpecialTopicContent content : topicContentShare) {
								if (sh == content.getId()) {
									eShares.add(content);
									break;
								}
							}
						}
						CmsSpecialTopicContent bigshare = new CmsSpecialTopicContent();
						String[] bigShare = cmsTopicEnterprise.getLongVideo()
								.split(",");
						for (String bs : bigShare) {
							int sh = Integer.parseInt(bs);
							for (CmsSpecialTopicContent content : topicContentShare) {
								if (sh == content.getId()) {
									bigshare = content;
									break;
								}
							}
						}
						model.addAttribute("topicEshares", eShares);
						model.addAttribute("bigshareone", bigshare);
					}
					if (cmsTopicEnterprise.getInterview() != null
							&& cmsTopicEnterprise.getInterview().length() > 0) {
						String[] inters = cmsTopicEnterprise.getInterview()
								.split(",");
						List<CmsSpecialTopicContent> eInterviews = new ArrayList<CmsSpecialTopicContent>();
						for (String in : inters) {
							int inter = Integer.parseInt(in);
							for (CmsSpecialTopicContent content : topicContentInterview) {
								if (inter == content.getId()) {
									eInterviews.add(content);
									break;
								}
							}
						}
						model.addAttribute("topicEinterviews", eInterviews);
					}

					if (cmsTopicEnterprise.getShortVideo() != null
							&& cmsTopicEnterprise.getShortVideo().length() > 0) {
						VideoUtils vu = new VideoUtils();
						//转换短视频地址
						String targetUrl = vu
								.getTencentMovieSource(cmsTopicEnterprise
										.getShortVideo());
						cmsTopicEnterprise.setShortVideo(targetUrl);
					}
					/*if (cmsTopicEnterprise.getLongVideo() != null && cmsTopicEnterprise.getLongVideo().length() > 0){
						//转换长视频地址
						String targetUrl = vu.getTencentMovieSource(cmsTopicEnterprise.getLongVideo());
						cmsTopicEnterprise.setLongVideo(targetUrl);
					}*/
					model.addAttribute("cmsTopicEnterprise", cmsTopicEnterprise);
					//获取图片路径
					if (cmsTopicEnterprise.getShortVideo() != null
							&& cmsTopicEnterprise.getShortVideo().length() > 0) {
						//String realVideoPath = realPathResolver.get(cmsTopicEnterprise.getShortVideo());
						//String imagePath = realVideoPath.substring(0,realVideoPath.indexOf("video"));
						//String xdImagePath = cmsTopicEnterprise.getShortVideo().substring(0,cmsTopicEnterprise.getShortVideo().indexOf("video"));
						//xdImagePath = xdImagePath + "picture/";
						//File imageFile = new File(imagePath+"picture/");
						String local = getClass().getResource("/").getFile()
								.toString();
						String rootLocal = local.substring(0,
								local.indexOf("WEB-INF"));
						String realPicturePath = rootLocal
								+ "/yltmq/alibaba/picture/";
						File imageFile = new File(realPicturePath);
						if (imageFile.isDirectory()) {
							List<String> imagePaths = new ArrayList<String>();
							File[] files = imageFile.listFiles();
							String filepath = "";
							for (File file : files) {
								//imagePaths.add(xdImagePath + file.getName());
								filepath = "/"
										+ file.getAbsolutePath().substring(
												file.getAbsolutePath().indexOf(
														"yltmq"));
								imagePaths.add(filepath);
							}
							model.addAttribute("imagePaths", imagePaths);
							model.addAttribute("imageCount", imagePaths.size());
						}
					}
				}
				if (topicId == 1) {
					visitPath = SPECIAL_TOPIC_TEMPLATE;
				} else if (topicId == 2) {
					visitPath = SHANGHAIFENGHUI_TOPIC_TEMPLATE;
				} else if (topicId == 3) {
					visitPath = ZGXINXITONGXIN_TOPIC_TEMPLATE;
				} else if (topicId == 4) {
					visitPath = QUANYUDSJ_TOPIC_TEMPLATE;
				} else if (topicId == 5) {
					visitPath = YUANLIATAN_ALIBABA_TOPIC_TEMPLATE;
				} else if (topicId == 6) {
					//加载互联网大会图片集
					String local = getClass().getResource("/").getFile()
							.toString();
					String rootLocal = local.substring(0,
							local.indexOf("WEB-INF"));
					String realPicturePath = rootLocal
							+ "/yltmq/hulianwang/picture/";
					File imageFile = new File(realPicturePath);
					if (imageFile.isDirectory()) {
						List<String> imagePaths = new ArrayList<String>();
						File[] files = imageFile.listFiles();
						String filepath = "";
						for (File file : files) {
							filepath = "/"
									+ file.getAbsolutePath().substring(
											file.getAbsolutePath().indexOf(
													"yltmq"));
							imagePaths.add(filepath);
						}
						model.addAttribute("hulianwangPictures", imagePaths);

					}
					visitPath = HULIANWANGDAHUI_TOPIC_TEMPLATE;
				} else if (topicId == 7){
					//加载基因大数据图片集
					String local = getClass().getResource("/").getFile()
							.toString();
					String rootLocal = local.substring(0,
							local.indexOf("WEB-INF"));
					String realPicturePath = rootLocal
							+ "/yltmq/jiyindashuju/picture/";
					File imageFile = new File(realPicturePath);
					if (imageFile.isDirectory()) {
						List<String> imagePaths = new ArrayList<String>();
						File[] files = imageFile.listFiles();
						String filepath = "";
						for (File file : files) {
							filepath = "/"
									+ file.getAbsolutePath().substring(
											file.getAbsolutePath().indexOf(
													"yltmq"));
							imagePaths.add(filepath);
						}
						model.addAttribute("jiyindashujuPictures", imagePaths);
						model.addAttribute("imageCount", imagePaths.size());
					}
					//加载倒计时时间
					Date finishDate = DateUtils.getDateFormat("2016-09-27 09:00:00");
					Date nowDate = new Date();
					long daojishi = (finishDate.getTime()-nowDate.getTime())/1000;
					model.addAttribute("daojishi",daojishi);
					visitPath = JIYINDASHUJU_TOPIC_TEMPLATE;
				}else if (topicId == 8){
					visitPath = MOFANGDASHUJUYUCE_TOPIC_TEMPLATE;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 基因大数据专题(子页面)
	 */
	@RequestMapping(value = "/jiYinDSJTopic.htm", method = RequestMethod.GET)
	public String jiYinDSJTopicVisit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String[] paramss = ctx.split("\\&");
		String params = paramss[0].substring(paramss[0].indexOf("=") + 1);
		int subSport = Integer.parseInt(params);
		String visitPath = "";
		if (subSport == 1){
			visitPath = JIYINDASHUJU_CHILD1_TOPIC_TEMPLATE;
		}else if (subSport == 2){
			visitPath = JIYINDASHUJU_CHILD2_TOPIC_TEMPLATE;
			List<JiYinDaShuJu> jiYindsjs = getKeSaiData(Constant.JIYINDASHUJU_TWO_RANKING_INTERFACE);
			model.addAttribute("jiYindsjs",jiYindsjs);
		}else if (subSport == 3){
			visitPath = JIYINDASHUJU_CHILD3_TOPIC_TEMPLATE;
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 请求科赛接口数据
	 */
	public List<JiYinDaShuJu> getKeSaiData(String url){
		List<JiYinDaShuJu> jiYindsjs = new ArrayList<JiYinDaShuJu>();
		//String responseStr = HttpUtil.doHPGet(url,null);
		//String responseStr = "[{\"Team\": \"Deadshot\",\"Score\": 0.780529042992,\"Ranking\": 1,\"LastRanking\": 3,\"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\",\"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\",\"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 2,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 3,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7}]";
		String responseStr = "[{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 1, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 2,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 3,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 4,\"LastRanking\": 1,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 5,\"LastRanking\": 6,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 6,\"LastRanking\": 5,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 7,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 8,\"LastRanking\": 8,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 9,\"LastRanking\": 10,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 10,\"LastRanking\": 10,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 11, \"LastRanking\": 11, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 12,\"LastRanking\": 12,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 13,\"LastRanking\": 10,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 14,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 15,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 16,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 17,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 18,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 19,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 20,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 21, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 22,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 23,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 24,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 25,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 26,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 27,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 28,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 29,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 30,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 31, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 32,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 33,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 34,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 35,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 36,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 37,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 38,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 39,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 40,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 41, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 42,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 43,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 44,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 45,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 46,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 47,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 48,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 49,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 50,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 51, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 52,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 53,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 54,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 55,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 56,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 57,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 58,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 59,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 60,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 61, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 62,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 63,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 64,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 65,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 66,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 67,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 68,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 69,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 70,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 71, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 72,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 73,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 74,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 75,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 76,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 77,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 78,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 79,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 80,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 81, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 82,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 83,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 84,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 85,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 86,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 87,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 88,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 89,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 90,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"Deadshot\", \"Score\": 0.780529042992, \"Ranking\": 91, \"LastRanking\": 3, \"LastSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"BestSubmissionDate\": \"2016-03-31T15:39:02.904Z\", \"Entries\": 14},{\"Team\": \"涌泉\",\"Score\": 0.780442399469,\"Ranking\": 92,\"LastRanking\": 2,\"LastSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"BestSubmissionDate\": \"2016-03-30T04:40:26.892Z\",\"Entries\": 9},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 93,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 94,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 95,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 96,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 97,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 98,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 99,\"LastRanking\": 15,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7},{\"Team\": \"三湖连江数据分析团队\",\"Score\": 0.779832565034,\"Ranking\": 100,\"LastRanking\": 99,\"LastSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"BestSubmissionDate\": \"2016-03-24T14:51:50.372Z\",\"Entries\": 7}]";
		if (responseStr != null && responseStr.length() > 0){
			try {
				JSONArray jsonArray = new JSONArray(responseStr);
				for (int i = 0; i < jsonArray.length(); i++) {  
					JSONObject item = jsonArray.getJSONObject(i);
					JiYinDaShuJu jydsj = new JiYinDaShuJu();
					jydsj.setTeam(item.getString("Team"));
					jydsj.setScore(""+item.getDouble("Score"));
					jydsj.setRanking(item.getInt("Ranking"));
					jydsj.setLastRanking(item.getInt("LastRanking"));
					jydsj.setLastSubmissionDate(item.getString("LastSubmissionDate"));
					jydsj.setBestSubmissionDate(item.getString("BestSubmissionDate"));
					jydsj.setEntries(""+item.getString("Entries"));
					jiYindsjs.add(jydsj);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jiYindsjs;
	}
	
	/**
	 * 搜索指数
	 */
	@RequestMapping(value = "/indexRanking.htm", method = RequestMethod.GET)
	public String indexRanking(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		int topNum = 10;												//获取排行条数
		//从数据库中取出数据,1.指数排行大图 2.按各分类列出各类列表
		List<Content> contents = contentMng.getZhiShuPaiHangDaTu();
		List<CmsSearchEngine> searchList = cmsSearchEngineMng.getList();
		//List<CmsSearchEngine> list101 = new ArrayList<CmsSearchEngine>();//零售商
		List<CmsSearchEngine> list102 = new ArrayList<CmsSearchEngine>();//零售支持
		//List<CmsSearchEngine> list103 = new ArrayList<CmsSearchEngine>();//网上购物
		List<CmsSearchEngine> list201 = new ArrayList<CmsSearchEngine>();//医疗
		List<CmsSearchEngine> list301 = new ArrayList<CmsSearchEngine>();//通讯
		List<CmsSearchEngine> list401 = new ArrayList<CmsSearchEngine>();//征信
		//List<CmsSearchEngine> list402 = new ArrayList<CmsSearchEngine>();//支付
		List<CmsSearchEngine> list403 = new ArrayList<CmsSearchEngine>();//金融服务
		List<CmsSearchEngine> list501 = new ArrayList<CmsSearchEngine>();//营销
		//List<CmsSearchEngine> list601 = new ArrayList<CmsSearchEngine>();//出行
		List<CmsSearchEngine> list602 = new ArrayList<CmsSearchEngine>();//交通支持
		//List<CmsSearchEngine> list701 = new ArrayList<CmsSearchEngine>();//旅行
		List<CmsSearchEngine> list801 = new ArrayList<CmsSearchEngine>();//影视
		//List<CmsSearchEngine> list802 = new ArrayList<CmsSearchEngine>();//阅读
		//List<CmsSearchEngine> list901 = new ArrayList<CmsSearchEngine>();//地理信息
		List<CmsSearchEngine> list1001 = new ArrayList<CmsSearchEngine>();//农业
		//List<CmsSearchEngine> list1101 = new ArrayList<CmsSearchEngine>();//制造业
		//List<CmsSearchEngine> list1201 = new ArrayList<CmsSearchEngine>();//政府机构
		List<CmsSearchEngine> list1202 = new ArrayList<CmsSearchEngine>();//政府服务商
		if (searchList != null && searchList.size() > 0){
			for (CmsSearchEngine se : searchList){
				/*if (se.getCategory() == 101){
					if (list101.size() < topNum){
						list101.add(se);
					}
				}else*/
				if (se.getCategory() == 102){
					if (list102.size() < topNum){
						list102.add(se);
					}
				}/*else if (se.getCategory() == 103){
					if (list103.size() < topNum){
						list103.add(se);
					}
				}*/else if (se.getCategory() == 201){
					if (list201.size() < topNum){
						list201.add(se);
					}
				}else if (se.getCategory() == 301){
					if (list301.size() < topNum){
						list301.add(se);
					}
				}else if (se.getCategory() == 401){
					if (list401.size() < topNum){
						list401.add(se);
					}
				}/*else if (se.getCategory() == 402){
					if (list402.size() < topNum){
						list402.add(se);
					}
				}*/else if (se.getCategory() == 403){
					if (list403.size() < topNum){
						list403.add(se);
					}
				}else if (se.getCategory() == 501){
					if (list501.size() < topNum){
						list501.add(se);
					}
				}/*else if (se.getCategory() == 601){
					if (list601.size() < topNum){
						list601.add(se);
					}
				}*/else if (se.getCategory() == 602){
					if (list602.size() < topNum){
						list602.add(se);
					}
				}/*else if (se.getCategory() == 701){
					if (list701.size() < topNum){
						list701.add(se);
					}
				}*/else if (se.getCategory() == 801){
					if (list801.size() < topNum){
						list801.add(se);
					}
				}/*else if (se.getCategory() == 802){
					if (list802.size() < topNum){
						list802.add(se);
					}
				}else if (se.getCategory() == 901){
					if (list901.size() < topNum){
						list901.add(se);
					}
				}*/else if (se.getCategory() == 1001){
					if (list1001.size() < topNum){
						list1001.add(se);
					}
				}/*else if (se.getCategory() == 1101){
					if (list1101.size() < topNum){
						list1101.add(se);
					}
				}else if (se.getCategory() == 1201){
					if (list1201.size() < topNum){
						list1201.add(se);
					}
				}*/else if (se.getCategory() == 1202){
					if (list1202.size() < topNum){
						list1202.add(se);
					}
				}
			}
		}
		model.addAttribute("contents",contents);
		//model.addAttribute("list101",list101);
		model.addAttribute("list102",list102);
		//model.addAttribute("list103",list103);
		model.addAttribute("list201",list201);
		model.addAttribute("list301",list301);
		model.addAttribute("list401",list401);
		//model.addAttribute("list402",list402);
		model.addAttribute("list403",list403);
		model.addAttribute("list501",list501);
		//model.addAttribute("list601",list601);
		model.addAttribute("list602",list602);
		//model.addAttribute("list701",list701);
		model.addAttribute("list801",list801);
		//model.addAttribute("list802",list802);
		//model.addAttribute("list901",list901);
		model.addAttribute("list1001",list1001);
		//model.addAttribute("list1101",list1101);
		//model.addAttribute("list1201",list1201);
		model.addAttribute("list1202",list1202);
		String visitPath = "";
		//判断访问来源(是PC端或移动端)
		if (HttpRequestDeviceUtils.isMobileDevice(request)){
			visitPath = SEARCH_MOBILE_INDEX_TEMPLATE;
		}else{
			visitPath = SEARCH_INDEX_TEMPLATE;
		}
		//visitPath = SEARCH_INDEX_TEMPLATE;
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 滚动页
	 */
	@RequestMapping(value = "/gundong", method = RequestMethod.GET)
	public String gundongList(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		List<Content> contents = contentMng.getListForGunDong();
		model.addAttribute("contents",contents);
		String visitPath = GUNDONG_TEMPLATE;
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 搜索引擎页面
	 */
	@RequestMapping(value = "/search/", method = RequestMethod.GET)
	public String searchEnginePage(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		String visitPath = SEARCH_ENGIN_PAGE;
		List<CmsSearchWords> searchWords = cmsSearchWordsMng.getOrderList();
		if (searchWords != null && searchWords.size() > 0){
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				for (CmsSearchWords sw : searchWords) {
					String py = PingyinUtil.getPingYin(sw.getName());
					if (jedis.get(sw.getName()) == null) {
						jedis.set(py, sw.getName());
						//jedis.expire(py, 2592000); //缓存时间为一个月
					}
					sw.setNameUs(py);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			model.addAttribute("searchWords",searchWords);
		}
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 直播页
	 */
	@RequestMapping(value = "/zhibo", method = RequestMethod.GET)
	public String zhiboPage(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		String type = request.getParameter("type");
		String visitPath = ZHIBO_TEMPLATE;
		model.addAttribute("type",type);
		return listIndex(request,response, model,visitPath);
	}
	
	/**
	 * 获取首页更多文章
	 * 思路: 1.获取100篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadFollowUpArticle", method = RequestMethod.POST)
	public void loadFollowUpArticle(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			int maxId = 0;
			int minId = 1;
			String mid = request.getParameter("contentId");
			maxId = Integer.parseInt(mid.substring(mid.lastIndexOf("/")+1,mid.lastIndexOf(".")));
			String divLocal = request.getParameter("divLocal");
			int dLocal = Integer.parseInt(divLocal.substring(divLocal.indexOf("contentdiv") + 10));
			String category = request.getParameter("category");
			if (maxId > 0) {
				minId = maxId - 200;
				if (minId < 0) {
					minId = 1;
				}
				List<Content> contents = contentMng.getMoreArticles(maxId,minId,category);
				if (contents != null && contents.size() > 0) {
					//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
					String regex = ".*[a-zA-Z]+.*";
					Matcher m = null;
					for (Content content : contents) {
						if (content.getContentExt().getShortTitle() != null
								&& !"".equals(content.getContentExt().getShortTitle())) {
							m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
							if (m.matches()) {
								content.setIncludeLetter(1);
							} else {
								content.setIncludeLetter(0);
							}
						} else {
							content.setIncludeLetter(0);
						}
					}
					if ("article".equals(category)){							//生成文章LIST
						result = combinationDiv(site, contents, dLocal + 1);
					}else if ("kuaibo".equals(category)){						//生成快播LIST
						result = combinationKuaibo(site, contents, dLocal + 1);
					}
				}
			}
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成文章LIST
	 * @param site
	 * @param contents
	 * @param dLocal
	 * @return
	 */
	public String combinationDiv(CmsSite site,List<Content> contents,int dLocal){
		StringBuilder sb = new StringBuilder();
		String basePath = "";
		try {
			/*if (site.getStaticIndex()){
					basePath = site.getUrlStatic();
				}else{
					basePath = site.getUrlDynamic();
				}*/
			basePath = site.getUrlDynamic();
			for (Content content : contents) {
				sb.append("<div>");
				sb.append("<div id=\"contentdiv" + dLocal
						+ "\" class=\"wz-div\" style=\"display: none;\">");
				sb.append("<h2><a href=\"" + content.getUrl()
						+ "\" target=\"_blank\">");
				if (content.getIncludeLetter() == 1) {
					if (content.getShortTitle().length() < 34) {
						sb.append(content.getShortTitle());
					} else {
						sb.append(cutContent(content.getShortTitle(), 29));
					}
				} else if (content.getIncludeLetter() == 0) {
					if (content.getShortTitle().length() < 30) {
						sb.append(content.getShortTitle());
					} else {
						sb.append(cutContent(content.getShortTitle(), 30));
					}
				}
				sb.append("</a></h2>");
				sb.append("<div class=\"nametime\">");
				sb.append("<ul><li>");
				if (content.getContentExt().getTagConent() != null
						&& content.getContentExt().getTagConent().length() > 0) {
					String[] tags = content.getContentExt().getTagConent()
							.split(" ");
					int count = 0;
					for (String tag : tags) {
						String[] tagfb = tag.split("_");
						count++;
						sb.append("<a href=\"" + basePath + "/" + tagfb[1]
								+ ".htm\" target=\"_blank\">" + tagfb[0]
								+ "</a>");
						if (count > 5) {
							break;
						}
					}
				}
				sb.append("</li></ul>");

				sb.append("<p><a href=\"" + basePath + "/author.htm?author="
						+ content.getContentExt().getAuthor()
						+ "\" target=\"_blank\">"
						+ content.getContentExt().getAuthor()
						+ "</a>&nbsp;|&nbsp;<span><a href=\"" + basePath
						+ "/searchTime.htm?time="
						+ DateUtils.getDateYMD(content.getSortDate())
						+ "\" target=\"_blank\">"
						+ DateUtils.getDateYMD(content.getSortDate())
						+ "</a></span></p>");
				sb.append("</div>");
				sb.append("<div class=\"wz-div-img\">");
				if (content.getIsPictureTop() == 2) {
					sb.append("<p class=\"img-bq hd-bg\" style=\"color:#FFF;\">头条</p>");
				} else if (content.getIsPictureTop() == 1) {
					sb.append("<p class=\"img-bq hd-bg\" style=\"color:#FFF;\">推广</p>");
				}
				if (content.getContentImg() != null) {
					sb.append("<a href=\""
							+ content.getUrl()
							+ "\" target=\"_blank\"><img src=\"\" data-original=\""
							+ content.getContentImg()
							+ "\" width=\"690\" height=\"280\" alt=\""
							+ content.getShortTitle() + "\" /></a>");
				} else {
					sb.append("<a href=\""
							+ content.getUrl()
							+ "\" target=\"_blank\"><img src=\"res/common/img/theme/nopicture.png\" width=\"690\" height=\"280\" alt=\""
							+ content.getShortTitle() + "\" /></a>");
				}
				sb.append("</div>");
				sb.append("<div class=\"wz-div-text\">");
				if (content.getDescription() != null
						&& content.getDescription().length() > 0) {
					if (content.getDescription().length() < 140) {
						sb.append("<a href=\""
								+ content.getUrl()
								+ "\" target=\"_blank\"><span>"
								+ content.getDescription()
								+ "...</span><span class=\"qw-a\">《全文》</span></a>");
					} else {
						sb.append("<a href=\""
								+ content.getUrl()
								+ "\" target=\"_blank\"><span>"
								+ cutContent(content.getDescription(), 140)
								+ "...</span><span class=\"qw-a\">《全文》</span></a>");
					}
				} else {
					if (content.getTitle().length() < 140) {
						sb.append("<a href=\""
								+ content.getUrl()
								+ "\" target=\"_blank\"><span>"
								+ content.getTitle()
								+ "...</span><span class=\"qw-a\">《全文》</span></a>");
					} else {
						sb.append("<a href=\""
								+ content.getUrl()
								+ "\" target=\"_blank\"><span>"
								+ cutContent(content.getTitle(), 140)
								+ "...</span><span class=\"qw-a\">《全文》</span></a>");
					}
				}
				sb.append("</div>");
				sb.append("</div>");
				sb.append("</div>");
				dLocal++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 生成快播LIST
	 * @param site
	 * @param contents
	 * @param dLocal
	 * @return
	 */
	public String combinationKuaibo(CmsSite site,List<Content> contents,int dLocal){
		StringBuilder sb = new StringBuilder();
		String basePath = "";
		basePath = site.getUrlDynamic();
		for (Content content : contents){
			sb.append("<div id=\"contentdiv"+dLocal+"\" class=\"data-con-pkuaibo\" style=\"display: none;\">");
			sb.append("<span class=\"kuaibodata-conspankuaibo\"><span class=\"kuaibodata-conspaninner\"></span></span>");
			sb.append("<span class=\"spanionkuaibo\">");
			sb.append("<a href=\""+content.getUrl()+"\" style=\"cursor:default;\" onclick=\"return false;\">"+content.getTitle()+"</a><br>");
			sb.append("<div class=\"data-con-pspankuaibo\">").append(content.getTxt()!=null?content.getTxt():"").append("</div>");
			sb.append("<span class=\"spanionspankuaibo\">").append(DateUtils.convertMinuteString(content.getSortDate())).append("</span>");
			sb.append("</span>");
			sb.append("</div>");
			dLocal++;
		}
		return sb.toString();
	}
	
	/**
	 * 按数量取内容
	 * @return
	 */
	public String cutContent(String content,int num){
		String result = "";
		result = content.substring(0,num);
		return result;
	}
	
	/**
	 * 获取首页更多文章
	 * 思路: 1.下拉刷新获取最新N篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadMobileDownArticle", method = RequestMethod.POST)
	public void loadMobileDownArticle(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			Integer maxlengthId = Integer.parseInt(request.getParameter("maxLengthId"));
			Integer articlelength = Integer.parseInt(request.getParameter("articleLength"));
			List<Content> contents = contentMng.getMoreArticles(maxlengthId,articlelength,true);
			if (contents != null && contents.size() > 0) {
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents) {
					if (content.getContentExt().getShortTitle() != null
							&& !"".equals(content.getContentExt().getShortTitle())) {
						m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()) {
							content.setIncludeLetter(1);
						} else {
							content.setIncludeLetter(0);
						}
					} else {
						content.setIncludeLetter(0);
					}
				}
			}
			JSONObject json = new JSONObject();
			json = combinationMobileDiv(json,contents);
			json.put("articleItem",contents.size());
			if (contents.size() > 0){
				json.put("maxid",contents.get(0).getId());
			}
			result = json.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取活动更多文章（手机）
	 * 思路: 1.下拉刷新获取最新N篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadMobileDownSport", method = RequestMethod.POST)
	public void loadMobileDownSport(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			Integer maxlengthId = Integer.parseInt(request.getParameter("maxLengthId"));
			Integer articlelength = Integer.parseInt(request.getParameter("articleLength"));
			//List<Content> contents = contentMng.getMoreArticles(maxlengthId,articlelength,true);
			List<Content> contents = contentMng.findPuTongSports(maxlengthId,articlelength,"down");
			if (contents != null && contents.size() > 0) {
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents) {
					if (content.getContentExt().getShortTitle() != null
							&& !"".equals(content.getContentExt().getShortTitle())) {
						m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()) {
							content.setIncludeLetter(1);
						} else {
							content.setIncludeLetter(0);
						}
					} else {
						content.setIncludeLetter(0);
					}
				}
			}
			JSONObject json = new JSONObject();
			json = combinationMobileDiv(json,contents);
			json.put("articleItem",contents.size());
			if (contents.size() > 0){
				json.put("maxid",contents.get(0).getId());
			}
			result = json.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 拼接移动端页面显示
	 * @return
	 */
	public JSONObject combinationMobileDiv(JSONObject json,List<Content> contents){
		StringBuilder sb = new StringBuilder();
		try {
			int count = 1;
			for (Content content : contents) {
				//标题
				sb.append("<a href=\"");
				sb.append(content.getUrl());
				sb.append("\" target=\"_blank\" style=\"font-size:18px;\">");
				sb.append(content.getShortTitle());
				sb.append("</a>");
				//作者、时间、关键词
				sb.append("<div class=\"nametime\">");
				sb.append("<ul>");
				List<String> tags = content.getTagContent();
				if (tags != null && tags.size() > 0){
					int sCount = 1;
					for (String s : tags){
						if (sCount > 1){
							break;
						}
						String[] tagss = s.split("_");
						sb.append("<li>");
						sb.append("<a href=\"${base}/s/").append(tagss[1]).append(".htm\" target=\"_blank\">").append(tagss[0]).append("</a>");
						sb.append("</li>");
						sCount++;
					}
				}
				
				sb.append("</ul>");
				sb.append("<p>");
				sb.append("<a href=\"${base}/author.htm?author=").append(content.getAuthor()).append("\"  target=\"_blank\">");
				sb.append(content.getAuthor()).append("</a>&nbsp;|&nbsp;<span>").append("<a href=\"${base}/searchTime.htm?time=")
					.append(DateUtils.getDateYMD(content.getSortDate())).append("\" target=\"_blank\">")
					.append(DateUtils.getDateYMD(content.getSortDate())).append("</a></span>");
				sb.append("</p>");
				sb.append("</div>");
				sb.append("<div>");
				sb.append("<a href=\"");
				sb.append(content.getUrl());
				sb.append("\">");
				if (content.getContentMobileImg() != null) {
					sb.append("<img width=\"100%\" src=\""
							+ content.getContentMobileImg()
							+ "\" alt=\""
							+ content.getShortTitle()
							+ "\">");
				} else {
					sb.append("<img width=\"100%\" height=\"100%\" src=\"res/common/img/theme/nopicture.png\" alt=\""
							+ content.getShortTitle() + "\" />");
				}
				sb.append("</a>");
				sb.append("</div>");
				json.put("" + count, sb.toString());
				count++;
				sb.setLength(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 获取首页更多文章
	 * 思路: 1.上拉刷新获取之前N篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadMobileUpArticle", method = RequestMethod.POST)
	public void loadMobileUpArticle(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			Integer minlengthId = Integer.parseInt(request.getParameter("minLengthId"));
			Integer articlelength = Integer.parseInt(request.getParameter("articleLength"));
			List<Content> contents = contentMng.getMoreArticles(minlengthId,articlelength,false);
			if (contents != null && contents.size() > 0) {
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents) {
					if (content.getContentExt().getShortTitle() != null
							&& !"".equals(content.getContentExt().getShortTitle())) {
						m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()) {
							content.setIncludeLetter(1);
						} else {
							content.setIncludeLetter(0);
						}
					} else {
						content.setIncludeLetter(0);
					}
				}
			}
			JSONObject json = new JSONObject();
			json = combinationMobileDiv(json,contents);
			json.put("articleItem",contents.size());
			if (contents.size() > 0){
				json.put("minid",contents.get(contents.size()-1).getId());
			}
			result = json.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取活动页面更多文章（手机）
	 * 思路: 1.上拉刷新获取之前N篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadMobileUpSport", method = RequestMethod.POST)
	public void loadMobileUpSport(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			Integer minlengthId = Integer.parseInt(request.getParameter("minLengthId"));
			Integer articlelength = Integer.parseInt(request.getParameter("articleLength"));
			List<Content> contents = contentMng.findPuTongSports(minlengthId,articlelength,"up");
			//List<Content> contents = contentMng.getMoreArticles(minlengthId,articlelength,false);
			if (contents != null && contents.size() > 0) {
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents) {
					if (content.getContentExt().getShortTitle() != null
							&& !"".equals(content.getContentExt().getShortTitle())) {
						m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()) {
							content.setIncludeLetter(1);
						} else {
							content.setIncludeLetter(0);
						}
					} else {
						content.setIncludeLetter(0);
					}
				}
			}
			JSONObject json = new JSONObject();
			json = combinationMobileDiv(json,contents);
			json.put("articleItem",contents.size());
			if (contents.size() > 0){
				json.put("minid",contents.get(contents.size()-1).getId());
			}
			result = json.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取精品活动页面更多文章（手机）
	 * 思路: 1.上拉刷新获取之前N篇文章
	 * 		 2.拼接成div,然后传给页面
	 */
	@RequestMapping(value = "/loadMobileUpJPSport", method = RequestMethod.POST)
	public void loadMobileUpJPSport(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			Integer minlengthId = Integer.parseInt(request.getParameter("minLengthId"));
			Integer articlelength = Integer.parseInt(request.getParameter("articleLength"));
			String jpType = request.getParameter("jpType");
			List<Content> contents = contentMng.findPuTongJPSports(minlengthId,articlelength,"up",jpType);
			//List<Content> contents = contentMng.getMoreArticles(minlengthId,articlelength,false);
			if (contents != null && contents.size() > 0) {
				//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents) {
					if (content.getContentExt().getShortTitle() != null
							&& !"".equals(content.getContentExt().getShortTitle())) {
						m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()) {
							content.setIncludeLetter(1);
						} else {
							content.setIncludeLetter(0);
						}
					} else {
						content.setIncludeLetter(0);
					}
				}
			}
			JSONObject json = new JSONObject();
			json = combinationMobileDiv(json,contents);
			json.put("articleItem",contents.size());
			if (contents.size() > 0){
				json.put("minid",contents.get(contents.size()-1).getId());
			}
			result = json.toString();
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取更多热门职业
	 */
	@RequestMapping(value = "/loadFollowReMenZhiYe", method = RequestMethod.POST)
	public void loadFollowReMenZhiYe(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		try {
			CmsSite site = CmsUtils.getSite(request);
			String result = "";
			int maxId = 0;
			int minId = 1;
			String mid = request.getParameter("contentId");
			maxId = Integer.parseInt(mid.substring(mid.lastIndexOf("/")+1,mid.lastIndexOf(".")));
			String category = request.getParameter("category");
			if (maxId > 0) {
				minId = maxId - 200;
				if (minId < 0) {
					minId = 1;
				}
				List<Content> contents = contentMng.getMoreArticles(maxId,minId,category);
				if (contents != null && contents.size() > 0) {
					//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
					String regex = ".*[a-zA-Z]+.*";
					Matcher m = null;
					for (Content content : contents) {
						if (content.getContentExt().getShortTitle() != null
								&& !"".equals(content.getContentExt().getShortTitle())) {
							m = Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
							if (m.matches()) {
								content.setIncludeLetter(1);
							} else {
								content.setIncludeLetter(0);
							}
						} else {
							content.setIncludeLetter(0);
						}
					}
					result = combinationReMenZhiYe(site,contents);
				}
			}
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String combinationReMenZhiYe(CmsSite site,List<Content> contents){
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Content content : contents){
			sb.append("<li style=\"display:none;\">");
			sb.append("<dl>");
			sb.append("<dt>");
			sb.append("<a title=\"").append(content.getTitle()).append(" target=\"_blank\" href=\"").append(content.getUrl()).append("\">").append(content.getTitle()).append("</a>");
			sb.append("</dt>");
			sb.append("<dd>").append(DateUtils.convertMinuteString(content.getReleaseDate())).append("</dd>");
			sb.append("</dl>");
			sb.append("</li>");
			count++;
			if (count%5 == 0){
				sb.append("<div class=\"dashujukuaibo_nei\" style=\"display:none;\"></div>");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取更多活动信息
	 */
	@RequestMapping(value = "/getactivity", method = RequestMethod.GET)
	public void getactivity(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		UrlPathHelper helper = new UrlPathHelper();
		String ctx = helper.getOriginatingQueryString(request);
		String[] paramss = ctx.split("\\&");
		String params = paramss[0].substring(paramss[0].indexOf("=") + 1);
		try {
			params = URLDecoder.decode(params,"utf-8");
			String[] times = params.split(" ");
			//拼接时间(时间范围是整月时间)
			String month = ChannelCacheUtils.monthMap.get(times[0]);
			String startTime = times[1] + "-" + month + "-01 00:00:00";
			String endTime = "";
			if ("02".equals(month)){					//二月28天
				endTime = times[1] + "-" + month + "-28 23:59:59";
			}else if ("04".equals(month) || "06".equals(month) || "09".equals(month) || "11".equals(month)){			//4,6,9,11月30天
				endTime = times[1] + "-" + month + "-30 23:59:59";
			}else{										//1,3,5,7,8,10,12月31天
				endTime = times[1] + "-" + month + "-31 23:59:59";
			}
			List<Content> contents = contentMng.findCurrentMonthSports(startTime, endTime);
			if (contents != null && contents.size() > 0){
				JSONArray array = new JSONArray();
				for (Content content : contents){
					JSONObject json = new JSONObject();
					//需要数据:日期,图片,标题,链接,颜色type(当天是红色,不是是蓝色,当月之前是灰色)
					//这里判断查询年份是否是当年,如果不是活动颜色是灰色,如果是当年，查询月份比当前月份小也是灰色。如果是当年大于当月是绿色
					Date currentDate = new Date();
					Date contentDate = content.getSportDate();
					if (Integer.parseInt(times[1]) < (currentDate.getYear()+1900)){												//颜色
						json.put("type","0");
					}else if (Integer.parseInt(times[1]) > (currentDate.getYear()+1900)){
						json.put("type","4");
					}else{
						if (Integer.parseInt(month) < (currentDate.getMonth()+1)){
							json.put("type","0");
						}else if (Integer.parseInt(month) > (currentDate.getMonth()+1)){
							json.put("type","4");
						}else{
							if (contentDate.getDate() < currentDate.getDate()){
								json.put("type","0");
							}else if (contentDate.getDate() > currentDate.getDate()){
								json.put("type","4");
							}else{
								json.put("type","2");
							}
						}
					}
					if (contentDate.getDate() < 10){																			//日期
						json.put("start_time","0"+contentDate.getDate());
					}else{
						json.put("start_time",""+contentDate.getDate());
					}
					//json.put("start_time",contentDate.getDate());
					json.put("imgPath",content.getContentImg());																//图片
					json.put("title",content.getTitle());																		//标题
					json.put("url",content.getUrl());																			//链接
					array.put(json);
				}
				response.setCharacterEncoding("utf-8");
				String jsonStr = array.toString();
				PrintWriter pw = response.getWriter();
				pw.print(jsonStr);
				pw.flush();
				pw.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 动态页入口
	 * PC端和移动端
	 */
	@RequestMapping(value = "/**/*.*", method = RequestMethod.GET)
	public String dynamic(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String towardUrl = "";
		String[] paths = URLHelper.getPaths(request);
		Integer id = Integer.parseInt(paths[1]);
		Content content = contentMng.findById(id);
		if (content == null){
			return FrontUtils.pageNotFound(request, response, model);
		}
		if (content.getStatus() != 2){
			towardUrl = FrontUtils.pageNotFound(request, response, model);
		}else{
			if (HttpRequestDeviceUtils.isMobileDevice(request)){
				towardUrl = content.getStaticMobileUrl();
			}else{
				//towardUrl = content.getStaticUrl();
				//如果有设置跳转地址，则跳转到指定地址。如果没有设置，则跳转到文章页面
				if (content.getContentExt().getSendUrl() != null && content.getContentExt().getSendUrl().length() > 0){
					try {
						response.sendRedirect(content.getContentExt().getSendUrl());
					} catch (IOException e) {
					}
				}else{
					towardUrl = content.getStaticUrl();
				}
			}
		}
		return towardUrl;
	}
	
	/**
	 * 动态页入口
	 */
	//@RequestMapping(value = "/**/*.*", method = RequestMethod.GET)
	//public String dynamic(HttpServletRequest request,
	//		HttpServletResponse response, ModelMap model) {
		// 尽量不要携带太多参数，多使用标签获取数据。
		// 目前已知的需要携带翻页信息。
		// 获得页号和翻页信息吧。
	//	int pageNo = URLHelper.getPageNo(request);
	//	String[] params = URLHelper.getParams(request);
	//	PageInfo info = URLHelper.getPageInfo(request);
	//	String[] paths = URLHelper.getPaths(request);
	//	int len = paths.length;
	//	if (len == 1) {
			// 单页
	//		return channel(paths[0],true, pageNo, params, info, request, response,
	//				model);
	//	} else if (len == 2) {
	//		if (paths[1].equals(INDEX)) {
				// 栏目页
	//			return channel(paths[0],false, pageNo, params, info, request,
	//					response, model);
	//		} else {
				// 内容页
	//			try {
	//				Integer id = Integer.parseInt(paths[1]);
	//				return content(id, pageNo, params, info, request, response,
	//						model);
	//			} catch (NumberFormatException e) {
	//				log.debug("Content id must String: {}", paths[1]);
	//				return FrontUtils.pageNotFound(request, response, model);
	//			}
	//		}
	//	} else {
	//		log.debug("Illegal path length: {}, paths: {}", len, paths);
	//		return FrontUtils.pageNotFound(request, response, model);
	//	}
	//}

	public String channel(String path,boolean checkAlone, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		Channel channel = channelMng.findByPathForTag(path, site.getId());
		if (channel == null) {
			log.debug("Channel path not found: {}", path);
			return FrontUtils.pageNotFound(request, response, model);
		}
		//检查是否单页
		if(checkAlone){
			if(channel.getHasContent()){
				return FrontUtils.pageNotFound(request, response, model);
			}
		}
		model.addAttribute("channel", channel);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		return channel.getTplChannelOrDef();
	}

	public String content(Integer id, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		Content content = contentMng.findById(id);
		boolean isMobile = HttpRequestDeviceUtils.isMobileDevice(request);
		if (content == null) {
			log.debug("Content id not found: {}", id);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		Boolean preview=config.getConfigAttr().getPreview();
		if(config.getViewOnlyChecked()&&!content.getStatus().equals(ContentCheck.CHECKED)){
			return FrontUtils.showMessage(request, model, CONTENT_STATUS_FORBIDDEN);
		}
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = content.getSite();
		Set<CmsGroup> groups = content.getViewGroupsExt();
		int len = groups.size();
		// 需要浏览权限
		if (len != 0) {
			// 没有登录
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}
			// 已经登录但没有权限
			Integer gid = user.getGroup().getId();
			boolean right = false;
			for (CmsGroup group : groups) {
				if (group.getId().equals(gid)) {
					right = true;
					break;
				}
			}
			//无权限且不支持预览
			if (!right&&!preview) {
				String gname = user.getGroup().getName();
				return FrontUtils.showMessage(request, model, GROUP_FORBIDDEN,
						gname);
			}
			//无权限支持预览
			if(!right&&preview){
				model.addAttribute("preview", preview);
				model.addAttribute("groups", groups);
			}
		}
		//给详情页面加上固定超链接
		List<ArticleTopLink> articleTopLinks = articleTopLinkgMng.getList();
		if (articleTopLinks != null && articleTopLinks.size() > 0){
			String[] tagStrs = content.getTagStr().split(",");
			if (tagStrs != null && tagStrs.length < 5){
				List<ArticleTopLink> articleTopLinkList = new ArrayList<ArticleTopLink>();
				for (int i=0;i<articleTopLinks.size();i++){
					articleTopLinkList.add(articleTopLinks.get(i));
					if ((i+1) == (5-tagStrs.length)){
						break;
					}
				}
				if (articleTopLinkList.size() > 0){
					model.addAttribute("articleTopLinkList",articleTopLinkList);
				}
			}
		}
		String txt = content.getTxtByNo(pageNo);
		//替换文章内容视频有效地址(目前只有腾讯需要替换)
		String videoUrl = findValidVideoUrl(content.getTxt1(),content.getUrltt());
		ContentTxt contentTxt = content.getContentTxt();
		contentTxt.setTxt1(videoUrl);
		content.setContentTxt(contentTxt);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
		model.addAttribute("pagination", pagination);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("channel", content.getChannel());
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		model.addAttribute("pic", content.getPictureByNo(pageNo));
		FrontUtils.frontData(request, model, site);
		if (isMobile){
			String modelPath = content.getTplContentOrDef();
			//这里转换成移动端模板路径
			return MobileUtils.getMobilePath(modelPath);
		}else{
			return content.getTplContentOrDef();
		}
		//return content.getTplContentOrDef();
	}

	public String findValidVideoUrl(String flashUrl,String urltt){
		String flashBak = flashUrl;
		String targetUrl = "";
		StringBuilder urlSb = new StringBuilder();
		if (urltt != null){
			String[] videoUrls = urltt.split(",");
			int count = 0;
			while (flashBak.indexOf("playVide") > -1){
				int divLocal = flashBak.indexOf("playVide");
				urlSb.append(flashBak.substring(0,divLocal));
				flashBak = flashBak.substring(divLocal);
				int rightJian = flashBak.indexOf(">") + 1;
				urlSb.append(flashBak.substring(0,rightJian));
				flashBak = flashBak.substring(rightJian);
				int divendLocal = flashBak.indexOf("</div>");
				String vUrl = flashBak.substring(0,divendLocal);
				String houzhui = vUrl.substring(vUrl.length() - 4);
				if ("html".equals(houzhui) || "=mp4".equals(houzhui)) {
					VideoUtils vu = new VideoUtils();
					targetUrl = vu.getTencentMovieSource(videoUrls[count]);
					urlSb.append(targetUrl);
					urlSb.append("</div>");
					divendLocal = flashBak.indexOf("</div>") + 6;
					flashBak = flashBak.substring(divendLocal);
					count++;
				}else{
					divendLocal = flashBak.indexOf("</div>") + 6;
					urlSb.append(flashBak.substring(0,divendLocal));
					flashBak = flashBak.substring(divendLocal);
				}
			}
			urlSb.append(flashBak);
		}else{
			urlSb.append(flashBak);
		}
		return urlSb.toString();
	}
	
	/*public String findValidVideoUrl(String flashUrl,String urltt){
		String targetUrl = "";
		StringBuilder urlSb = new StringBuilder();
		try {
			String flashBak = flashUrl;
			if (urltt == null){
				urlSb.append(flashBak);
			}else{
				String[] videoUrls = urltt.split(",");
				int count = 0;
				while (flashUrl.indexOf("<object") > -1) {
					int highLocal = flashUrl.indexOf("movie") + 14;
					urlSb.append(flashUrl.substring(0, highLocal));
					flashBak = flashUrl.substring(highLocal);
					int yinLocal = flashBak.indexOf("\"");
					String vUrl = flashBak.substring(0, yinLocal);
					String houzhui = vUrl.substring(vUrl.length() - 4);
					if ("html".equals(houzhui) || "=mp4".equals(houzhui)) { 						//这样腾讯地址需要替换为真实引用视频地址(这里是建稿和编辑时用的)
						VideoUtils vu = new VideoUtils();
						targetUrl = vu.getTencentMovieSource(videoUrls[count]);
						flashBak = flashBak.substring(yinLocal);
						int srcLocal = flashBak.indexOf("src=") + 5;
						urlSb.append(targetUrl).append(flashBak.substring(0, srcLocal));
						flashBak = flashBak.substring(srcLocal);
						int yLocal = flashBak.indexOf("\"");
						flashBak = flashBak.substring(yLocal);
						urlSb.append(targetUrl);
						int objendLocal = flashBak.indexOf("</object>") + 9;
						urlSb.append(flashBak.substring(0,objendLocal));
						flashBak = flashBak.substring(objendLocal);
						count++;
					}else {
						int objendLocal = flashBak.indexOf("</object>") + 9;
						urlSb.append(flashBak.substring(0,objendLocal));
						flashBak = flashBak.substring(objendLocal);
					}
				}
				urlSb.append(flashBak);
			}
		} catch (Exception e) {
			e.printStackTrace();
			urlSb.setLength(0);
			urlSb.append(flashUrl);
		}
		return urlSb.toString();
	}*/
	
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsKeywordMng cmsKeywordMng;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private ExpertMng expertMng;
	@Autowired
	private CmsModelItemMng cmsModelItemMng;
	@Autowired
	private CmsSpecialTopicMng cmsSpecialTopicMng;
	@Autowired
	private CmsSearchEngineMng cmsSearchEngineMng;
	@Autowired
	private ArticleTopLinkMng articleTopLinkgMng;
	@Autowired
	private CmsSearchWordsMng cmsSearchWordsMng;
	@Autowired
	private CmsTopicEnterpriseMng enterpriseMng;
}
