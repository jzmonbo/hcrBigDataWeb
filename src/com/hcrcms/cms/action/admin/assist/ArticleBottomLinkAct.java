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

import com.hcrcms.cms.entity.main.ArticleBottomLink;
import com.hcrcms.cms.manager.main.ArticleBottomLinkMng;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class ArticleBottomLinkAct {
	private static final Logger log = LoggerFactory
			.getLogger(ArticleBottomLinkAct.class);

	@RequiresPermissions("articlebottomlink:v_list")
	@RequestMapping("/articlebottomlink/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<ArticleBottomLink> articleBottomLinkList = null;
		if (StringUtils.isBlank(text)){
			articleBottomLinkList = articleBottomLinkMng.getList();
		}else{
			articleBottomLinkList = articleBottomLinkMng.findByName(text);
		}
		model.addAttribute("articleBottomLinkList", articleBottomLinkList);
		return "articlebottomlink/list";
	}

	@RequiresPermissions("articlebottomlink:v_add")
	@RequestMapping("/articlebottomlink/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "articlebottomlink/add";
	}

	@RequiresPermissions("articlebottomlink:v_edit")
	@RequestMapping("/articlebottomlink/v_edit.so")
	public String edit(Integer ablId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("articleBottomLink", articleBottomLinkMng.findById(ablId));
		return "articlebottomlink/edit";
	}

	@RequiresPermissions("articlebottomlink:o_save")
	@RequestMapping("/articlebottomlink/o_save.so")
	public String save(ArticleBottomLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		articleBottomLinkMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("articlebottomlink:o_update")
	@RequestMapping("/articlebottomlink/o_update.so")
	public String update(ArticleBottomLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		articleBottomLinkMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("articlebottomlink:o_delete")
	@RequestMapping("/articlebottomlink/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		articleBottomLinkMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private ArticleBottomLinkMng articleBottomLinkMng;
}