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

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.assist.CmsFriendlink;
import com.hcrcms.cms.entity.assist.CmsFriendlinkCtg;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class CmsExpertAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsExpertAct.class);

	@RequiresPermissions("expert:v_list")
	@RequestMapping("/expert/v_list.so")
	public String list(String author,HttpServletRequest request,ModelMap model) {
		List<Expert> expertList = null;
		if (StringUtils.isBlank(author)){
			expertList = expertMng.getList();
		}else{
			expertList = expertMng.findByName(author);
		}
		model.addAttribute("expertList", expertList);
		return "expert/list";
	}

	@RequiresPermissions("expert:v_add")
	@RequestMapping("/expert/v_add.so")
	public String add(ModelMap model, HttpServletRequest request) {
		return "expert/add";
	}

	@RequiresPermissions("expert:v_edit")
	@RequestMapping("/expert/v_edit.so")
	public String edit(Integer expertId,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsExpert", expertMng.findById(expertId));
		return "expert/edit";
	}

	@RequiresPermissions("expert:o_save")
	@RequestMapping("/expert/o_save.so")
	public String save(Expert bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getReleaseDate() == null){
			bean.setReleaseDate(new Date());
		}
		if (bean.getSort() == null){
			bean.setSort(10000);
		}
		if (bean.getAuthor() != null){
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				String py = PingyinUtil.getPingYin(bean.getAuthor());
				if (jedis != null) {
					jedis.set(py, bean.getAuthor()); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
				}
				bean.setAuthorPy(py);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
		}
		expertMng.save(bean);
		log.info("add expert successful!");
		model.clear();
		return list(null,request, model);
	}

	@RequiresPermissions("expert:o_update")
	@RequestMapping("/expert/o_update.so")
	public String update(Expert bean,
			HttpServletRequest request, ModelMap model) {
		if (bean.getReleaseDate() == null){
			bean.setReleaseDate(new Date());
		}
		if (bean.getAuthor() != null){
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				String py = PingyinUtil.getPingYin(bean.getAuthor());
				if (jedis != null) {
					jedis.set(py, bean.getAuthor()); //把拼音放到缓存中
					//jedis.expire(py, 2592000); //缓存时间为一个月
				}
				bean.setAuthorPy(py);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
		}
		expertMng.update(bean);
		return list(null,request, model);
	}

	@RequiresPermissions("expert:o_delete")
	@RequestMapping("/expert/o_delete.so")
	public String delete(Integer[] ids,
			HttpServletRequest request, ModelMap model) {
		expertMng.deleteByIds(ids);
		return list(null,request, model);
	}

	@Autowired
	private ExpertMng expertMng;
}