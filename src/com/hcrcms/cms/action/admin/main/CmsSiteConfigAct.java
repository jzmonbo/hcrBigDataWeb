package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.cms.Constants.TPLDIR_INDEX;
import static com.hcrcms.cms.Constants.TPL_BASE;

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

import com.hcrcms.core.entity.CmsDictionary;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsSiteCompany;
import com.hcrcms.core.entity.Ftp;
import com.hcrcms.core.manager.CmsConfigMng;
import com.hcrcms.core.manager.CmsDictionaryMng;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.manager.CmsSiteCompanyMng;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.manager.FtpMng;
import com.hcrcms.core.tpl.TplManager;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.CoreUtils;

@Controller
public class CmsSiteConfigAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsSiteConfigAct.class);

	@RequiresPermissions("site_config:v_base_edit")
	@RequestMapping("/site_config/v_base_edit.so")
	public String baseEdit(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		List<Ftp> ftpList = ftpMng.getList();
		List<String>indexTplList=getTplIndex(site,null);
		// 当前模板，去除基本路径
		int tplPathLength = site.getTplPath().length();
		String tplIndex = site.getTplIndex();
		if (!StringUtils.isBlank(tplIndex)) {
			tplIndex = tplIndex.substring(tplPathLength);
		}
		model.addAttribute("ftpList", ftpList);
		model.addAttribute("indexTplList",indexTplList);
		model.addAttribute("config", configMng.get());
		model.addAttribute("cmsSite", site);
		model.addAttribute("tplIndex", tplIndex);
		return "site_config/base_edit";
	}

	@RequiresPermissions("site_config:o_base_update")
	@RequestMapping("/site_config/o_base_update.so")
	public String baseUpdate(CmsSite bean, Integer uploadFtpId,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateBaseUpdate(bean, request);
		String tplPath = bean.getTplPath();
		if (!StringUtils.isBlank(bean.getTplIndex())) {
			bean.setTplIndex(tplPath + bean.getTplIndex());
		}
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		bean.setId(site.getId());
		//这里自动赋值手机静态页面路径
		bean = manager.update(bean, uploadFtpId);
		model.addAttribute("message", "global.success");
		log.info("update CmsSite success. id={}", site.getId());
		cmsLogMng.operating(request, "cmsSiteConfig.log.updateBase", null);
		return baseEdit(request, model);
	}

	@RequiresPermissions("site_config:v_company_edit")
	@RequestMapping("/site_config/v_company_edit.so")
	public String companyEdit(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsSiteCompany company = site.getSiteCompany();
		List<CmsDictionary> scales = dictionaryMng.getList("scale");
		List<CmsDictionary> natures = dictionaryMng.getList("nature");
		List<CmsDictionary> industrys = dictionaryMng.getList("industry");
		model.addAttribute("site",CmsUtils.getSite(request));
		model.addAttribute("company", company);
		model.addAttribute("scales", scales);
		model.addAttribute("natures", natures);
		model.addAttribute("industrys", industrys);
		return "site_config/company_edit";
	}

	@RequiresPermissions("site_config:o_company_update")
	@RequestMapping("/site_config/o_company_update.so")
	public String companyUpdate(CmsSiteCompany company,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateCompanyUpdate(site, company, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		siteCompanyMng.update(company);
		model.addAttribute("message", "global.success");
		log.info("update CmsSite success. id={}", site.getId());
		cmsLogMng.operating(request, "cmsSiteConfig.log.updateBase", null);
		return companyEdit(request, model);
	}
	
	private List<String> getTplIndex(CmsSite site,String tpl) {
		String path=site.getPath();
		List<String> tplList = tplManager.getNameListByPrefix(site.getTplIndexPrefix(TPLDIR_INDEX));
		return CoreUtils.tplTrim(tplList,getTplPath(path), tpl);
	}

	private WebErrors validateCompanyUpdate(CmsSite site,
			CmsSiteCompany company, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (!company.getId().equals(site.getId())) {
			errors.addErrorCode("error.notInSite", CmsSiteCompany.class,
					company.getId());
		}
		return errors;
	}

	private WebErrors validateBaseUpdate(CmsSite bean,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}
	
	private String getTplPath(String path){
		return TPL_BASE + "/" + path;
	}

	@Autowired
	private FtpMng ftpMng;
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsSiteMng manager;
	@Autowired
	private CmsSiteCompanyMng siteCompanyMng;
	@Autowired
	private CmsDictionaryMng dictionaryMng;
	@Autowired
	private TplManager tplManager;
	@Autowired
	private CmsConfigMng configMng;

}