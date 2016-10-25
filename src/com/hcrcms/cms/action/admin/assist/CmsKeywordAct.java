package com.hcrcms.cms.action.admin.assist;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import redis.clients.jedis.Jedis;

import com.hcrcms.cms.entity.assist.CmsKeyword;
import com.hcrcms.cms.manager.assist.CmsKeywordMng;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.PingyinUtil;

@Controller
public class CmsKeywordAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsKeywordAct.class);

	@RequiresPermissions("keyword:v_list")
	@RequestMapping("/keyword/v_list.so")
	public String list(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		//List<CmsKeyword> list = manager.getListBySiteId(site.getId(), false,false);
		List<CmsKeyword> list = manager.getList(site.getId(),false,false);
		model.addAttribute("list", list);
		return "keyword/list";
	}

	@RequiresPermissions("keyword:o_save")
	@RequestMapping("/keyword/o_save.so")
	public String save(CmsKeyword bean, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		//把汉字关键词转换为拼音形式
		bean.setSite(site);
		String py = PingyinUtil.getPingYin(bean.getName());
		Jedis jedis = null;
		try {
			jedis = Constant.getJedis();
			if (jedis != null){
				jedis.set(py, bean.getName()); //把拼音放到缓存中
				//jedis.expire(py, 2592000); //缓存时间为一个月
				bean.setUrl(site.getUrlBuffer(true, true, true) + "/s/" + py
						+ ".htm"); //前面1是固定的,无意义,只是后面的拼音读取
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Constant.returnResource(jedis);
		}
		bean = manager.save(bean);
		model.addAttribute("message", "global.success");
		log.info("save CmsKeyword id={}", bean.getId());
		cmsLogMng.operating(request, "cmsKeyword.log.save", "id="
				+ bean.getId() + ";name=" + bean.getName());
		return list(request, model);
	}

	@RequiresPermissions("keyword:o_update")
	@RequestMapping("/keyword/o_update.so")
	public String update(Integer[] id, String[] name, String[] url,
			Boolean[] disabled, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateUpdate(id, name, url, disabled, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		if (id != null && id.length > 0) {
			//把汉字关键词转换为拼音形式
			Jedis jedis = null;
			try {
				jedis = Constant.getJedis();
				if (jedis != null){
					for (int i=0;i<name.length;i++){
						String py = PingyinUtil.getPingYin(name[i]);
						jedis.set(py, name[i]); //把拼音放到缓存中
						//jedis.expire(py, 2592000); //缓存时间为一个月
						url[i] = site.getUrlBuffer(true, true, true) + "/s/" + py
								+ ".htm"; //前面1是固定的,无意义,只是后面的拼音读取
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Constant.returnResource(jedis);
			}
			manager.updateKeywords(id, name, url, disabled);
		}
		log.info("update CmsKeyword");
		model.addAttribute("message", "global.success");
		cmsLogMng.operating(request, "cmsKeyword.log.update", null);
		return list(request, model);
	}

	@RequiresPermissions("keyword:o_delete")
	@RequestMapping("/keyword/o_delete.so")
	public String delete(Integer[] ids, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsKeyword[] beans = manager.deleteByIds(ids);
		for (CmsKeyword bean : beans) {
			log.info("delete CmsKeyword id={}", bean.getId());
			cmsLogMng.operating(request, "cmsKeyword.log.delete", "id="
					+ bean.getId() + ";name=" + bean.getName());
		}
		return list(request, model);
	}

	private WebErrors validateSave(CmsKeyword bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateUpdate(Integer[] ids, String[] names,
			String[] urls, Boolean[] disalbeds, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "id")) {
			return errors;
		}
		if (errors.ifEmpty(names, "name")) {
			return errors;
		}
		if (errors.ifEmpty(urls, "url")) {
			return errors;
		}
		if (ids.length != names.length || ids.length != urls.length) {
			errors.addErrorString("id, name, url length not equals");
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsKeyword entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsKeyword.class, id)) {
			return true;
		}
		// TODO 暂时不判断站点相关权限
		// if (!entity.getSite().getId().equals(siteId)) {
		// errors.notInSite(CmsKeyword.class, id);
		// return true;
		// }
		return false;
	}

	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsKeywordMng manager;
}