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

import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.cms.manager.main.ArticleTopLinkMng;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class ArticleTopLinkAct {
	private static final Logger log = LoggerFactory
			.getLogger(ArticleTopLinkAct.class);

	@RequiresPermissions("articletoplink:v_list")
	@RequestMapping("/articletoplink/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<ArticleTopLink> articleTopLinkList = null;
		if (StringUtils.isBlank(text)){
			articleTopLinkList = articleTopLinkMng.getList();
		}else{
			articleTopLinkList = articleTopLinkMng.findByName(text);
		}
		model.addAttribute("articleTopLinkList", articleTopLinkList);
		return "articletoplink/list";
	}

	@RequiresPermissions("articletoplink:v_add")
	@RequestMapping("/articletoplink/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "articletoplink/add";
	}

	@RequiresPermissions("articletoplink:v_edit")
	@RequestMapping("/articletoplink/v_edit.so")
	public String edit(Integer atlId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("articleTopLink", articleTopLinkMng.findById(atlId));
		return "articletoplink/edit";
	}

	@RequiresPermissions("articletoplink:o_save")
	@RequestMapping("/articletoplink/o_save.so")
	public String save(ArticleTopLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		articleTopLinkMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("articletoplink:o_update")
	@RequestMapping("/articletoplink/o_update.so")
	public String update(ArticleTopLink bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getText() != null){
			String py = PingyinUtil.getPingYin(bean.getText());
			bean.setTextUs(py);
		}
		articleTopLinkMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("articletoplink:o_delete")
	@RequestMapping("/articletoplink/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		articleTopLinkMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private ArticleTopLinkMng articleTopLinkMng;
}