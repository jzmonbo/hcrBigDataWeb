package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.cms.Constants.TPLDIR_TOPIC;
import static com.hcrcms.cms.action.front.TopicAct.TOPIC_DEFAULT;
import static com.hcrcms.cms.action.front.TopicAct.TOPIC_INDEX;
import static com.hcrcms.common.page.SimplePage.cpn;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.manager.assist.CmsFileMng;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.common.web.springmvc.MessageResolver;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.tpl.TplManager;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.CoreUtils;

@Controller
public class CmsSpecialTopicAct {
	private static final Logger log = LoggerFactory.getLogger(CmsSpecialTopicAct.class);

	@RequiresPermissions("special:v_list")
	@RequestMapping("/special/v_list.so")
	public String list(Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils
				.getPageSize(request));
		model.addAttribute("pagination", pagination);
		return "specialtopic/list";
	}

	@RequiresPermissions("special:v_add")
	@RequestMapping("/special/v_add.so")
	public String add(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		return "specialtopic/add";
	}

	@RequiresPermissions("special:v_edit")
	@RequestMapping("/special/v_edit.so")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSpecialTopic topic = manager.findById(id);
		model.addAttribute("cmsspecialTopic", topic);
		return "specialtopic/edit";
	}

	@RequiresPermissions("special:o_save")
	@RequestMapping("/special/o_save.so")
	public String save(CmsSpecialTopic bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if (bean.getIsUse() == null){
			bean.setIsUse(1);
		}
		if (bean.getStartTime() == null){
			bean.setStartTime(new Timestamp(System.currentTimeMillis()));
		}
		if (bean.getEndTime() == null){
			bean.setEndTime(new Timestamp(System.currentTimeMillis()));
		}
		manager.save(bean,null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("special:o_update")
	@RequestMapping("/special/o_update.so")
	public String update(CmsSpecialTopic bean,
			Integer pageNo,HttpServletRequest request, ModelMap model) {
		if (bean.getIsUse() == null){
			bean.setIsUse(1);
		}
		if (bean.getStartTime() == null){
			bean.setStartTime(new Timestamp(System.currentTimeMillis()));
		}
		if (bean.getEndTime() == null){
			bean.setEndTime(new Timestamp(System.currentTimeMillis()));
		}
		manager.update(bean, null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("special:o_delete")
	@RequestMapping("/special/o_delete.so")
	public String delete(Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		manager.deleteByIds(ids);
		return list(pageNo, request, model);
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
}