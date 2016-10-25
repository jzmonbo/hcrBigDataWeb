package com.hcrcms.cms.action.admin.assist;

import static com.hcrcms.common.page.SimplePage.cpn;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.entity.assist.CmsTopicReport;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.manager.assist.CmsTopicEnterpriseMng;
import com.hcrcms.cms.manager.assist.CmsTopicReportMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;

@Controller
public class CmsTopicReportAct {
	private static final Logger log = LoggerFactory.getLogger(CmsTopicReportAct.class);

	@RequiresPermissions("topicreport:v_list")
	@RequestMapping("/topicreport/v_list.so")
	public String list(Integer entId,Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		
		List<CmsTopicEnterprise> enterprises = enterpriseMng.getListForTag(null, false, null);
		List<CmsTopicReport> cmsTopicReports = topicReportMng.getListForTag(null, false, null);
		model.addAttribute("enterprises", enterprises);
		model.addAttribute("cmsTopicReports",cmsTopicReports);
		return "topicreport/list";
	}

	@RequiresPermissions("topicreport:v_add")
	@RequestMapping("/topicreport/v_add.so")
	public String add(Integer entId,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsTopicEnterprise cmsTopicEnterprise = enterpriseMng.findById(entId);
		model.addAttribute("cmsTopicEnterprise",cmsTopicEnterprise);
		model.addAttribute("entId",entId);
		return "topicreport/add";
	}

	@RequiresPermissions("topicreport:v_edit")
	@RequestMapping("/topicreport/v_edit.so")
	public String edit(Integer rId, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(rId, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsTopicReport cmsTopicReport = topicReportMng.findById(rId);
		model.addAttribute("cmsTopicReport",cmsTopicReport);
		return "topicreport/edit";
	}

	@RequiresPermissions("topicreport:o_save")
	@RequestMapping("/topicreport/o_save.so")
	public String save(CmsTopicReport bean,Integer entId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsTopicEnterprise cmsTopicEnterprise = enterpriseMng.findById(entId);
		bean.setTopicEnterprise(cmsTopicEnterprise);
		topicReportMng.save(bean,null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("topicreport:o_update")
	@RequestMapping("/topicreport/o_update.so")
	public String update(CmsTopicReport bean,
			Integer pageNo,HttpServletRequest request, ModelMap model) {
		topicReportMng.update(bean, null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("topicreport:o_delete")
	@RequestMapping("/topicreport/o_delete.so")
	public String delete(Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		topicReportMng.deleteByIds(ids);
		return list(null,pageNo, request, model);
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
		CmsSpecialTopic entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsTopic.class, id)) {
			return true;
		}
		return false;
	}

	@Autowired
	private CmsSpecialTopicMng manager;
	@Autowired
	private CmsTopicEnterpriseMng enterpriseMng;
	@Autowired
	private CmsTopicReportMng topicReportMng;
}