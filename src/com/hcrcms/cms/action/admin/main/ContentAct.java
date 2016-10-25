package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.CmsModel;
import com.hcrcms.cms.entity.main.CmsModelItem;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Content.ContentStatus;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.cms.entity.main.ContentExt;
import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.cms.entity.main.ContentType;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.assist.CmsFileMng;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.CmsModelItemMng;
import com.hcrcms.cms.manager.main.CmsModelMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicContentMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.cms.manager.main.CmsTopicMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.manager.main.ContentTypeMng;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.cms.service.ImageSvc;
import com.hcrcms.cms.service.WeiXinSvc;
import com.hcrcms.cms.staticpage.exception.ContentNotCheckedException;
import com.hcrcms.cms.staticpage.exception.GeneratedZeroStaticPageException;
import com.hcrcms.cms.staticpage.exception.StaticPageNotOpenException;
import com.hcrcms.cms.staticpage.exception.TemplateNotFoundException;
import com.hcrcms.cms.staticpage.exception.TemplateParseException;
import com.hcrcms.common.image.ImageUtils;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.upload.FileRepository;
import com.hcrcms.common.util.StrUtils;
import com.hcrcms.common.web.Constants;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.common.web.RequestUtils;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.common.web.springmvc.MessageResolver;
import com.hcrcms.core.entity.CmsGroup;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.Ftp;
import com.hcrcms.core.manager.CmsGroupMng;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.manager.CmsUserMng;
import com.hcrcms.core.manager.DbFileMng;
import com.hcrcms.core.tpl.TplManager;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.CloneUtil;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.CoreUtils;
import com.hcrcms.core.web.util.DateUtils;
import com.hcrcms.core.web.util.DownloadImage;
import com.hcrcms.core.web.util.MobileUtils;
import com.hcrcms.core.web.util.PingyinUtil;
import com.hcrcms.core.web.util.VideoUtils;

@Controller
public class ContentAct{
	private static final Logger log = LoggerFactory.getLogger(ContentAct.class);
	private static final String LINK_HEAD = "http://www.datayuan.cn";
	private static final String ADD_NOFOLLOW = " rel=\"nofollow\"";
	private static final String NOFOLLOW = "nofollow";

	@RequiresPermissions("content:v_left")
	@RequestMapping("/content/v_left.so")
	public String left(String source, ModelMap model) {
		model.addAttribute("source", source);
		return "content/left";
	}

	/**
	 * 栏目导航
	 * 
	 * @param root
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("content:v_tree")
	@RequestMapping(value = "/content/v_tree.so")
	public String tree(String root, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		log.debug("tree path={}", root);
		boolean isRoot;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		model.addAttribute("isRoot", isRoot);
		WebErrors errors = validateTree(root, request);
		if (errors.hasErrors()) {
			log.error(errors.getErrors().get(0));
			ResponseUtils.renderJson(response, "[]");
			return null;
		}
		Integer siteId = CmsUtils.getSiteId(request);
		Integer userId = CmsUtils.getUserId(request);
		List<Channel> list;
		if (isRoot) {
			list = channelMng.getTopListByRigth(userId, siteId, true);
		} else {
			list = channelMng.getChildListByRight(userId, siteId, Integer
					.parseInt(root), true);
		}
		
		model.addAttribute("list", list);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "content/tree";
	}

	/**
	 * 专题列表
	 */
	@RequiresPermissions("content:v_special_topic")
	@RequestMapping("/content/v_special_topic.so")
	public String getSpecialTopic(String source, ModelMap model) {
		List<CmsSpecialTopic> specialTopics = cmsSpecialTopicMng.getListForTag(null,false,100);
		model.addAttribute("specialTopics",specialTopics);
		return "content/specialTopicList";
	}
	
	/**
	 * 专题列表
	 */
	@RequiresPermissions("content:v_topiclist")
	@RequestMapping("/content/v_topiclist.so")
	public String v_topiclist(String source,String text,ModelMap model) {
		List<CmsSpecialTopic> specialTopics = cmsSpecialTopicMng.getListByName(text);
		model.addAttribute("specialTopics",specialTopics);
		return "content/specialTopicList";
	}
	
	/**
	 * 副栏目树
	 * 
	 * @param root
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("content:v_tree_channels")
	@RequestMapping(value = "/content/v_tree_channels.so")
	public String treeChannels(String root, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		tree(root, request, response, model);
		return "content/tree_channels";
	}

	@RequiresPermissions("content:v_list")
	@RequestMapping("/content/v_list.so")
	public String list(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		long time = System.currentTimeMillis();
		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		queryTitle = StringUtils.trim(queryTitle);
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		queryInputUsername = StringUtils.trim(queryInputUsername);
		if (queryTopLevel == null) {
			queryTopLevel = false;
		}
		if (queryRecommend == null) {
			queryRecommend = false;
		}
		if (queryOrderBy == null) {
			queryOrderBy = 4;
		}
		ContentStatus status;
		if (!StringUtils.isBlank(queryStatus)) {
			status = ContentStatus.valueOf(queryStatus);
		} else {
			status = ContentStatus.all;
		}
		Integer queryInputUserId = null;
		if (!StringUtils.isBlank(queryInputUsername)) {
			CmsUser u = cmsUserMng.findByUsername(queryInputUsername);
			if (u != null) {
				queryInputUserId = u.getId();
			} else {
				// 用户名不存在，清空。
				queryInputUsername = null;
			}
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getId();
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getId();
		byte currStep = user.getCheckStep(siteId);
		Pagination p = manager.getPageByRight(queryTitle, queryTypeId,user.getId(),
				queryInputUserId, queryTopLevel, queryRecommend, status, user
						.getCheckStep(siteId), siteId, cid, userId,
				queryOrderBy, cpn(pageNo), CookieUtils.getPageSize(request));
		List<ContentType> typeList = contentTypeMng.getList(true);
		List<CmsModel>models=cmsModelMng.getList(false, true);
		if(cid!=null){
			Channel c=channelMng.findById(cid);
			models=c.getModels(models);
		}
		model.addAttribute("pagination", p);
		model.addAttribute("cid", cid);
		model.addAttribute("typeList", typeList);
		model.addAttribute("currStep", currStep);
		model.addAttribute("site", site);
		model.addAttribute("models", models);
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);
		time = System.currentTimeMillis() - time;
		return "content/list";
	}

	@RequiresPermissions("content:v_add")
	@RequestMapping("/content/v_add.so")
	public String add(Integer cid,Integer modelId, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateAdd(cid,modelId, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getId();
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getId();
		// 栏目
		Channel c;
		if (cid != null) {
			c = channelMng.findById(cid);
		} else {
			c = null;
		}
		// 模型
		CmsModel m;
		if(modelId==null){
			if (c != null) {
				m = c.getModel();
			} else {
				m = cmsModelMng.getDefModel();
				// TODO m==null给出错误提示
				if (m == null) {
					throw new RuntimeException("default model not found!");
				}
			}
		}else{
			m=cmsModelMng.findById(modelId);
		}
		// 模型项列表
		List<CmsModelItem> itemList = cmsModelItemMng.getList(m.getId(), false,
				false);
		// 栏目列表
		List<Channel> channelList;
		Set<Channel> rights;
		if (user.getUserSite(siteId).getAllChannel()) {
			// 拥有所有栏目权限
			rights = null;
		} else {
			rights = user.getChannels(siteId);
		}
		if (c != null) {
			channelList = c.getListForSelect(rights, true);
		} else {
			List<Channel> topList = channelMng.getTopListByRigth(userId,siteId, true);
			channelList = Channel.getListForSelect(topList, rights, true);
		}

		// 专题列表
		List<CmsTopic> topicList;
		if (c != null) {
			topicList = cmsTopicMng.getListByChannel(c.getId());
		} else {
			topicList = new ArrayList<CmsTopic>();
		}
		// 内容模板列表
		List<String> tplList = getTplContent(site, m, null);
		// 会员组列表
		List<CmsGroup> groupList = cmsGroupMng.getList();
		// 内容类型
		List<ContentType> typeList = contentTypeMng.getList(false);
		//专题会场信息
		List<CmsSpecialTopic> specialTopicList = cmsSpecialTopicMng.getListForTag(null,false,100);
		
		model.addAttribute("site",CmsUtils.getSite(request));
		model.addAttribute("model", m);
		model.addAttribute("itemList", itemList);
		model.addAttribute("channelList", channelList);
		model.addAttribute("topicList", topicList);
		model.addAttribute("tplList", tplList);
		model.addAttribute("groupList", groupList);
		model.addAttribute("typeList", typeList);
		model.addAttribute("specialTopicList",specialTopicList);
		
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		if (c != null) {
			model.addAttribute("channel", c);
		}
		return "content/add";
	}

	@RequiresPermissions("content:v_view")
	@RequestMapping("/content/v_view.so")
	public String view(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer pageNo, Integer cid, Integer id,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		byte currStep = user.getCheckStep(site.getId());
		Content content = manager.findById(id);

		model.addAttribute("content", content);
		model.addAttribute("currStep", currStep);
		model.addAttribute("site", site);
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);
		return "content/view";
	}

	@RequiresPermissions("content:v_edit")
	@RequestMapping("/content/v_edit.so")
	public String edit(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer pageNo, Integer cid, Integer id,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getId();
		CmsUser user = CmsUtils.getUser(request);
		// 内容
		Content content = manager.findById(id);
		String bigPictureImgBak = content.getBigPictureImg();						//防止新闻分类大图被覆盖,先备份下
		if ("2".equals(content.getStatus())){
			content.setAttr_status("yes");
		}else if ("0".equals(content.getStatus())){
			content.setAttr_status("0");
		}
		//赋给首页大图位置
		if (content.getIsBigPicture() != null && content.getIsBigPicture() == 1){
			content.setBigPictureLocal(content.getClassify());
		}
		//赋给指数排行大图位置
		if (content.getIsBigPicture() != null && content.getIsBigPicture() == 2){
			content.setIndexrankLocal(content.getClassify());
			content.setIndexrankImg(content.getContentExt().getBigPictureImg());
			content.getContentExt().setBigPictureImg(null);
		}
		// 栏目
		Channel channel = content.getChannel();
		// 模型
		CmsModel m=content.getModel();
		// 模型项列表
		List<CmsModelItem> itemList = cmsModelItemMng.getList(m.getId(), false,
				false);
		// 栏目列表
		Set<Channel> rights;
		if (user.getUserSite(siteId).getAllChannel()) {
			// 拥有所有栏目权限
			rights = null;
		} else {
			rights = user.getChannels(siteId);
		}

		List<Channel> topList = channelMng.getTopList(site.getId(), true);
		List<Channel> channelList = Channel.getListForSelect(topList, rights,
				true);

		// 专题列表
		List<CmsTopic> topicList = cmsTopicMng
				.getListByChannel(channel.getId());
		Set<CmsTopic> topics = content.getTopics();
		for (CmsTopic t : topics) {
			if (!topicList.contains(t)) {
				topicList.add(t);
			}
		}
		Integer[] topicIds = CmsTopic.fetchIds(content.getTopics());
		// 内容模板列表
		List<String> tplList = getTplContent(site, m, content.getTplContent());
		// 会员组列表
		List<CmsGroup> groupList = cmsGroupMng.getList();
		Integer[] groupIds = CmsGroup.fetchIds(content.getViewGroups());
		// 内容类型
		List<ContentType> typeList = contentTypeMng.getList(false);
		// 当前模板，去除基本路径
		int tplPathLength = site.getTplPath().length();
		String tplContent = content.getTplContent();
		if (!StringUtils.isBlank(tplContent)) {
			tplContent = tplContent.substring(tplPathLength);
		}
		
		//判断是更多分类里是否包含"人物专访"和"大数据学堂"，如果有需要把小图地址赋给变量renwuBigPictureImg和xuetangBigPictureImg
		String[] moreCategoies = content.getReleaseMoreCategory().split(",");
		String[] moreCategorIds = content.getReleaseMoreId().split(",");
		String classifybak = content.getClassify();							//先备份当前内容的classify信息，方便以后再恢复
		String bigContentImg = content.getBigPictureImg();
		if (moreCategoies.length > 0){
			for (int i=0;i<moreCategoies.length;i++){
				int categoryi = Integer.parseInt(moreCategoies[i]);
				int categoryid = Integer.parseInt(moreCategorIds[i]);
				if (categoryi == 70){							//新闻资讯
					if (content.getChannel().getId() != categoryi){
						Content xinwenContent = manager.findById(categoryid);
						content.setArticle(xinwenContent.getArticle());
						content.setClassify(xinwenContent.getClassify());
						if (xinwenContent.getBigPictureImg() != null){
							content.getContentExt().setBigPictureImg(xinwenContent.getBigPictureImg());
						}
						model.addAttribute("bigPictureLocal",xinwenContent.getClassify());
						if (content.getIsBigPicture() == 2){									//给指数大图赋值信息
							model.addAttribute("indexrankLocal",xinwenContent.getClassify());
						}
					}else{
						model.addAttribute("bigPictureLocal",content.getClassify());
						if (content.getIsBigPicture() == 2){									//给指数大图赋值信息
							model.addAttribute("indexrankLocal",content.getClassify());
						}
					}
				}
				if (categoryi == 75){							//人物专访
					if (content.getChannel().getId() == categoryi){
						//content.setClassify(classifybak);
						content.setRenwuBigPictureImg(content.getBigPictureImg());
					}else{
						Content rewuContent = manager.findById(categoryid);
						content.setRenwuBigPictureImg(rewuContent.getBigPictureImg());
					}
				}
				if (categoryi == 76){							//大数据学堂
					if (content.getChannel().getId() == categoryi){
						content.setXuetangBigPictureImg(bigContentImg);
						content.setXuetangClassify(classifybak);
						model.addAttribute("xueTangCatory",classifybak);
					}else{
						Content xuetangContent = manager.findById(categoryid);
						content.setXuetangBigPictureImg(xuetangContent.getBigPictureImg());
						content.setXuetangClassify(xuetangContent.getClassify());
						model.addAttribute("xueTangCatory",xuetangContent.getClassify());
					}
				}
				if (categoryi == 85){							//指数大图
					if (content.getChannel().getId() == categoryi){
						content.setIndexrankImg(content.getBigPictureImg());
						content.setZhishuClassify(classifybak);
						model.addAttribute("indexrankLocal",classifybak);
					}else{
						Content indexContent = manager.findById(categoryid);
						content.setIndexrankImg(indexContent.getBigPictureImg());
						content.setZhishuClassify(indexContent.getClassify());
						model.addAttribute("indexrankLocal",indexContent.getClassify());
					}
				}
			}
		}
		
		//由于出现莫名问题99999，所以在这里置为NULL
		if ("99999".equals(content.getClassify())){
			content.setClassify(null);
		}
		//设置内容位置
		/*if ("0".equals(content.getIsPictureTop())){
			content.setAttr_selPictureLocal("日常内容");
		}else if ("1".equals(content.getIsPictureTop())){
			content.setAttr_selPictureLocal("推广");
		}else if ("2".equals(content.getIsPictureTop())){
			content.setAttr_selPictureLocal("置顶");
		}*/
		
		
		//如果当前分类是新闻分类,恢复下大图
		/*if (content.getChannel().getId() == 70){
			content.getContentExt().setBigPictureImg(bigPictureImgBak);
		}else{
			content.getContentExt().setBigPictureImg(null);
		}*/
		
