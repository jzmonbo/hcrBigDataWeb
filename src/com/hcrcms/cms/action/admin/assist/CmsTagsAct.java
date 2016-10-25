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

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.main.CmsTags;
import com.hcrcms.cms.manager.main.TagsMng;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class CmsTagsAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsTagsAct.class);

	@RequiresPermissions("cmstags:v_list")
	@RequestMapping("/cmstags/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<CmsTags> cmsTagsList = null;
		if (StringUtils.isBlank(text)){
			cmsTagsList = tagsMng.getList();
		}else{
			cmsTagsList = tagsMng.findByName(text);
		}
		model.addAttribute("cmsTagsList", cmsTagsList);
		return "cmstags/list";
	}

	@RequiresPermissions("cmstags:v_add")
	@RequestMapping("/cmstags/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "cmstags/add";
	}

	@RequiresPermissions("cmstags:v_edit")
	@RequestMapping("/cmstags/v_edit.so")
	public String edit(Integer tagId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsTags", tagsMng.findById(tagId));
		return "cmstags/edit";
	}

	@RequiresPermissions("cmstags:o_save")
	@RequestMapping("/cmstags/o_save.so")
	public String save(CmsTags bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if (bean.getUrl() == null || "".equals(bean.getUrl())){
			String py = PingyinUtil.getPingYin(bean.getText());
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				if (jedis != null) {
					jedis.set(py, bean.getText()); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			bean.setTextUs(py);
			bean.setUrl(site.getUrlBuffer(true,true,true) + "/s/" + py + ".htm");		//前面1是固定的,无意义,只是后面的拼音读取
		}
		tagsMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("cmstags:o_update")
	@RequestMapping("/cmstags/o_update.so")
	public String update(CmsTags bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		Jedis jedis = null;
		try {
			jedis = Constant.getJedis();
			String py = PingyinUtil.getPingYin(bean.getText());
			if (jedis.get(py) == null) {
				jedis.set(py, bean.getText()); //把拼音放到缓存中
				//jedis.expire(py, 2592000); //缓存时间为一个月
				bean.setTextUs(py);
				bean.setUrl(site.getUrlBuffer(true, true, true) + "/s/" + py
						+ ".htm"); //前面1是固定的,无意义,只是后面的拼音读取
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Constant.returnResource(jedis);
		}
		tagsMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("cmstags:o_delete")
	@RequestMapping("/cmstags/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		tagsMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private TagsMng tagsMng;
}