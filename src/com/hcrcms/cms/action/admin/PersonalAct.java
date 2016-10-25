package com.hcrcms.cms.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserExt;
import com.hcrcms.core.manager.CmsUserExtMng;
import com.hcrcms.core.manager.CmsUserMng;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;

@Controller
public class PersonalAct {
	@RequiresPermissions("personal:v_profile")
	@RequestMapping("/personal/v_profile.so")
	public String profileEdit(HttpServletRequest request, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		model.addAttribute("user", user);
		return "personal/profile";
	}

	@RequiresPermissions("personal:o_profile")
	@RequestMapping("/personal/o_profile.so")
	public String profileUpdate(String origPwd, String newPwd, String email,
			String realname, HttpServletRequest request, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		WebErrors errors = validatePasswordSubmit(user.getId(), origPwd,
				newPwd, email, realname, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUserExt ext = user.getUserExt();
		if (ext == null) {
			ext = new CmsUserExt();
		}
		ext.setRealname(realname);
		cmsUserExtMng.update(ext, user);
		cmsUserMng.updatePwdEmail(user.getId(), newPwd, email);
		model.addAttribute("message", "global.success");
		return profileEdit(request, model);
	}

	/**
	 * 验证密码是否正确
	 * 
	 * @param origPwd
	 *            原密码
	 * @param request
	 * @param response
	 */
	@RequiresPermissions("personal:v_checkPwd")
	@RequestMapping("/personal/v_checkPwd.so")
	public void checkPwd(String origPwd, HttpServletRequest request,
			HttpServletResponse response) {
		CmsUser user = CmsUtils.getUser(request);
		boolean pass = cmsUserMng.isPasswordValid(user.getId(), origPwd);
		ResponseUtils.renderJson(response, pass ? "true" : "false");
	}

	private WebErrors validatePasswordSubmit(Integer id, String origPwd,
			String newPwd, String email, String realname,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(origPwd, "origPwd", 32)) {
			return errors;
		}
		if (errors.ifMaxLength(newPwd, "newPwd", 32)) {
			return errors;
		}
		if (errors.ifMaxLength(email, "email", 100)) {
			return errors;
		}
		if (errors.ifMaxLength(realname, "realname", 100)) {
			return errors;
		}
		if (!cmsUserMng.isPasswordValid(id, origPwd)) {
			errors.addErrorCode("member.origPwdInvalid");
			return errors;
		}
		return errors;
	}

	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsUserExtMng cmsUserExtMng;
}
