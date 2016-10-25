package com.hcrcms.cms.action.admin.assist;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.entity.main.HotSpotNavigation;
import com.hcrcms.cms.manager.main.HotSpotNavigationMng;

@Controller
public class CmsHotSpotNavigationAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsHotSpotNavigationAct.class);

	@RequiresPermissions("hot:v_list")
	@RequestMapping("/hot/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<HotSpotNavigation> hotSpotNavigationList = null;
		if (StringUtils.isBlank(text)){
			hotSpotNavigationList = cmsHotSpotNavigationMng.getList();
		}else{
			hotSpotNavigationList = cmsHotSpotNavigationMng.findByName(text);
		}
		model.addAttribute("hotNavigationList", hotSpotNavigationList);
		return "hot/list";
	}

	@RequiresPermissions("hot:v_add")
	@RequestMapping("/hot/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "hot/add";
	}

	@RequiresPermissions("hot:v_edit")
	@RequestMapping("/hot/v_edit.so")
	public String edit(Integer hotId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsHotSpotNavigation", cmsHotSpotNavigationMng.findById(hotId));
		return "hot/edit";
	}

	@RequiresPermissions("hot:o_save")
	@RequestMapping("/hot/o_save.so")
	public String save(HotSpotNavigation bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		cmsHotSpotNavigationMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("hot:o_update")
	@RequestMapping("/hot/o_update.so")
	public String update(HotSpotNavigation bean,
			HttpServletRequest request, ModelMap model) {
		cmsHotSpotNavigationMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("hot:o_delete")
	@RequestMapping("/hot/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		cmsHotSpotNavigationMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private HotSpotNavigationMng cmsHotSpotNavigationMng;
}