		//是否是专题活动
		if ("专题活动".equals(channel.getName())){
			CmsSpecialTopicContent specialTopicContent = cmsSpecialTopicContentMng.findByTitle(content.getTitle());
			model.addAttribute("specialTopicContent",specialTopicContent);
			//专题会场信息
			List<CmsSpecialTopic> specialTopicList = cmsSpecialTopicMng.getListForTag(null,false,100);
			model.addAttribute("specialTopicList",specialTopicList);
		}
		
		model.addAttribute("site",CmsUtils.getSite(request));
		model.addAttribute("content", content);
		model.addAttribute("channel", channel);
		model.addAttribute("model", m);
		model.addAttribute("itemList", itemList);
		model.addAttribute("channelList", channelList);
		model.addAttribute("topicList", topicList);
		model.addAttribute("topicIds", topicIds);
		model.addAttribute("tplList", tplList);
		model.addAttribute("groupList", groupList);
		model.addAttribute("groupIds", groupIds);
		model.addAttribute("typeList", typeList);
		model.addAttribute("tplContent", tplContent);
		if (cid != null) {
			model.addAttribute("cid", cid);
		}

		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);

		return "content/edit";
	}

	@RequiresPermissions("content:o_save")
	@RequestMapping("/content/o_save.so")
	public String save(Content bean, ContentExt ext, ContentTxt txt, Integer meetingId, Integer siteLocation,Integer topicType,
			Boolean copyimg,Integer sendType,Integer selectImg,String weixinImg,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Integer channelId, Integer typeId, String tagStr, Boolean draft,Integer[] moreChannels,String bigPictureImg,String renwuBigPictureImg,String xuetangBigPictureImg,String attr_redirecctUrl,
			Integer cid, Integer modelId,HttpServletRequest request, ModelMap model) {
		/*WebErrors errors = validateSave(bean, channelId, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}*/
		// 加上模板前缀
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String tplPath = site.getTplPath();
		String releaseMoreCategory = "";
		String releaseMoreId = "";
		Content beanbak = CloneUtil.clone(bean);
		ContentExt extbak = CloneUtil.clone(ext);
		ContentTxt txtbak = CloneUtil.clone(txt);
		if (moreChannels.length > 0){
			for (Integer channelid : moreChannels){
				bean = CloneUtil.clone(beanbak);
				ext = CloneUtil.clone(extbak);
				txt = CloneUtil.clone(txtbak);
				channelId = channelid;
				bean.setSite(site);
				if (channelId == 70){
					ext.setBigPictureImg(bigPictureImg);
					//判断首页是否有大图
					if (!"0".equals(bean.getBigPictureLocal()) && bean.getBigPictureLocal().length() > 0){
						bean.setIsBigPicture(1);
						bean.setArticle("首页大图");
						bean.setClassify(bean.getBigPictureLocal());
					}else{
						bean.setIsBigPicture(0);
					}
					if (attr_redirecctUrl != null){
						ext.setSendUrl(attr_redirecctUrl);
					}
				}else if (channelId == 74){			//活动推荐
					bean.setIsBigPicture(0);
				}else if (channelId == 75){			//人物专访
					ext.setBigPictureImg(renwuBigPictureImg);
					bean.setIsBigPicture(0);
				}else if (channelId == 76){		//大数据学堂
					ext.setBigPictureImg(xuetangBigPictureImg);
					//判断大数据学堂分类
					if (!"0".equals(bean.getXueTangCatory())){
						bean.setArticle("大数据学堂");
						bean.setClassify(bean.getXueTangCatory());
					}
					bean.setIsBigPicture(0);
				}else if (channelId == 85){		//指数排行
					//判断是指数排行
					if (!"0".equals(bean.getIndexrankLocal())){
						bean.setIsBigPicture(2);
						bean.setArticle("指数排行大图");
						bean.setClassify(bean.getIndexrankLocal());
						ext.setBigPictureImg(bean.getIndexrankImg());
					}
					bean.setIsBigPicture(0);
				}else{
					bean.setIsBigPicture(0);
				}
				
				if (!StringUtils.isBlank(ext.getTplContent())) {
					ext.setTplContent(tplPath + ext.getTplContent());
				}
				bean.setAttr(RequestUtils.getRequestMap(request, "attr_"));
				String[] tagArr = StrUtils.splitAndTrim(tagStr, " ", MessageResolver
						.getMessage(request, "content.tagStr.split"));
				if(txt!=null&&copyimg!=null&&copyimg){
					txt=copyContentTxtImg(txt, site);
				}
				
				if (bean.getRecommandView() == null){
					bean.setRecommandView(0);
				}
				if (bean.getRecommendInterview() == null){
					bean.setRecommendInterview(0);
				}
				if (bean.getMySport() == null){
					bean.setMySport(findMySport(ext.getTitle()));
				}
				bean.setIsPushBaidu(0);
				
				//图片是否置顶
				if ("置顶".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(2);
				}else if ("推广".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(1);
				}else if ("日常内容".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(0);
				}
				//判断文章是否显示
				if (bean.getAttr_status() != null){
					if ("yes".equals(bean.getAttr_status())){
						bean.setStatus(Byte.parseByte("2"));
					}else if ("no".equals(bean.getAttr_status())){
						bean.setStatus(Byte.parseByte("0"));
					}
				}
				
				//给活动日期赋值
				if (bean.getAttr_sportDate() != null){
					bean.setSportDate(bean.getAttr_sportDate());
				}
				
				//给文章内容添加关键词超链接
				addContentWords(txt);
				//如果包含有腾讯视频(v.qq.com),需要单独把视频地址保留下来,方便以后自动更新引用地址
				saveTengxunVideoUrl(txt);
				//这里把内容txt表内容复制到txt1字段一份，方便页面视频播放
				copyTxt(txt);
				
				//把TAG标签保存到jc_content_ext表中
				//ext.setTagConent(tagStr);
				if (tagStr != null && tagStr.length() > 0){
					String tagStrs = cachePinYin(tagStr);
					ext.setTagConent(tagStrs);
				}
				
				if (ext.getContentMobileImg() == null){
					if (channelId != 71 && channelId != 79 && channelId != 82){
						//保存手机图片路径
						ext.setContentMobileImg(saveMobileImagePath(ext.getContentImg()));
						//这里需要生成手机版缩略图
						generatePhoneFirstThumb(ext.getContentImg());
					}
				}
				
				//判断作者是否为专家,如果是：在jc_content表的isExpert字段置1(这个字段方便查询按所有专家文章倒序)
				if (bean.getIsExpert() == null){
					if (channelId != 79 && channelId != 82){							//如果为热门招聘则不需要判断是否为专家
						if (confirmExpert(ext.getAuthor())){
							bean.setIsExpert(1);
						}else{
							bean.setIsExpert(0);
						}
					}else{
						bean.setIsExpert(0);
					}
				}
				
				//自动给作者添加拼音
				if (ext.getAuthorPy() == null){
					if (ext.getAuthor() != null){
						String authorPy = PingyinUtil.getPingYin(ext.getAuthor());
						ext.setAuthorPy(authorPy);
						cacheAuthor(ext.getAuthor(),ext.getAuthorPy());
					}
				}
				
				bean = manager.save(bean, ext, txt,channelIds, topicIds, viewGroupIds,
						tagArr, attachmentPaths, attachmentNames, attachmentFilenames,
						picPaths, picDescs, channelId, typeId, draft,false, user, false,meetingId,siteLocation,topicType);
				
				releaseMoreCategory += "".equals(releaseMoreCategory)?""+bean.getChannel().getId():","+bean.getChannel().getId();
				releaseMoreId += "".equals(releaseMoreId)?""+bean.getId():","+bean.getId();
				
				//微信消息发送
				//weiXinSvc.sendMessage(sendType, selectImg, weixinImg, bean, ext, txt);
				//处理附件
				fileMng.updateFileByPaths(attachmentPaths,picPaths,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
				log.info("save Content id={}", bean.getId());
				cmsLogMng.operating(request, "content.log.save", "id=" + bean.getId()
						+ ";title=" + bean.getTitle());
				//这里把三个内容类主键ID清空
				//bean.setId(null);
				//ext.setId(null);
				//txt.setId(null);
			}
			if (releaseMoreCategory.length() > 0 && releaseMoreId.length() > 0){
				String[] oids = releaseMoreId.split(",");
				for (String oid : oids){
					manager.updateMoreCategoryAndId(oid,releaseMoreCategory,releaseMoreId);
				}
			}
		}
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		model.addAttribute("message", "global.success");
		//****************  这里需要显示所有频道信息，把cid置为NULL  *********************
		cid = null;
		return add(cid,modelId, request, model);
	}

	@RequiresPermissions("content:o_update")
	@RequestMapping("/content/o_update.so")
	public String update(String queryStatus, Integer queryTypeId,CmsSpecialTopicContent topicContent,
			Integer meetingId, Integer siteLocation,Integer specialTopicContentId,String subPlaceStr,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Content bean, ContentExt ext, ContentTxt txt,
			Boolean copyimg,Integer sendType,Integer selectImg,String weixinImg,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths,String[] picDescs,
			Integer channelId, Integer typeId, String tagStr, Boolean draft,
			Integer cid,String[]oldattachmentPaths,String[] oldpicPaths,
			String oldTitleImg,String oldContentImg,String oldTypeImg,Integer[] moreChannels,String moreContentIds,String bigPictureImg,
			String renwuBigPictureImg,String xuetangBigPictureImg,String moreChannelsh,String contentImgCompare,String attr_redirecctUrl,
			Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		/*WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}*/
		
		// 加上模板前缀
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		
		Integer[] insertMoreChannels = null;
		Integer[] deleteMoreChannels = null;
		List<Integer[]> updateMoreChannels = new ArrayList<Integer[]>();
		//第一步、找出新分类中不在原分类中的内容(找出新增的)
		insertMoreChannels = getInsertMoreChannels(moreChannels,moreChannelsh);
		//第二步、找出删除的
		deleteMoreChannels = getDeleteMoreChannels(moreChannels,moreChannelsh,moreContentIds);
		//第三步、找出更新的
		updateMoreChannels = getUpdateMoreChannels(moreChannels,moreChannelsh,moreContentIds);
		String[] releaseMores = new String[2];													//找出最终修改文章ID集合
		
		if (insertMoreChannels != null && insertMoreChannels.length > 0){
			int contentIdBak = bean.getId();
			bean.setId(null);
			ext.setId(null);
			txt.setId(null);
			bean.setStatus(bean.getStatus()==null?2:bean.getStatus());
			releaseMores = osaveArticle(bean,ext,txt,-1,siteLocation,null,
					copyimg,sendType,selectImg,weixinImg,
					channelIds,topicIds,viewGroupIds,
					attachmentPaths,attachmentNames,
					attachmentFilenames,picPaths,picDescs,
					channelId,typeId,tagStr,draft,insertMoreChannels,bigPictureImg,renwuBigPictureImg,xuetangBigPictureImg,attr_redirecctUrl,
					cid,site,user,request);
			bean.setId(contentIdBak);
			ext.setId(contentIdBak);
			txt.setId(contentIdBak);
		}
		
		if (deleteMoreChannels != null && deleteMoreChannels.length > 0){
			osdeleteArticle(deleteMoreChannels);
		}
		
		if ((updateMoreChannels.get(0) != null && updateMoreChannels.get(0).length > 0)
				&& (updateMoreChannels.get(1) != null && updateMoreChannels.get(1).length > 0)){
			releaseMores = oupdateArticle(queryStatus,queryTypeId,topicContent,
					meetingId,siteLocation,specialTopicContentId,subPlaceStr,
					queryTopLevel,queryRecommend,
					queryOrderBy,bean,ext,txt,
					copyimg,sendType,selectImg,weixinImg,
					channelIds,topicIds,viewGroupIds,
					attachmentPaths,attachmentNames,
					attachmentFilenames,picPaths,picDescs,
					channelId,typeId,tagStr,draft,
					cid,oldattachmentPaths,oldpicPaths,
					oldTitleImg,oldContentImg,oldTypeImg,updateMoreChannels.get(0),moreContentIds,bigPictureImg,
					renwuBigPictureImg,xuetangBigPictureImg,moreChannelsh,contentImgCompare,attr_redirecctUrl,
					pageNo,request,model,releaseMores,site,user,updateMoreChannels.get(1));
		}
		
		if (releaseMores.length > 0){
			String[] oids = releaseMores[1].split(",");
			for (String oid : oids){
				manager.updateMoreCategoryAndId(oid,releaseMores[0],releaseMores[1]);
			}
		}
		
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_delete")
	@RequestMapping("/content/o_delete.so")
	public String delete(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Content[] beans;
		// 是否开启回收站
		if (site.getResycleOn()) {
			beans = manager.cycle(ids);
			for (Content bean : beans) {
				log.info("delete to cycle, Content id={}", bean.getId());
			}
		} else {
			for(Integer id:ids){
				Content c=manager.findById(id);
				//处理附件
				manager.updateFileByContent(c, false);
			}
			beans = manager.deleteByIds(ids);
			for (Content bean : beans) {
				log.info("delete Content id={}", bean.getId());
				cmsLogMng.operating(request, "content.log.delete", "id="
						+ bean.getId() + ";title=" + bean.getTitle());
			}
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_check")
	@RequestMapping("/content/o_check.so")
	public String check(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateCheck(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user = CmsUtils.getUser(request);
		Content[] beans = manager.check(ids, user);
		for (Content bean : beans) {
			log.info("check Content id={}", bean.getId());
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_check")
	@RequestMapping("/content/o_ajax_check.so")
	public void ajaxCheck(Integer[] ids, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		WebErrors errors = validateCheck(ids, request);
		JSONObject json=new JSONObject();
		if (errors.hasErrors()) {
			json.put("error", errors.getErrors().get(0));
			json.put("success", false);
		}
		CmsUser user = CmsUtils.getUser(request);
		manager.check(ids, user);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequiresPermissions("content:o_static")
	@RequestMapping("/content/o_static.so")
	public String contentStatic(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateStatic(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		try {
			Content[] beans = manager.contentStatic(ids);
			for (Content bean : beans) {
				log.info("static Content id={}", bean.getId());
			}
			model.addAttribute("message", errors.getMessage(
					"content.staticGenerated", beans.length));
		} catch (TemplateNotFoundException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (TemplateParseException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (GeneratedZeroStaticPageException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(), e
					.getGenerated()));
		} catch (StaticPageNotOpenException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (ContentNotCheckedException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_reject")
	@RequestMapping("/content/o_reject.so")
	public String reject(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Byte rejectStep,
			String rejectOpinion, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateReject(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user = CmsUtils.getUser(request);
		Content[] beans = manager.reject(ids, user,rejectStep, rejectOpinion);
		for (Content bean : beans) {
			log.info("reject Content id={}", bean.getId());
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_reject")
	@RequestMapping("/content/o_ajax_reject.so")
	public void ajaxReject(Integer[] ids,Byte rejectStep, String rejectOpinion, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		WebErrors errors = validateReject(ids, request);
		JSONObject json=new JSONObject();
		if (errors.hasErrors()) {
			json.put("error", errors.getErrors().get(0));
			json.put("success", false);
		}
		CmsUser user = CmsUtils.getUser(request);
		manager.reject(ids, user, rejectStep,rejectOpinion);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:v_tree_radio")
	@RequestMapping(value = "/content/v_tree_radio.so")
	public String move_tree(String root, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		tree(root, request, response, model);
		return "content/tree_move";
	}
	
	@RequiresPermissions("content:o_move")
	@RequestMapping("/content/o_move.so")
		public void move(Integer contentIds[], Integer channelId,HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		Boolean pass = true;
		if (contentIds != null && channelId != null) {
			Channel channel=channelMng.findById(channelId);
			for(Integer contentId:contentIds){
				Content bean=manager.findById(contentId);
				if(bean!=null&&channel!=null){
					bean.setChannel(channel);
					manager.update(bean);
				}
			}
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:o_copy")
	@RequestMapping("/content/o_copy.so")
		public void copy(Integer contentIds[],Integer channelId,Integer siteId,HttpServletRequest request,HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user=CmsUtils.getUser(request);
		Boolean pass = true;
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				Content bean=manager.findById(contentId);
				Content beanCopy= new Content();
				ContentExt extCopy=new ContentExt();
				ContentTxt txtCopy=new ContentTxt();
				beanCopy=bean.cloneWithoutSet();
				beanCopy.setChannel(channelMng.findById(channelId));
				//复制到别站
				if(siteId!=null){
					beanCopy.setSite(siteMng.findById(siteId));
				}
				boolean draft=false;
				if(bean.getStatus().equals(ContentCheck.DRAFT)){
					draft=true;
				}
				BeanUtils.copyProperties(bean.getContentExt(), extCopy);
				if(bean.getContentTxt()!=null){
					BeanUtils.copyProperties(bean.getContentTxt(), txtCopy);
				}
				manager.save(beanCopy, extCopy, txtCopy, bean.getChannelIdsWithoutChannel(),
						bean.getTopicIds(), bean.getViewGroupIds(), bean.getTagArray(), bean.getAttachmentPaths(), bean.getAttachmentNames(),
						bean.getAttachmentFileNames(), bean.getPicPaths(), bean.getPicDescs(), channelId, bean.getType().getId(), draft,false, user, false,null,null,null);
			}
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 引用
	 * @param contentIds
	 * @param channelId
	 */
	@RequiresPermissions("content:o_refer")
	@RequestMapping("/content/o_refer.so")
		public void refer(Integer contentIds[],Integer channelId,HttpServletRequest request,HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user=CmsUtils.getUser(request);
		Boolean pass = true;
		if(user==null){
			ResponseUtils.renderJson(response, "false");
		}
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				manager.updateByChannelIds(contentId, new Integer[]{channelId});
			}
		}else{
			ResponseUtils.renderJson(response, "false");
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:o_priority")
	@RequestMapping("/content/o_priority.so")
	public String priority(Integer[] wids, Byte[] topLevel,
			String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		for(int i=0;i<wids.length;i++){
			Content c=manager.findById(wids[i]);
			c.setTopLevel(topLevel[i]);
			manager.update(c);
		}
		log.info("update CmsFriendlink priority.");
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend, queryOrderBy, cid, pageNo, request, model);
	}
	
	/**
	 * 推送至专题
	 * @param contentIds
	 * @param topicIds
	 */
	@RequiresPermissions("content:o_send_to_topic")
	@RequestMapping("/content/o_send_to_topic.so")
		public void refer(Integer contentIds[],Integer[] topicIds,HttpServletRequest request,HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user=CmsUtils.getUser(request);
		Boolean pass = true;
		if(user==null){
			ResponseUtils.renderJson(response, "false");
		}
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				manager.addContentToTopics(contentId,topicIds);
			}
		}else{
			ResponseUtils.renderJson(response, "false");
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequiresPermissions("content:o_upload_attachment")
	@RequestMapping("/content/o_upload_attachment.so")
	public String uploadAttachment(
			@RequestParam(value = "attachmentFile", required = false) MultipartFile file,
			String attachmentNum, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file,request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return "content/attachment_iframe";
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上访问地址
				fileUrl = request.getContextPath() + dbFilePath + fileUrl;
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上url前缀
				fileUrl = ftpUrl + fileUrl;
			} else {
				String ctx = request.getContextPath();
				fileUrl = fileRepository.storeByExt(site.getUploadPath(), ext,
						file);
				// 加上部署路径
				fileUrl = ctx + fileUrl;
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, origName, false);
			model.addAttribute("attachmentPath", fileUrl);
			model.addAttribute("attachmentName", origName);
			model.addAttribute("attachmentNum", attachmentNum);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		return "content/attachment_iframe";
	}

	@RequiresPermissions("content:o_upload_media")
	@RequestMapping("/content/o_upload_media.so")
	public String uploadMedia(
			@RequestParam(value = "mediaFile", required = false) MultipartFile file,
			String filename, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file, request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return "content/media_iframe";
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(dbFilePath.length());
					fileUrl = dbFileMng.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext,
							file.getInputStream());
					// 加上访问地址
					fileUrl = request.getContextPath() + dbFilePath + fileUrl;
				}
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ftpUrl.length());
					fileUrl = ftp.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
							.getInputStream());
					// 加上url前缀
					fileUrl = ftpUrl + fileUrl;
				}
			} else {
				String ctx = request.getContextPath();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ctx.length());
					fileUrl = fileRepository.storeByFilename(filename, file);
				} else {
					fileUrl = fileRepository.storeByExt(site.getUploadPath(),
							ext, file);
					// 加上部署路径
					fileUrl = ctx + fileUrl;
				}
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, fileUrl, false);
			model.addAttribute("mediaPath", fileUrl);
			model.addAttribute("mediaExt", ext);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		return "content/media_iframe";
	}
	
	@RequiresPermissions("content_cycle:v_list")
	@RequestMapping("/content_cycle/v_list.so")
	public String cycleList(Integer queryTypeId, Boolean queryTopLevel,
			Boolean queryRecommend, Integer queryOrderBy, Integer cid,
			Integer pageNo, HttpServletRequest request, ModelMap model) {
		list(ContentStatus.recycle.toString(), queryTypeId, queryTopLevel,
				queryRecommend, queryOrderBy, cid, pageNo, request, model);
		return "content/cycle_list";
	}

	@RequiresPermissions("content_cycle:o_recycle")
	@RequestMapping("/content_cycle/o_recycle.so")
	public String cycleRecycle(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Content[] beans = manager.recycle(ids);
		for (Content bean : beans) {
			log.info("delete Content id={}", bean.getId());
		}
		return cycleList(queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content_cycle:o_delete")
	@RequestMapping("/content_cycle/o_delete.so")
	public String cycleDelete(String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		for(Integer id:ids){
			Content c=manager.findById(id);
			//处理附件
			manager.updateFileByContent(c, false);
		}
		Content[] beans = manager.deleteByIds(ids);
		for (Content bean : beans) {
			log.info("delete Content id={}", bean.getId());
		}
		return cycleList(queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_generateTags")
	@RequestMapping("/content/o_generateTags.so")
	public void generateTags(String title,HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		String tags="";
		if(StringUtils.isNotBlank(title)){
			tags=StrUtils.getKeywords(title, true);
		}
		json.put("tags", tags);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:rank_list")
	@RequestMapping(value = "/content/rank_list.so")
	public String contentRankList(Integer orderBy,Integer pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		model.addAttribute("orderBy", orderBy);
		model.addAttribute("pageNo", cpn(pageNo));
		model.addAttribute("pageSize", CookieUtils.getPageSize(request));
		model.addAttribute("site", CmsUtils.getSite(request));
		return "content/ranklist";
	}

	private void addAttibuteForQuery(ModelMap model, String queryTitle,
			String queryInputUsername, String queryStatus, Integer queryTypeId,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Integer pageNo) {
		if (!StringUtils.isBlank(queryTitle)) {
			model.addAttribute("queryTitle", queryTitle);
		}
		if (!StringUtils.isBlank(queryInputUsername)) {
			model.addAttribute("queryInputUsername", queryInputUsername);
		}
		if (queryTypeId != null) {
			model.addAttribute("queryTypeId", queryTypeId);
		}
		if (queryStatus != null) {
			model.addAttribute("queryStatus", queryStatus);
		}
		if (queryTopLevel != null) {
			model.addAttribute("queryTopLevel", queryTopLevel);
		}
		if (queryRecommend != null) {
			model.addAttribute("queryRecommend", queryRecommend);
		}
		if (queryOrderBy != null) {
			model.addAttribute("queryOrderBy", queryOrderBy);
		}
		if (pageNo != null) {
			model.addAttribute("pageNo", pageNo);
		}
	}

	private List<String> getTplContent(CmsSite site, CmsModel model, String tpl) {
		String sol = site.getSolutionPath();
		String tplPath = site.getTplPath();
		List<String> tplList = tplManager.getNameListByPrefix(model
				.getTplContent(sol, false));
		tplList = CoreUtils.tplTrim(tplList, tplPath, tpl);
		return tplList;
	}
	
	private WebErrors validateTree(String path, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		// if (errors.ifBlank(path, "path", 255)) {
		// return errors;
		// }
		return errors;
	}

	private WebErrors validateAdd(Integer cid,Integer modelId, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (cid == null) {
			return errors;
		}
		Channel c = channelMng.findById(cid);
		if (errors.ifNotExist(c, Channel.class, cid)) {
			return errors;
		}
		//所选发布内容模型不在栏目模型范围内
		if(modelId!=null){
			CmsModel m=cmsModelMng.findById(modelId);
			if(errors.ifNotExist(m, CmsModel.class, modelId)){
				return errors;
			}
			//默认没有配置的情况下modelIds为空 则允许添加
			if(c.getModelIds().size()>0&&!c.getModelIds().contains(modelId.toString())){
				errors.addErrorCode("channel.modelError", c.getName(),m.getName());
			}
		}
		Integer siteId = CmsUtils.getSiteId(request);
		if (!c.getSite().getId().equals(siteId)) {
			errors.notInSite(Channel.class, cid);
			return errors;
		}
		return errors;
	}

	private WebErrors validateSave(Content bean, Integer channelId,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		bean.setSite(site);
		if (errors.ifNull(channelId, "channelId")) {
			return errors;
		}
		Channel channel = channelMng.findById(channelId);
		if (errors.ifNotExist(channel, Channel.class, channelId)) {
			return errors;
		}
		if (channel.getChild().size() > 0) {
			errors.addErrorCode("content.error.notLeafChannel");
		}
		//所选发布内容模型不在栏目模型范围内
		if(bean.getModel().getId()!=null){
			CmsModel m=bean.getModel();
			if(errors.ifNotExist(m, CmsModel.class, bean.getModel().getId())){
				return errors;
			}
			//默认没有配置的情况下modelIds为空 则允许添加
			if(channel.getModelIds().size()>0&&!channel.getModelIds().contains(bean.getModel().getId().toString())){
				errors.addErrorCode("channel.modelError", channel.getName(),m.getName());
			}
		}
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		// Content content = manager.findById(id);
		// TODO 是否有编辑的数据权限。
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		Content content = manager.findById(id);
		// TODO 是否有编辑的数据权限。
		// 是否有审核后更新权限。
		if (!content.isHasUpdateRight()) {
			errors.addErrorCode("content.error.afterCheckUpdate");
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
//		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		if(ids!=null&&ids.length>0){
			for (Integer id : ids) {
				/*
				if (vldExist(id, site.getId(), errors)) {
					return errors;
				}
				*/
				Content content = manager.findById(id);
				// TODO 是否有编辑的数据权限。
				// 是否有审核后删除权限。
				if (!content.isHasDeleteRight()) {
					errors.addErrorCode("content.error.afterCheckDelete");
					return errors;
				}

			}
		}
		return errors;
	}

	private WebErrors validateCheck(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private WebErrors validateStatic(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private WebErrors validateReject(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private WebErrors validateUpload(MultipartFile file,
			HttpServletRequest request) {
		String origName = file.getOriginalFilename();
		CmsUser user= CmsUtils.getUser(request);
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		int fileSize = (int) (file.getSize() / 1024);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(file, "file")) {
			return errors;
		}
		//非允许的后缀
		if(!user.isAllowSuffix(ext)){
			errors.addErrorCode("upload.error.invalidsuffix", ext);
			return errors;
		}
		//超过附件大小限制
		if(!user.isAllowMaxFile((int)(file.getSize()/1024))){
			errors.addErrorCode("upload.error.toolarge",origName,user.getGroup().getAllowMaxFile());
			return errors;
		}
		//超过每日上传限制
		if (!user.isAllowPerDay(fileSize)) {
			long laveSize=user.getGroup().getAllowPerDay()-user.getUploadSize();
			if(laveSize<0){
				laveSize=0;
			}
			errors.addErrorCode("upload.error.dailylimit", laveSize);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		Content entity = manager.findById(id);
		if (errors.ifNotExist(entity, Content.class, id)) {
			return true;
		}
		if (!entity.getSite().getId().equals(siteId)) {
			errors.notInSite(Content.class, id);
			return true;
		}
		return false;
	}
	
	private ContentTxt copyContentTxtImg(ContentTxt txt,CmsSite site){
		if(StringUtils.isNotBlank(txt.getTxt())){
			txt.setTxt(copyTxtHmtlImg(txt.getTxt(), site));
		}
		if(StringUtils.isNotBlank(txt.getTxt1())){
			txt.setTxt1(copyTxtHmtlImg(txt.getTxt1(), site));
		}	
		if(StringUtils.isNotBlank(txt.getTxt2())){
			txt.setTxt2(copyTxtHmtlImg(txt.getTxt2(), site));
		}
		if(StringUtils.isNotBlank(txt.getTxt3())){
			txt.setTxt3(copyTxtHmtlImg(txt.getTxt3(), site));
		}
		return txt;
	}
	
	private String copyTxtHmtlImg(String txtHtml,CmsSite site){
		List<String>imgUrls=ImageUtils.getImageSrc(txtHtml);
		for(String img:imgUrls){
			txtHtml=txtHtml.replace(img, imageSvc.crawlImg(img,site.getContextPath(),site.getUploadPath()));
		}
		return txtHtml;
	}

	/**
	 * 如果包含有腾讯视频(v.qq.com),需要单独把视频地址保留下来,方便以后自动更新引用地址
	 */
	public void saveTengxunVideoUrl(ContentTxt txt){
		String flashBak = txt.getTxt();
		StringBuilder urlSb = new StringBuilder();
		if (flashBak.indexOf("v.qq.com") > -1){
			while (flashBak.indexOf("<object") > -1) {
				int highLocal = flashBak.indexOf("movie") + 14;
				flashBak = flashBak.substring(highLocal);
				int yinLocal = flashBak.indexOf("\"");
				String vUrl = flashBak.substring(0, yinLocal);
				String houzhui = vUrl.substring(vUrl.length() - 4);
				if ("html".equals(houzhui)) { 						//这样腾讯地址需要替换为真实引用视频地址(这里是建稿和编辑时用的)
					flashBak = flashBak.substring(yinLocal);
					int srcLocal = flashBak.indexOf("</object>") + 9;
					urlSb.append(vUrl).append(",");
					flashBak = flashBak.substring(srcLocal);
				}
			}
			if (urlSb.length() > 0){
				urlSb.setLength(urlSb.length()-1);
			}
			txt.setUrltt(urlSb.toString());
		}
	}
	
	private void copyTxt(ContentTxt txt){
		//思路：把txt字段内容视频地址取出来(由于以前页面不能播放)，拼一个<div id="playVide0">url</div>
		//层id属性顺利取决于插入多少个视频地址，这样方便页面视频播放
		String str = addNoFollow(txt.getTxt());
		//查找正文里的图片资源，如果图片路径中包含hcrBigDataWeb，说明是本地路径不用下载，否则需要下载图片资源然后再修改本地图片路径
		String pictureTxt = findDownloadImage(str);
		txt.setTxt(pictureTxt);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		boolean flag = true;
		while (flag){
			if (str.indexOf("<object") > 0){					//发现有插入本地视频
				sb.append(str.substring(0,str.indexOf("<object")));
				String linshi = str.substring(0,str.indexOf("</object>")+9);
				String addressUrl = linshi.substring(str.indexOf("movie\" value=\"")+14,str.indexOf("<embed")-4);
				str = str.substring(str.indexOf("</object>")+9);
				sb.append("<div id=\"playVide"+count+"\" style=\"display:none;\">"+addressUrl+"</div>");
			}else if (str.indexOf("[http") > 0){				//发现有插入在线视频
				sb.append(str.substring(0,str.indexOf("[http")));
				String linshi = str.substring(0,str.indexOf("/]")+2);
				String addressUrl = linshi.substring(linshi.indexOf("[http")+1,linshi.indexOf("/]"));
				str = str.substring(str.indexOf("/]")+2);
				sb.append("<div id=\"playVide"+count+"\" style=\"display:none;\">"+addressUrl+"</div>");
			}else{
				sb.append(str);
				flag = false;
			}
			count++;
		}
		String newTxt = findValidVideoUrl(sb.toString(),txt.getUrltt());
		txt.setTxt1(findDownloadImage(newTxt));
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
	
	public String findDownloadImage(String str){
		String saveImagePath = "/hcrBigDataWeb/u/cms/www/";
		//String saveImagePath = "/u/cms/www/";
		String imageYM = DateUtils.convertImagePathYM();
		String result = "";
		if (str != null){
			int imgStart = str.indexOf("<img");
			while (imgStart > -1){
				result += str.substring(0,imgStart);
				str = str.substring(imgStart);
				int imgsrc = str.indexOf("src=\"");
				result += str.substring(0,imgsrc+5);
				str = str.substring(imgsrc+5);
				String imageSrc = str.substring(0,str.indexOf("\""));
				str = str.substring(str.indexOf("\""));
				if (imageSrc.indexOf("u/cms/www") == -1){					//包含hcrBigDataWeb说明是本地图片路径
					String fileName = imageSrc.substring(imageSrc.lastIndexOf("/")+1);
					//本地图片路径
					String localImagePath = "D:/tools/apache-tomcat-7.0.63/webapps/hcrBigDataWeb/u/cms/www/" + imageYM + "/" + fileName;
					//142图片路径
					//String localImagePath = "/DATA/hcr/apache-tomcat-7.0.63/webapps/ROOT/u/cms/www/" + imageYM + "/" + fileName;
					//83图片路径
					//String localImagePath = "/APP/hcr/apache-tomcat-7.0.63/webapps/ROOT/u/cms/www/" + imageYM + "/" + fileName;
					DownloadImage.savePicture(imageSrc,localImagePath);
					result += saveImagePath + imageYM + "/" + fileName;
				}else{
					result += imageSrc;
				}
				imgStart = str.indexOf("<img");
			}
			result += str;
		}
		return result;
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
	
	/**
	 * 给内容中超链接内链添加nofollow(防止搜索引擎来抓取)
	 */
	private String addNoFollow(String txt){
		StringBuilder sb = new StringBuilder();
		try {
			if (txt.indexOf(LINK_HEAD) > -1) {
				sb.append(txt.substring(0, txt.indexOf(LINK_HEAD)));
				boolean flag = true;
				while (flag) {
					txt = txt.substring(txt.indexOf(LINK_HEAD));
					String compareFollow = txt.substring(0, txt.indexOf(">"));
					if (compareFollow.indexOf(NOFOLLOW) == -1) {
						sb.append(txt.substring(0, txt.indexOf(">"))).append(ADD_NOFOLLOW);
					} else {
						sb.append(txt.substring(0, txt.indexOf(">")));
					}
					txt = txt.substring(txt.indexOf(">"));
					if (txt.indexOf(LINK_HEAD) == -1) {
						flag = false;
						sb.append(txt);
					} else {
						sb.append(txt.substring(0, txt.indexOf(LINK_HEAD)));
					}
				}
			} else {
				sb.append(txt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 给文章内容关键词添加超链接(添加方法)
	 */
	private void addContentWords(ContentTxt txt){
		if (txt != null){
			//1.把文章中手工加超链接部分取出来
			String base = "___aaa___";
			Map<String,String> maps = new HashMap<String,String>();
			StringBuilder sb = new StringBuilder();
			String str = txt.getTxt();
			//这里先把所有超链接及内容放到map中
			if (str.indexOf("<a") > -1){
				boolean flag = true;
				int count = 0;
				while (flag){
					int startLocal = str.indexOf("<a");
					sb.append(str.substring(0,startLocal));
					int endLocal = str.indexOf("</a>")+4;
					String val = str.substring(startLocal,endLocal);
					maps.put(base+count,val);
					sb.append(base+count);
					str = str.substring(endLocal);
					count++;
					if (str.indexOf("<a") == -1){
						flag = false;
						sb.append(str);
					}
				}
			}
			if (sb.length() > 0){
				str = sb.toString();
			}
			txt.setTxt(str);
			//2.给文章关键词加超链接
			if (!ChannelCacheUtils.contentWordsMap.isEmpty()){
				for (Integer key : ChannelCacheUtils.contentWordsMap.keySet()){
					CmsKeyword  keyword = ChannelCacheUtils.contentWordsMap.get(key);
					if (txt.getTxt().contains(keyword.getName())){
						if (keyword.getLongTail() != null){
							String[] ids = keyword.getLongTail().split(",");
							boolean flag = false;
							for (String id : ids){
								int idi = Integer.parseInt(id);
								CmsKeyword cKeyword = ChannelCacheUtils.contentWordsMap.get(idi);
								if (cKeyword != null){
									if (cKeyword.getIsUse() != null && cKeyword.getIsUse() == 1){
										flag = true;
										break;
									}
								}
							}
							if (!flag){
								txt.setTxt(replaceKeyword(txt.getTxt(),keyword.getName(),keyword.getUrl()));
								ChannelCacheUtils.contentWordsMap.get(key).setIsUse(1);
							}
						}else{
							txt.setTxt(replaceKeyword(txt.getTxt(),keyword.getName(),keyword.getUrl()));
							ChannelCacheUtils.contentWordsMap.get(key).setIsUse(1);
						}
					}
				}
			}
			clearCacheKeywordsUseStatus();
			//3.还原文章中手工加超链接的部分
			str = txt.getTxt();
			if (!maps.isEmpty()){
				for (String key : maps.keySet()){
					str = str.replaceFirst(key,maps.get(key));
				}
				txt.setTxt(str);
			}
		}
	}
	
	/**
	 * 给文章内容关键词添加超链接(修改方法)
	 */
	private void updateContentWords(ContentTxt txt){
		if (txt != null){
			//1.把文章中手工加超链接部分取出来
			String base = "aabbcde";
			Map<String,String> maps = new HashMap<String,String>();
			StringBuilder sb = new StringBuilder();
			String str = txt.getTxt();
			//这里先把所有超链接及内容放到map中
			if (str.indexOf("<a") > -1){
				boolean flag = true;
				int count = 0;
				while (flag){
					int startLocal = str.indexOf("<a");
					sb.append(str.substring(0,startLocal));
					int endLocal = str.indexOf("</a>")+4;
					String val = str.substring(startLocal,endLocal);
					maps.put(base+count,val);
					sb.append(base+count);
					str = str.substring(endLocal);
					count++;
					if (str.indexOf("<a") == -1){
						flag = false;
						sb.append(str);
					}
				}
			}
			if (sb.length() > 0){
				str = sb.toString();
			}
			txt.setTxt(str);
			
			//2.给文章关键词加超链接
			if (!ChannelCacheUtils.contentWordsMap.isEmpty()){
				for (Integer key : ChannelCacheUtils.contentWordsMap.keySet()){
					CmsKeyword  keyword = ChannelCacheUtils.contentWordsMap.get(key);
					int local = txt.getTxt().indexOf(keyword.getName());
					if (local == 1){				//在第一个位置,需要替换
						txt.getTxt().replace(keyword.getName(),keyword.getUrl());
						ChannelCacheUtils.contentWordsMap.get(key).setIsUse(1);
					}else if (local > 1 && local < txt.getTxt().length()){			//在内容里面,需要进一下判断关键词前面是否有">"尖括号,说明是否已经加过超链接
						String specialChar = txt.getTxt().substring(local - 1, local);
						int endLocal = local + keyword.getName().length();
						String specialTwo = txt.getTxt().substring(endLocal, endLocal+1);
						//System.out.println("----- find key["+keyword.getId()+"]   ,  specialChar["+local+"]   ,   specialTwo["+endLocal+"]   -----");
						if (!">".equals(specialChar) && !"<".equals(specialTwo)
								&& !"=".equals(specialChar) && !"\"".equals(specialTwo)){
							if (keyword.getLongTail() != null){
								String[] ids = keyword.getLongTail().split(",");
								boolean flag = false;
								for (String id : ids){
									int idi = Integer.parseInt(id);
									
									CmsKeyword cKeyword = ChannelCacheUtils.contentWordsMap.get(idi);
									if (cKeyword != null){
										if (cKeyword.getIsUse() != null && cKeyword.getIsUse() == 1){
											flag = true;
											break;
										}
									}
								}
								if (!flag){
									txt.setTxt(replaceKeyword(txt.getTxt(),keyword.getName(),keyword.getUrl()));
									ChannelCacheUtils.contentWordsMap.get(key).setIsUse(1);
								}
							}else{
								txt.setTxt(replaceKeyword(txt.getTxt(),keyword.getName(),keyword.getUrl()));
								ChannelCacheUtils.contentWordsMap.get(key).setIsUse(1);
							}
						}
					}
				}
			}
			clearCacheKeywordsUseStatus();
			//3.还原文章中手工加超链接的部分
			str = txt.getTxt();
			if (!maps.isEmpty()){
				for (String key : maps.keySet()){
					str = str.replaceFirst(key,maps.get(key));
				}
				txt.setTxt(str);
			}
		}
	}
	
	/**
	 * 替换关键词
	 */
	private String replaceKeyword(String old,String keyword,String url){
		String result = "";
		String nUrl = "<a href=\""+url+"\" class=\"link-bottom\" target=\"_blank\">"+keyword+"</a>";
		int imgLocal = old.indexOf("<img");
		if (imgLocal > -1){
			//如果正文中包含图片，则图片的alt内容不能被替换为链接形式
			String oldtxt = "";
			while(imgLocal > -1){
				String linshi = old.substring(0,imgLocal);
				old = old.substring(imgLocal);
				if (linshi.indexOf(keyword) > -1){
					linshi = linshi.replaceFirst(keyword,nUrl);
				}
				oldtxt += linshi;
				oldtxt += old.substring(0,old.indexOf("src=\"")+5);
				old = old.substring(old.indexOf("src=\"")+5);
				String canshu = old.substring(0,old.indexOf("\""));
				old = old.substring(old.indexOf("\""));
				if (canshu.indexOf("?") > -1){
					canshu = canshu.substring(0,canshu.indexOf("?"));
				}
				oldtxt += canshu;
				oldtxt += old.substring(old.indexOf("\""),old.indexOf("/>")+2);
				old = old.substring(old.indexOf("/>")+2);
				imgLocal = old.indexOf("<img");
			}
			oldtxt += old.replaceFirst(keyword,nUrl);
			result = oldtxt;
		}else{
			result = old.replaceFirst(keyword,nUrl);
		}
		return result;
	}
	
	/*
	 * 清除内存缓存中关键词使用状态
	 */
	private void clearCacheKeywordsUseStatus(){
		if (!ChannelCacheUtils.contentWordsMap.isEmpty()){
			for (Integer key : ChannelCacheUtils.contentWordsMap.keySet()){
				ChannelCacheUtils.contentWordsMap.get(key).setIsUse(0);
			}
		}
	}
	
	/**
	 * 把关键词转为拼音
	 */
	public String cachePinYin(String tagstrs){
		StringBuilder sb = new StringBuilder();
		tagstrs = Constant.replaceManySpace(tagstrs);
		String[] tags = tagstrs.split(" ");
		Jedis jedis = null;
		try {
			jedis = Constant.getJedis();
			for (String tag : tags) {
				String py = PingyinUtil.getPingYin(tag);
				if (jedis != null) {
					jedis.set(py, tag); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
					sb.append(tag).append("_").append(py).append(" ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Constant.returnResource(jedis);
		}
		if (sb.length() > 0){
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * 把作者转为拼音
	 */
	public void cacheAuthor(String author,String authorPy){
		Jedis jedis = null;
		try {
			jedis = Constant.getJedis();
			jedis.set(authorPy,author);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Constant.returnResource(jedis);
		}
	}
	
	/**
	 * 保存手机图片路径
	 */
	public String saveMobileImagePath(String filePath){
		StringBuilder sb = new StringBuilder();
		int local = filePath.indexOf("/u/");
		sb.append(filePath.substring(0,local));
		sb.append(Constants.MOBILE);
		sb.append(filePath.substring(local));
		return sb.toString();
	}
	
	/**
	 * 生成手机端缩略图
	 */
	public void generatePhoneFirstThumb(String filePath){
		//找到源文件路径
		String local = getClass().getResource("/").getFile().toString();
		String rootLocal = local.substring(0,local.indexOf("WEB-INF"));
		filePath = filePath.substring(filePath.indexOf("/u/"));
		String srcPath = rootLocal + filePath;
		String targetPath = rootLocal + Constants.MOBILE + filePath;
		String cc = ChannelCacheUtils.thumbMap.get("phone_first_list");
		String[] ccs = cc.split("_");
		int width = Integer.parseInt(ccs[0]);
		int height = Integer.parseInt(ccs[1]);
		try {
			String houzhi = filePath.substring(filePath.indexOf(".")+1);
			Constant.imageCutUtil.saveImageAsJpg(srcPath, targetPath, width, height,houzhi);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询作者是否为专家
	 */
	public boolean confirmExpert(String author){
		boolean result = false;
		Map<String,Expert> expertMap = ChannelCacheUtils.exoertMap;
		if (!expertMap.isEmpty()){
			for (String key : expertMap.keySet()){
				if (author.equals(key)){
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 查找是否属于数据猿精品活动
	 */
	public Integer findMySport(String title){
		Integer mySport = 0;
		try {
			if (!ChannelCacheUtils.jingPinSportMap.isEmpty()) {
				for (String key : ChannelCacheUtils.jingPinSportMap.keySet()){
					Integer val = ChannelCacheUtils.jingPinSportMap.get(key);
					if (title.indexOf(key) > -1){
						mySport = val;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mySport;
	}
	
	/**
	 * 获取新增的分类
	 */
	public Integer[] getInsertMoreChannels(Integer[] moreChannels,String moreChannelsh){
		List<Integer> lists = new ArrayList<Integer>();
		String[] str2s = moreChannelsh.split(",");
		boolean insertFlag = false;
		for (Integer s1 : moreChannels){
			insertFlag = false;
			for (String s2 : str2s){
				Integer s22 = Integer.parseInt(s2);
				if (s1 == s22){
					insertFlag = true;
					break;
				}
			}
			if (!insertFlag){
				lists.add(s1);
			}
		}
		Integer[] results = new Integer[lists.size()];
		for (int i=0;i<lists.size();i++){
			results[i] = lists.get(i);
		}
		return results;
	}
	
	/**
	 * 获取找出删除的分类ID
	 */
	public Integer[] getDeleteMoreChannels(Integer[] moreChannels,String moreChannelsh,String moreContentIds){
		List<Integer> lists = new ArrayList<Integer>();
		String[] str2s = moreChannelsh.split(",");
		String[] mids = moreContentIds.split(",");
		boolean deleteFlag = false;
		int count = 0;
		for (String s2 : str2s){
			deleteFlag = false;
			Integer s22 = Integer.parseInt(s2);
			for (Integer s1 : moreChannels){
				if (s1 == s22){
					deleteFlag = true;
					break;
				}
			}
			if (!deleteFlag){
				lists.add(Integer.parseInt(mids[count]));
			}
			count++;
		}
		Integer[] results = new Integer[lists.size()];
		for (int i=0;i<lists.size();i++){
			results[i] = lists.get(i);
		}
		return results;
	}
	
	/**
	 * 找出更新的分类
	 */
	public List<Integer[]> getUpdateMoreChannels(Integer[] moreChannels,String moreChannelsh,String moreContentIds){
		List<Integer[]> results = new ArrayList<Integer[]>();
		List<Integer> channelLists = new ArrayList<Integer>();
		List<Integer> contentIdLists = new ArrayList<Integer>();
		String[] str2s = moreChannelsh.split(",");
		String[] str3s = moreContentIds.split(",");
		boolean updateFlag = false;
		int count = 0;
		for (String s2 : str2s){
			updateFlag = false;
			Integer s22 = Integer.parseInt(s2);
			for (Integer s1 : moreChannels){
				if (s22 == s1){
					updateFlag = true;
					break;
				}
			}
			if (updateFlag){
				channelLists.add(Integer.parseInt(s2));
				contentIdLists.add(Integer.parseInt(str3s[count]));
			}
			count++;
		}
		Integer[] channelIs = new Integer[channelLists.size()];
		Integer[] contentIdIs = new Integer[contentIdLists.size()];
		for (int i=0;i<channelLists.size();i++){
			channelIs[i] = channelLists.get(i);
		}
		for (int j=0;j<contentIdLists.size();j++){
			contentIdIs[j] = contentIdLists.get(j);
		}
		results.add(channelIs);
		results.add(contentIdIs);
		return results;
	}
	
	/**
	 * 多分类添加文章
	 */
	public String[] osaveArticle(Content bean, ContentExt ext, ContentTxt txt, Integer meetingId, Integer siteLocation,Integer topicType,
			Boolean copyimg,Integer sendType,Integer selectImg,String weixinImg,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Integer channelId, Integer typeId, String tagStr, Boolean draft,Integer[] moreChannels,String bigPictureImg,String renwuBigPictureImg,String xuetangBigPictureImg,String attr_redirecctUrl,
			Integer cid, CmsSite site,CmsUser user,HttpServletRequest request){
		String[] results = new String[2];
		// 加上模板前缀
		String tplPath = site.getTplPath();
		String releaseMoreCategory = "";
		String releaseMoreId = "";
		Content beanbak = CloneUtil.clone(bean);
		ContentExt extbak = CloneUtil.clone(ext);
		ContentTxt txtbak = CloneUtil.clone(txt);
		if (moreChannels.length > 0){
			for (Integer channelid : moreChannels){
				bean = CloneUtil.clone(beanbak);
				ext = CloneUtil.clone(extbak);
				txt = CloneUtil.clone(txtbak);
				channelId = channelid;
				bean.setSite(site);
				if (channelId == 70){
					ext.setBigPictureImg(bigPictureImg);
					//判断首页是否有大图
					if (!"0".equals(bean.getBigPictureLocal()) && bean.getBigPictureLocal().length() > 0){
						bean.setIsBigPicture(1);
						bean.setArticle("首页大图");
						bean.setClassify(bean.getBigPictureLocal());
					}else{
						bean.setIsBigPicture(0);
					}
					if (attr_redirecctUrl != null){
						ext.setSendUrl(attr_redirecctUrl);
					}
				}else if (channelId == 74){			//活动推荐
					bean.setIsBigPicture(0);
				}else if (channelId == 75){			//人物专访
					ext.setBigPictureImg(renwuBigPictureImg);
					bean.setIsBigPicture(0);
				}else if (channelId == 76){		//大数据学堂
					ext.setBigPictureImg(xuetangBigPictureImg);
					//判断大数据学堂分类
					if (!"0".equals(bean.getXueTangCatory())){
						bean.setArticle("大数据学堂");
						bean.setClassify(bean.getXueTangCatory());
					}
					bean.setIsBigPicture(0);
				}else if (channelId == 85){		//指数排行
					//判断是指数排行
					if (!"0".equals(bean.getIndexrankLocal())){
						bean.setIsBigPicture(2);
						bean.setArticle("指数排行大图");
						bean.setClassify(bean.getIndexrankLocal());
						ext.setBigPictureImg(bean.getIndexrankImg());
					}
					bean.setIsBigPicture(0);
				}else{
					bean.setIsBigPicture(0);
				}
				
				if (!StringUtils.isBlank(ext.getTplContent())) {
					ext.setTplContent(tplPath + ext.getTplContent());
				}
				bean.setAttr(RequestUtils.getRequestMap(request, "attr_"));
				String[] tagArr = StrUtils.splitAndTrim(tagStr, " ", MessageResolver
						.getMessage(request, "content.tagStr.split"));
				if(txt!=null&&copyimg!=null&&copyimg){
					txt=copyContentTxtImg(txt, site);
				}
				
				if (bean.getRecommandView() == null){
					bean.setRecommandView(0);
				}
				if (bean.getRecommendInterview() == null){
					bean.setRecommendInterview(0);
				}
				if (bean.getMySport() == null){
					bean.setMySport(findMySport(ext.getTitle()));
				}
				bean.setIsPushBaidu(0);
				
				//图片是否置顶
				if ("置顶".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(2);
				}else if ("推广".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(1);
				}else if ("日常内容".equals(bean.getAttr_selPictureLocal())){
					bean.setIsPictureTop(0);
				}else{
					bean.setIsPictureTop(0);
				}
				//判断文章是否显示
				if (bean.getAttr_status() != null){
					if ("yes".equals(bean.getAttr_status())){
						bean.setStatus(Byte.parseByte("2"));
					}else if ("no".equals(bean.getAttr_status())){
						bean.setStatus(Byte.parseByte("0"));
					}
				}
				
				//给活动日期赋值
				if (bean.getAttr_sportDate() != null){
					bean.setSportDate(bean.getAttr_sportDate());
				}
				
				//给文章内容添加关键词超链接
				addContentWords(txt);
				//如果包含有腾讯视频(v.qq.com),需要单独把视频地址保留下来,方便以后自动更新引用地址
				saveTengxunVideoUrl(txt);
				//这里把内容txt表内容复制到txt1字段一份，方便页面视频播放
				copyTxt(txt);
				
				//把TAG标签保存到jc_content_ext表中
				//ext.setTagConent(tagStr);
				if (tagStr != null && tagStr.length() > 0){
					String tagStrs = cachePinYin(tagStr);
					ext.setTagConent(tagStrs);
				}
				
				if (ext.getContentMobileImg() == null){
					if (channelId != 71 && channelId != 79 && channelId != 82){
						//保存手机图片路径
						ext.setContentMobileImg(saveMobileImagePath(ext.getContentImg()));
						//这里需要生成手机版缩略图
						generatePhoneFirstThumb(ext.getContentImg());
					}
				}
				
				//判断作者是否为专家,如果是：在jc_content表的isExpert字段置1(这个字段方便查询按所有专家文章倒序)
				if (bean.getIsExpert() == null){
					if (channelId != 79 && channelId != 82){							//如果为热门招聘则不需要判断是否为专家
						if (confirmExpert(ext.getAuthor())){
							bean.setIsExpert(1);
						}else{
							bean.setIsExpert(0);
						}
					}else{
						bean.setIsExpert(0);
					}
				}
				
				//自动给作者添加拼音
				if (ext.getAuthorPy() == null){
					if (ext.getAuthor() != null){
						String authorPy = PingyinUtil.getPingYin(ext.getAuthor());
						ext.setAuthorPy(authorPy);
						cacheAuthor(ext.getAuthor(),ext.getAuthorPy());
					}
				}
				
				bean = manager.save(bean, ext, txt,channelIds, topicIds, viewGroupIds,
						tagArr, attachmentPaths, attachmentNames, attachmentFilenames,
						picPaths, picDescs, channelId, typeId, draft,false, user, false,meetingId,siteLocation,topicType);
				
				releaseMoreCategory += "".equals(releaseMoreCategory)?""+bean.getChannel().getId():","+bean.getChannel().getId();
				releaseMoreId += "".equals(releaseMoreId)?""+bean.getId():","+bean.getId();
				
				//处理附件
				fileMng.updateFileByPaths(attachmentPaths,picPaths,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
				log.info("save Content id={}", bean.getId());
				cmsLogMng.operating(request, "content.log.save", "id=" + bean.getId()
						+ ";title=" + bean.getTitle());
			}
			results[0] = releaseMoreCategory;
			results[1] = releaseMoreId;
		}
		return results;
	}
	
	/**
	 * 删除多分类文章
	 */
	public void osdeleteArticle(Integer[] oids){
		if (oids != null && oids.length > 0){
			for (Integer oid : oids){
				manager.deleteById(oid);
			}
		}
	}
	
	/**
	 * 多分类更新文章
	 */
	public String[] oupdateArticle(String queryStatus, Integer queryTypeId,CmsSpecialTopicContent topicContent,
			Integer meetingId, Integer siteLocation,Integer specialTopicContentId,String subPlaceStr,
			Boolean queryTopLevel, Boolean queryRecommend,
			Integer queryOrderBy, Content bean, ContentExt ext, ContentTxt txt,
			Boolean copyimg,Integer sendType,Integer selectImg,String weixinImg,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths,String[] picDescs,
			Integer channelId, Integer typeId, String tagStr, Boolean draft,
			Integer cid,String[]oldattachmentPaths,String[] oldpicPaths,
			String oldTitleImg,String oldContentImg,String oldTypeImg,Integer[] moreChannels,String moreContentIds,String bigPictureImg,
			String renwuBigPictureImg,String xuetangBigPictureImg,String moreChannelsh,String contentImgCompare,String attr_redirecctUrl,
			Integer pageNo, HttpServletRequest request,ModelMap model,String[] releaseMores,CmsSite site,CmsUser user,Integer[] updateMoreChannels){
		String tplPath = site.getTplPath();
		Content beanbak = CloneUtil.clone(bean);
		ContentExt extbak = CloneUtil.clone(ext);
		ContentTxt txtbak = CloneUtil.clone(txt);
		String tagStrBak = tagStr;
		String releaseMoreCategory = releaseMores[0]==null?"":releaseMores[0];
		String releaseMoreId = releaseMores[1]==null?"":releaseMores[1];
		
		if (updateMoreChannels.length > 0){
			for (int ch=0;ch<updateMoreChannels.length;ch++){
				Integer channelid = moreChannels[ch];
				Integer contentid = updateMoreChannels[ch];
				
				bean = CloneUtil.clone(beanbak);
				ext = CloneUtil.clone(extbak);
				txt = CloneUtil.clone(txtbak);
				tagStr = tagStrBak;
				channelId = channelid;
				bean.setSite(site);
				
				//Content contentZH = manager.findById(contentid);
				//重新赋值文章ID和分类
				bean.setId(contentid);
				//bean.setChannel(contentZH.getChannel());
				ext.setId(contentid);
				txt.setId(contentid);
				
				if (channelId == 70){
					ext.setBigPictureImg(bigPictureImg);
					//判断是否修改为首页大图
					if (!"0".equals(bean.getBigPictureLocal()) && bean.getBigPictureLocal().length() > 0){
						bean.setIsBigPicture(1);
						bean.setArticle("首页大图");
						bean.setClassify(bean.getBigPictureLocal());
					}else{
						if (channelId == 70){
							bean.setIsBigPicture(0);
							bean.setArticle(null);
							bean.setClassify(null);
							ext.setBigPictureImg(null);
						}else{
							bean.setIsBigPicture(0);
							bean.setArticle(null);
							bean.setClassify(null);
						}
					}
					if (attr_redirecctUrl != null && attr_redirecctUrl.length() > 0){
						ext.setSendUrl(attr_redirecctUrl);
					}else{
						ext.setSendUrl("");
					}
					//图片是否置顶
					if ("置顶".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(2);
					}else if ("推广".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(1);
					}else if ("日常内容".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(0);
					}
					
					//判断指数排行
					if (!"0".equals(bean.getIndexrankLocal()) && bean.getIndexrankLocal().length() > 0){
						bean.setIsBigPicture(2);
						bean.setArticle("指数排行大图");
						bean.setClassify(bean.getIndexrankLocal());
						ext.setBigPictureImg(bean.getIndexrankImg().split(",")[0]);
					}
				}else if (channelId == 74){			//活动推荐
					bean.setIsBigPicture(0);
				}else if (channelId == 75){			//人物专访
					ext.setBigPictureImg(renwuBigPictureImg);
					bean.setArticle(null);
					bean.setClassify(null);
					//图片是否置顶
					if ("置顶".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(2);
					}else if ("推广".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(1);
					}else if ("日常内容".equals(bean.getAttr_selPictureLocal())){
						bean.setIsPictureTop(0);
					}
				}else if (channelId == 76){		//大数据学堂
					ext.setBigPictureImg(xuetangBigPictureImg);
					//判断大数据学堂分类
					if (!"0".equals(bean.getXueTangCatory())){
						bean.setArticle("大数据学堂");
						bean.setClassify(bean.getXueTangCatory());
					}
					
					if (bean.getXueTangFL() != null){
						bean.setArticle("大数据学堂");
						bean.setClassify(bean.getXueTangFL());
					}
				}else if (channelId == 85){		//指数排行
					//判断指数排行
					if (!"0".equals(bean.getIndexrankLocal())){
						bean.setIsBigPicture(2);
						bean.setArticle("指数排行大图");
						bean.setClassify(bean.getIndexrankLocal());
						ext.setBigPictureImg(bean.getIndexrankImg().split(",")[1]);
					}
				}
				
				if (channelId == 71){
					if (txt.getTxt() == null){
						txt.setTxt(txt.getTxt1());
					}
				}
				if (!StringUtils.isBlank(ext.getTplContent())) {
					ext.setTplContent(tplPath + ext.getTplContent());
				}
				String[] tagArr = StrUtils.splitAndTrim(tagStr, " ", MessageResolver
						.getMessage(request, "content.tagStr.split"));
				Map<String, String> attr = RequestUtils.getRequestMap(request, "attr_");
				if(txt!=null&&copyimg!=null&&copyimg){
					txt=copyContentTxtImg(txt, site);
				}
				if (bean.getRecommendInterview() == null){
					bean.setRecommendInterview(0);
				}
				//if (bean.getMySport() == null){
					bean.setMySport(findMySport(ext.getTitle()));
				//}
				try{
					//判断文章是否显示
					if (bean.getAttr_status() != null){
						if ("yes".equals(bean.getAttr_status())){
							bean.setStatus(Byte.parseByte("2"));
						}else if ("no".equals(bean.getAttr_status())){
							bean.setStatus(Byte.parseByte("0"));
						}
					}
					if (ext.getReleaseDate() != null){
						bean.setSortDate(ext.getReleaseDate());
					}
					if (bean.getAttr_sportDate() != null){
						bean.setSportDate(bean.getAttr_sportDate());
					}
					//给文章内容关键词添加超链接
					updateContentWords(txt);
					//如果包含有腾讯视频(v.qq.com),需要单独把视频地址保留下来,方便以后自动更新引用地址
					saveTengxunVideoUrl(txt);
					//这里把内容txt表内容复制到txt1字段一份，方便页面视频播放
					copyTxt(txt);
					
					//把TAG标签保存到jc_content_ext表中
					//ext.setTagConent(tagStr);
					if (tagStr != null && tagStr.length() > 0){
						tagStr = cachePinYin(tagStr);
						ext.setTagConent(tagStr);
					}
					if (channelId != 71 && channelId != 79 && channelId != 82){
						if (!contentImgCompare.equals(ext.getContentImg())){
							//保存手机图片路径
							ext.setContentMobileImg(saveMobileImagePath(ext.getContentImg()));
							//这里需要生成手机版缩略图
							generatePhoneFirstThumb(ext.getContentImg());
						}
					}
					
					//判断作者是否为专家,如果是：在jc_content表的isExpert字段置1(这个字段方便查询按所有专家文章倒序)
					if (channelId != 79 && channelId != 82){							//如果为热门招聘则不需要判断是否为专家
						if (confirmExpert(ext.getAuthor())){
							bean.setIsExpert(1);
						}else{
							bean.setIsExpert(0);
						}
					}else{
						bean.setIsExpert(0);
					}
					
					//自动给作者添加拼音
					if (ext.getAuthor() != null){
						String authorPy = PingyinUtil.getPingYin(ext.getAuthor());
						ext.setAuthorPy(authorPy);
						cacheAuthor(ext.getAuthor(),ext.getAuthorPy());
					}
					
					bean = manager.update(bean, ext, txt,tagArr, channelIds, topicIds,
							viewGroupIds, attachmentPaths, attachmentNames,
							attachmentFilenames, picPaths, picDescs, attr, channelId,
							typeId, draft, user, false,queryStatus,queryTypeId,topicContent,meetingId,siteLocation,specialTopicContentId,subPlaceStr,queryTopLevel,queryRecommend,queryOrderBy);
					releaseMoreCategory += "".equals(releaseMoreCategory)?""+bean.getChannel().getId():","+bean.getChannel().getId();
					releaseMoreId += "".equals(releaseMoreId)?""+bean.getId():","+bean.getId();
				}catch(Exception e){
					System.out.println("-------------------update article is error:   -------------------------");
					e.printStackTrace();
				}
				
				
				//微信消息发送
				//weiXinSvc.sendMessage(sendType, selectImg, weixinImg, bean, ext, txt);
				//处理之前的附件有效性
				fileMng.updateFileByPaths(oldattachmentPaths,oldpicPaths,null,oldTitleImg,oldTypeImg,oldContentImg,false,bean);
				//处理更新后的附件有效性
				fileMng.updateFileByPaths(attachmentPaths,picPaths,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
				log.info("update Content id={}.", bean.getId());
				cmsLogMng.operating(request, "content.log.update", "id=" + bean.getId()
						+ ";title=" + bean.getTitle());
			}
		}
		releaseMores[0] = releaseMoreCategory;
		releaseMores[1] = releaseMoreId;
		return releaseMores;
	}
	
	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsModelMng cmsModelMng;
	@Autowired
	private CmsModelItemMng cmsModelItemMng;
	@Autowired
	private CmsTopicMng cmsTopicMng;
	@Autowired
	private CmsGroupMng cmsGroupMng;
	@Autowired
	private ContentTypeMng contentTypeMng;
	@Autowired
	private TplManager tplManager;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private DbFileMng dbFileMng;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private ContentMng manager;
	@Autowired
	private CmsFileMng fileMng;
	@Autowired
	private CmsSiteMng siteMng;
	@Autowired
	private ImageSvc imageSvc;
	@Autowired
	private WeiXinSvc weiXinSvc;
	@Autowired
	private ExpertMng expertMng;
	@Autowired
	private CmsSpecialTopicMng cmsSpecialTopicMng;
	@Autowired
	private CmsSpecialTopicContentMng cmsSpecialTopicContentMng;
}