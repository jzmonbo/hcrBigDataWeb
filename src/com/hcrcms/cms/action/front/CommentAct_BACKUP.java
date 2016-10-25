package com.hcrcms.cms.action.front;

import static com.hcrcms.cms.Constants.TPLDIR_CSI;
import static com.hcrcms.cms.Constants.TPLDIR_SPECIAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.main.ChannelExt;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.manager.assist.CmsCommentMng;
import com.hcrcms.cms.manager.assist.CmsIpAddressMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.common.web.RequestUtils;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.common.web.session.SessionProvider;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserExt;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.FrontUtils;
import com.hcrcms.core.web.util.RandomUtils;
import com.hcrcms.core.web.util.RsortComment;
import com.hcrcms.core.web.util.SortComment;
import com.octo.captcha.service.image.ImageCaptchaService;

@Controller
public class CommentAct_BACKUP {
	private static final Logger log = LoggerFactory.getLogger(CommentAct_BACKUP.class);

	public static final String COMMENT_PAGE = "tpl.commentPage";
	public static final String COMMENT_LIST = "tpl.commentList";

	@RequestMapping(value = "/comment*.jspx", method = RequestMethod.GET)
	public String page(Integer contentId, Integer pageNo,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if(contentId==null){
			return FrontUtils.showMessage(request, model,
			"comment.contentNotFound");
		}
		Content content = contentMng.findById(contentId);
		if (content == null) {
			return FrontUtils.showMessage(request, model,
					"comment.contentNotFound");
		}
		if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_OFF) {
			return FrontUtils.showMessage(request, model, "comment.closed");
		}
		// 将request中所有参数保存至model中。
		model.putAll(RequestUtils.getQueryParams(request));
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_SPECIAL, COMMENT_PAGE);
	}

	@RequestMapping(value = "/comment_list.jspx")
	public String list(Integer siteId, Integer contentId, Integer greatTo,
			Integer recommend, Integer checked, Integer orderBy, Integer count,String userIp,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		if (count == null || count <= 0 || count > 200) {
			count = 200;
		}
		boolean desc, rec;
		if (orderBy == null || orderBy == 0) {
			desc = true;
		} else {
			desc = false;
		}
		if (recommend == null || recommend == 0) {
			rec = false;
		} else {
			rec = true;
		}
		Boolean chk;
		if (checked != null) {
			chk = checked != 0;
		} else {
			chk = null;
		}
		List<CmsComment> list = cmsCommentMng.getListForTag(siteId, contentId,
				greatTo, chk, rec, desc, count);
		// 将request中所有参数
		model.putAll(RequestUtils.getQueryParams(request));
		CmsUser user = CmsUtils.getUser(request);
		/*if (user == null){
			if (list != null && list.size() > 0){
				String  userName = "";
				long ipNum = ConvertIpUtils.iptolong(String.valueOf(model.get("userIp")));
				//查找IP库里是否有该IP,如果有就写某某网友，如果没有就写匿名用户
				BigDecimal bigDecimal = new BigDecimal(""+ipNum);
				List<CmsIpAddress> ipAddresss = cmsIpAddressMng.getList(bigDecimal);
				if (ipAddresss != null && ipAddresss.size() > 0){
					userName = ipAddresss.get(0).getCity()+"的网友";
				}else{
					userName = "匿名的网友";
				}
				for (CmsComment comment : list){
					if (comment.getCommentUser() == null){
						user = new CmsUser();
						user.setId(999999);
						user.setUsername(userName);
						comment.setCommentUser(user);
					}
				}
			}
		}*/
		
		/*if (list != null && list.size() > 0){
			for (CmsComment comment : list){
				if (comment.getCommentUser() == null){
					String  userName = "";
					//long ipNum = ConvertIpUtils.iptolong(String.valueOf(model.get("userIp")));     通过接口获取用户IP来判断是哪里用户
					//查找IP库里是否有该IP,如果有就写某某网友，如果没有就写匿名用户
					//BigDecimal bigDecimal = new BigDecimal(""+ipNum);
					double result = RandomUtils.getProvinceRandom();
					List<CmsIpAddress> ipAddresss = cmsIpAddressMng.getList(result);
					if (ipAddresss != null && ipAddresss.size() > 0){
						userName = ipAddresss.get(0).getCity()+"的网友";
					}else{
						userName = "匿名的网友";
					}
					CmsUser cUser = new CmsUser();
					//cUser.setId(999999);
					cUser.setUsername(userName);
					CmsUserExt cExt = new CmsUserExt();
					cExt.setUserImg("/hcrBigDataWeb/r/cms/www/default/headPic/pic"+RandomUtils.getHeadRandom()+".jpg");
					Set<CmsUserExt> userExt = new HashSet<CmsUserExt>();
					userExt.add(cExt);
					cUser.setUserExtSet(userExt);
					comment.setCommentUser(cUser);
				}
			}
		}*/
		
		//给回复做排序
		if (list != null && list.size() > 0){
			SortComment sortComment = new SortComment();
			RsortComment rsortComment = new RsortComment();
			for (CmsComment comment : list){
				List<CmsCommentExt> exts = new ArrayList<CmsCommentExt>(comment.getCommentExts());
				Collections.sort(exts,rsortComment);
				Collections.reverse(exts);
				if (exts != null && exts.size() > 0){
					List<List<CmsCommentExt>> sortExts = new ArrayList<List<CmsCommentExt>>();
					int rSort = -1;
					List<CmsCommentExt> optionExts = new ArrayList<CmsCommentExt>();
					for (CmsCommentExt commentExt : exts){
						if (commentExt.getReply() == null){
							optionExts.add(commentExt);
						}else{
							if (rSort == -1){
								rSort = commentExt.getRsort();
								optionExts.clear();
								optionExts.add(commentExt);
							}else{
								if (rSort == commentExt.getRsort()){
									rSort = commentExt.getRsort();
									optionExts.add(commentExt);
								}else{
									List<CmsCommentExt> optionTwoExts = new ArrayList<CmsCommentExt>();
									optionTwoExts.addAll(optionExts);
									Collections.sort(optionTwoExts,sortComment);
									sortExts.add(optionTwoExts);
									rSort = commentExt.getRsort();
									optionExts.clear();
									optionExts.add(commentExt);
								}
							}
						}
					}
					if (optionExts != null && optionExts.size() > 0){
						Collections.sort(optionExts,sortComment);
						sortExts.add(optionExts);
					}
					comment.setCommentLists(sortExts);
				}
				comment.setCommentContent(exts.get(0).getText());
				model.put("isReply",exts.get(0).getReply()==null?0:1);
				
				if (comment.getCommentUser() == null) {
					CmsUser cmsUser = new CmsUser();
					//cmsUser.setId(999999);
					String amousImg = "/hcrBigDataWeb/r/cms/www/default/headPic/pic"+RandomUtils.getHeadRandom()+".jpg";
					String anonymousName = "";
					double result = RandomUtils.getProvinceRandom();
					List<CmsIpAddress> ipAddresss = cmsIpAddressMng.getList(result);
					if (ipAddresss != null && ipAddresss.size() > 0){
						anonymousName = ipAddresss.get(0).getCity()+"的网友";
					}else{
						anonymousName = "匿名的网友";
					}
					cmsUser.setUsername(anonymousName);
					CmsUserExt cmsUserExt = new CmsUserExt();
					cmsUserExt.setUserImg(amousImg);
					Set set = new HashSet();
					set.add(cmsUserExt);
					cmsUser.setUserExtSet(set);
					comment.setCommentUser(cmsUser);
				}
			}
		}
		
		model.addAttribute("list", list);
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_CSI, COMMENT_LIST);
	}

	@RequestMapping(value = "/comment.jspx", method = RequestMethod.POST)
	public void submit(Integer contentId, Integer score,String text, String captcha,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (contentId == null) {
			json.put("success", false);
			json.put("status", 100);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		if (StringUtils.isBlank(text)) {
			json.put("success", false);
			json.put("status", 101);
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		/*if (user == null || user.getGroup().getNeedCaptcha()) {
			// 验证码错误
			try {
				if (!imageCaptchaService.validateResponseForID(session
						.getSessionId(request, response), captcha)) {
					json.put("success", false);
					json.put("status", 1);
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			} catch (CaptchaServiceException e) {
				json.put("success", false);
				json.put("status", 1);
				log.warn("", e);
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
		}
		Content content = contentMng.findById(contentId);
		if (content == null) {
			// 内容不存在
			json.put("success", false);
			json.put("status", 2);
		} else if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_OFF) {
			// 评论关闭
			json.put("success", false);
			json.put("status", 3);
		} else if (content.getChannel().getCommentControl() == ChannelExt.COMMENT_LOGIN
				&& user == null) {
			// 需要登录才能评论
			json.put("success", false);
			json.put("status", 4);
		}else if (hasCommented(user, content)) {
			// 已经评论过，不能重复评论
			json.put("success", false);
			json.put("status", 5);
		} else {
			boolean checked = false;
			Integer userId = null;
			if (user != null) {
				checked = !user.getGroup().getNeedCheck();
				userId = user.getId();
			}
			cmsCommentMng.comment(score,text, RequestUtils.getIpAddr(request),
					contentId, site.getId(), userId, checked, false);
			json.put("success", true);
			json.put("status", 0);
		}*/
		
		boolean checked = true;
		Integer userId = null;
		if (user != null) {
			checked = !user.getGroup().getNeedCheck();
			userId = user.getId();
		}
		checked = true;
		CmsComment cmsComment = cmsCommentMng.comment(score,text, RequestUtils.getIpAddr(request),
				contentId, site.getId(), userId, checked, false,null,null);
		json.put("success", true);
		json.put("status", 0);
		json.put("commentId",cmsComment.getId());
		
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequestMapping(value = "/commentDelete.jspx", method = RequestMethod.POST)
	public void commentDelete(Integer commentId, Integer local,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		CmsComment cmsComment = cmsCommentMng.deleteById(commentId);
		json.put("success", true);
		json.put("status", 0);
		
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequestMapping(value = "/comment_up.jspx")
	public void up(Integer contentId, HttpServletRequest request,
			HttpServletResponse response) {
		if (exist(contentId)) {
			cmsCommentMng.ups(contentId);
			ResponseUtils.renderJson(response, "true");
		} else {
			ResponseUtils.renderJson(response, "false");
		}
	}

	@RequestMapping(value = "/comment_down.jspx")
	public void down(Integer contentId, HttpServletRequest request,
			HttpServletResponse response) {
		if (exist(contentId)) {
			cmsCommentMng.downs(contentId);
			ResponseUtils.renderJson(response, "true");
		} else {
			ResponseUtils.renderJson(response, "false");
		}
	}

	private boolean hasCommented(CmsUser user, Content content) {
		if (content.hasCommentUser(user)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean exist(Integer id) {
		if (id == null) {
			return false;
		}
		Content content = contentMng.findById(id);
		return content != null;
	}

	@Autowired
	private CmsCommentMng cmsCommentMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private CmsIpAddressMng cmsIpAddressMng;
}
