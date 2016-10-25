package com.hcrcms.cms.action.front;

import static com.hcrcms.cms.Constants.TPLDIR_SPECIAL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.assist.CmsSearchWords;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.manager.assist.CmsSearchWordsMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.service.SearchWordsCache;
import com.hcrcms.common.web.RequestUtils;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.FrontUtils;
import com.hcrcms.core.web.util.HttpRequestDeviceUtils;
import com.hcrcms.core.web.util.HttpsUtil;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class SearchAct {
	public static final String SEARCH_INPUT = "tpl.searchInput";
	public static final String SEARCH_RESULT = "tpl.searchResult";
	public static final String SEARCH_TAG_RESULT = "tpl.searchTagResult";
	public static final String SEARCH_ERROR = "tpl.searchError";
	public static final String SEARCH_JOB = "tpl.searchJob";
	public static final String SPECIAL_INPUT = "大数据时代";
	public static final String SEARCH_ENGINES = "tpl.searchEngines";
	
	@RequestMapping(value = "/search*", method = RequestMethod.GET)
	public String index(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		String q = RequestUtils.getQueryParam(request, "q");
		String channelId = RequestUtils.getQueryParam(request, "channelId");
		if (SPECIAL_INPUT.equals(q) || q == null || "".equals(q)){
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
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					"content",SEARCH_ENGINES);
		}else {
			String parseQ=parseKeywords(q);
			model.addAttribute("input",q);
			model.addAttribute("q",parseQ);
			searchWordsCache.cacheWord(q);
			if (HttpRequestDeviceUtils.isMobileDevice(request)){
				return FrontUtils.getTplPath(request, site.getSolutionMobilePath(),
						TPLDIR_SPECIAL, SEARCH_RESULT);
			}else{
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_SPECIAL, SEARCH_RESULT);
			}
		}
	}
	
	@RequestMapping(value = "/searchJob*.jspx", method = RequestMethod.GET)
	public String searchJob(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		String q = RequestUtils.getQueryParam(request, "q");
		String category = RequestUtils.getQueryParam(request, "category");
		String workplace = RequestUtils.getQueryParam(request, "workplace");
		model.putAll(RequestUtils.getQueryParams(request));
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		if (StringUtils.isBlank(q)) {
			model.remove("q");
		}else{
			//处理lucene查询字符串中的关键字
			String parseQ=parseKeywords(q);
			model.addAttribute("q",parseQ);
		}
		model.addAttribute("input",q);
		model.addAttribute("queryCategory",category);
		model.addAttribute("queryWorkplace",workplace);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_SPECIAL, SEARCH_JOB);
	}
	
	@RequestMapping("/search/v_ajax_list.jspx")
	public void ajaxList(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		JSONObject object = new JSONObject();
		Map<String,String>wordsMap=new LinkedHashMap<String, String>();
		String word=RequestUtils.getQueryParam(request, "term");
		if(StringUtils.isNotBlank(word)){
			List<CmsSearchWords>words=manager.getList(word,CmsSearchWords.HIT_DESC,true);
			for(CmsSearchWords w:words){
				wordsMap.put(w.getName(), w.getName());
			}
		}
		object.put("words", wordsMap);
		ResponseUtils.renderJson(response, object.get("words").toString());
	}
	
	@RequestMapping(value = "/s/*", method = RequestMethod.GET)
	public String search_index(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		String q = RequestUtils.getQueryParam(request, "q");
		String url = request.getRequestURI();
		String rArg = "";
		String hz = "";
		try{
			System.out.println("++++++++++++ visit IP [" + RequestUtils.getIpAddr(request) + "] searchAct q[" + q + "]    ,    url[" + url + "]  +++++++++");
			if (!url.contains("_")){
				if (q != null){
					hz = PingyinUtil.getPingYin(q);
				}else{
					hz = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
				}
				
				rArg = "1_" + hz;
			}else{
				rArg = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
			}
			String[] rArgs = rArg.split("_");
			Jedis jedis = null;
			String py = "";
			String parseQ;
			try {
				jedis = Constant.getJedis();
				py = jedis.get(rArgs[1]);
				parseQ = "";
				if (py == null) {
					if (!"1".equals(rArgs[0])) {
						Content content = contentMng.findById(Integer
								.parseInt(rArgs[0]));
						if (content != null) {
							String[] tags = content.getContentExt()
									.getTagConent().split(" ");
							if (tags.length > 0) {
								for (String tag : tags) {
									if (tag.indexOf(rArgs[1]) > -1) {
										py = tag.split("_")[0];
										if (jedis != null) {
											jedis.set(rArgs[1], py); //把拼音放到缓存中
											//jedis.expire(rArgs[1], 2592000); //缓存时间为一个月
										}
										break;
									}
								}
							}
						} else {
							py = rArgs[0];
						}
					} else {
						py = rArgs[0];
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			parseQ=parseKeywords(py);
			model.addAttribute("input",py);
			model.addAttribute("q",parseQ);
			//给tag标签搜索添加相关词
			String getRelates = HttpsUtil.relatedWords(py);
			model.addAttribute("stitle",getRelates.replaceAll(",","_"));
			model.addAttribute("sname",getRelates);
			searchWordsCache.cacheWord(py);
		}catch(Exception e){
			System.out.println("----------- search is error:  --------------");
			e.printStackTrace();
		}
		if (HttpRequestDeviceUtils.isMobileDevice(request)){
			return FrontUtils.getTplPath(request, site.getSolutionMobilePath(),TPLDIR_SPECIAL, SEARCH_TAG_RESULT);
		}else{
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_SPECIAL, SEARCH_TAG_RESULT);
		}
	}
	
	public static String parseKeywords(String q){
		char c='\\';
		int cIndex=q.indexOf(c);
		if(cIndex!=-1&&cIndex==0){
			q=q.substring(1);
		}
		if(cIndex!=-1&&cIndex==q.length()-1){
			q=q.substring(0,q.length()-1);
		}
		try {
			String regular = "[\\+\\-\\&\\|\\!\\(\\)\\{\\}\\[\\]\\^\\~\\*\\?\\:\\\\]";
			Pattern p = Pattern.compile(regular);
			Matcher m = p.matcher(q);
			String src = null;
			while (m.find()) {
				src = m.group();
				q = q.replaceAll("\\" + src, ("\\\\" + src));
			}
			q = q.replaceAll("AND", "and").replaceAll("OR", "or").replace("NOT", "not").replace("[", "［").replace("]", "］");
		} catch (Exception e) {
			e.printStackTrace();
			q=q;
		}
		return  q;
	}

	@Autowired
	private CmsSearchWordsMng manager;
	@Autowired
	private SearchWordsCache searchWordsCache;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsSearchWordsMng cmsSearchWordsMng;
}
