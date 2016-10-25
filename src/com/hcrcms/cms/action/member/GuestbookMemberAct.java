package com.hcrcms.cms.action.member;

import static com.hcrcms.cms.Constants.TPLDIR_GUESTBOOK;
import static com.hcrcms.common.page.SimplePage.cpn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.assist.CmsGuestbook;
import com.hcrcms.cms.manager.assist.CmsGuestbookMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.MemberConfig;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.FrontUtils;

/**
 * 会员中心获取留言Action
 * 
 * @author Zhang
 * 
 */
@Controller
public class GuestbookMemberAct {

	public static final String GUESTBOOK_LIST = "tpl.guestBookLists";
	public static final String GUESTBOOK_DETAIL = "tpl.guestBookDetail";
	public static final String GUESTBOOK_REPLAY = "tpl.guestBookReplay";

	/**
	 * 我的留言
	 * 
	 * 如果没有登录则跳转到登陆页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/myguestbook.jspx")
	public String myguestbook(Integer pageNo, Integer ctgId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		Pagination pagination = guestbookMng.getPage(site.getId(), ctgId, user
				.getId(), null, null, true, false, cpn(pageNo), CookieUtils
				.getPageSize(request));
		model.addAttribute("pagination", pagination);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_GUESTBOOK, GUESTBOOK_LIST);
	}

	/**
	 * 留言详细
	 * 
	 */
	@RequestMapping(value = "/member/guestbook_detail.jspx")
	public String guestbook_detail(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		CmsGuestbook guestbook = guestbookMng.findById(id);
		model.addAttribute("guestbook", guestbook);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_GUESTBOOK, GUESTBOOK_DETAIL);
	}

	/**
	 * 查看留言回复
	 */
	@RequestMapping(value = "/member/guestbook_replay.jspx")
	public String guestbook_replay(Integer id, String nextUrl,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		CmsGuestbook guestbook = guestbookMng.findById(id);
		if(!guestbook.getMember().equals(user)){
			WebErrors errors=WebErrors.create(request);
			errors.addErrorCode("error.noPermissionsView");
			return FrontUtils.showError(request, response, model, errors);
		}
		model.addAttribute("guestbook", guestbook);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_GUESTBOOK, GUESTBOOK_REPLAY);
	}

	@Autowired
	private CmsGuestbookMng guestbookMng;

}
