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

import com.hcrcms.cms.entity.assist.CmsSearchEngine;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.manager.assist.CmsSearchEngineMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;

@Controller
public class CmsIndexRankingAct {
	private static final Logger log = LoggerFactory.getLogger(CmsIndexRankingAct.class);

	@RequiresPermissions("indexranking:v_list")
	@RequestMapping("/indexranking/v_list.so")
	public String list(Integer pageNo, HttpServletRequest request,String companyName,
			ModelMap model) {
		Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils
				.getPageSize(request),companyName);
		model.addAttribute("pagination", pagination);
		return "indexranking/list";
	}

	@RequiresPermissions("indexranking:v_add")
	@RequestMapping("/indexranking/v_add.so")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		return "indexranking/add";
	}

	@RequiresPermissions("indexranking:v_edit")
	@RequestMapping("/indexranking/v_edit.so")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSearchEngine topic = manager.findById(id);
		model.addAttribute("cmsIndexBanking", topic);
		return "indexranking/edit";
	}

	@RequiresPermissions("indexranking:o_save")
	@RequestMapping("/indexranking/o_save.so")
	public String save(CmsSearchEngine bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		manager.save(bean);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("indexranking:o_update")
	@RequestMapping("/indexranking/o_update.so")
	public String update(CmsSearchEngine bean,
			Integer pageNo,HttpServletRequest request, ModelMap model) {
		manager.update(bean);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("indexranking:o_delete")
	@RequestMapping("/indexranking/o_delete.so")
	public String delete(Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		manager.deleteByIds(ids);
		return list(pageNo, request,null,model);
	}

	private WebErrors validateSave(CmsTopic bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}

	private WebErrors validatePriority(Integer[] wids, Integer[] priority,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifEmpty(wids, "wids")) {
			return errors;
		}
		if (errors.ifEmpty(priority, "priority")) {
			return errors;
		}
		if (wids.length != priority.length) {
			errors.addErrorString("wids length not equals priority length");
			return errors;
		}
		for (int i = 0, len = wids.length; i < len; i++) {
			if (vldExist(wids[i], errors)) {
				return errors;
			}
			if (priority[i] == null) {
				priority[i] = 0;
			}
		}
		return errors;
	}

	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsSearchEngine entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsTopic.class, id)) {
			return true;
		}
		return false;
	}

	@Autowired
	private CmsSearchEngineMng manager;
}