package com.hcrcms.cms.action.admin.assist;

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

import com.hcrcms.cms.entity.main.TopLink;
import com.hcrcms.cms.manager.main.TopLinkMng;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class CmsTopLinkAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsTopLinkAct.class);

	@RequiresPermissions("toplink:v_list")
	@RequestMapping("/toplink/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<TopLink> topLinkList = null;
		if (StringUtils.isBlank(text)){
			topLinkList = topLinkMng.getList();
		}else{
			topLinkList = topLinkMng.findByName(text);
		}
		model.addAttribute("topLinkList", topLinkList);
		return "toplink/list";
	}

	@RequiresPermissions("toplink:v_add")
	@RequestMapping("/toplink/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "toplink/add";
	}

	@RequiresPermissions("toplink:v_edit")
	@RequestMapping("/toplink/v_edit.so")
	public String edit(Integer topId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("topLink", topLinkMng.findById(topId));
		return "toplink/edit";
	}

	@RequiresPermissions("toplink:o_save")
	@RequestMapping("/toplink/o_save.so")
	public String save(TopLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		topLinkMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("toplink:o_update")
	@RequestMapping("/toplink/o_update.so")
	public String update(TopLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		topLinkMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("toplink:o_delete")
	@RequestMapping("/toplink/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		topLinkMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private TopLinkMng topLinkMng;
}