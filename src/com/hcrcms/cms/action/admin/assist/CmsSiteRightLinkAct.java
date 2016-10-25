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

import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.cms.manager.main.SiteRightLinkMng;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class CmsSiteRightLinkAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsSiteRightLinkAct.class);

	@RequiresPermissions("siterightlink:v_list")
	@RequestMapping("/siterightlink/v_list.so")
	public String list(String text,HttpServletRequest request,ModelMap model) {
		List<SiteRightLink> siteRightList = null;
		if (StringUtils.isBlank(text)){
			siteRightList = siteRightLinkMng.getList();
		}else{
			siteRightList = siteRightLinkMng.findByName(text);
		}
		model.addAttribute("siteRightList", siteRightList);
		return "siterightlink/list";
	}

	@RequiresPermissions("siterightlink:v_add")
	@RequestMapping("/siterightlink/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "siterightlink/add";
	}

	@RequiresPermissions("siterightlink:v_edit")
	@RequestMapping("/siterightlink/v_edit.so")
	public String edit(Integer srId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("siteRightLink", siteRightLinkMng.findById(srId));
		return "siterightlink/edit";
	}

	@RequiresPermissions("siterightlink:o_save")
	@RequestMapping("/siterightlink/o_save.so")
	public String save(SiteRightLink bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		if (bean.getText() != null){
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				String py = PingyinUtil.getPingYin(bean.getText());
				if (jedis != null) {
					jedis.set(py, bean.getText()); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
				}
				bean.setTextUs(py);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
		}
		if (bean.getUrl() == null || "".equals(bean.getUrl())){
			bean.setUrl(site.getUrlBuffer(true,true,true) + "/s/" + bean.getTextUs() + ".htm");
		}
		siteRightLinkMng.save(bean);
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("siterightlink:o_update")
	@RequestMapping("/siterightlink/o_update.so")
	public String update(SiteRightLink bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if (bean.getText() != null){
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				String py = PingyinUtil.getPingYin(bean.getText());
				if (jedis != null) {
					jedis.set(py, bean.getText()); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
				}
				bean.setTextUs(py);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			bean.setUrl(site.getUrlBuffer(true,true,true) + "/s/" + bean.getTextUs() + ".htm");
		}
		siteRightLinkMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("siterightlink:o_delete")
	@RequestMapping("/siterightlink/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		siteRightLinkMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private SiteRightLinkMng siteRightLinkMng;
}