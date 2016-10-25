package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.common.page.SimplePage.cpn;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.main.CmsThirdAccount;
import com.hcrcms.cms.manager.main.CmsThirdAccountMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;

@Controller
public class CmsThirdAccountAct {
	private static final Logger log = LoggerFactory.getLogger(CmsThirdAccountAct.class);

	@RequiresPermissions("account:v_list")
	@RequestMapping("/account/v_list.so")
	public String list(String username,String source,Integer pageNo, HttpServletRequest request, ModelMap model) {
		Pagination pagination = manager.getPage(username,source,cpn(pageNo), CookieUtils
				.getPageSize(request));
		model.addAttribute("username",username);
		model.addAttribute("source",source);
		model.addAttribute("pagination",pagination);
		model.addAttribute("pageNo",pagination.getPageNo());
		return "account/list";
	}
	
	@RequiresPermissions("account:o_delete")
	@RequestMapping("/account/o_delete.so")
	public String delete(String username,String source,Long[] ids, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsThirdAccount[] beans = manager.deleteByIds(ids);
		for (CmsThirdAccount bean : beans) {
			log.info("delete CmsThirdAccount id={}", bean.getId());
		}
		return list(username,source,pageNo, request, model);
	}

	
	private WebErrors validateDelete(Long[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Long id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private boolean vldExist(Long id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsThirdAccount entity = manager.findById(id);
		if(errors.ifNotExist(entity, CmsThirdAccount.class, id)) {
			return true;
		}
		return false;
	}
	
	@Autowired
	private CmsThirdAccountMng manager;
}