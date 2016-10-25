package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.common.page.SimplePage.cpn;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.manager.assist.CmsSearchEngineLogMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;

@Controller
public class CmsIndexRankingLogAct {
	private static final Logger log = LoggerFactory.getLogger(CmsIndexRankingLogAct.class);

	@RequiresPermissions("indexrankinglog:v_list")
	@RequestMapping("/indexrankinglog/v_list.so")
	public String list(Integer pageNo, HttpServletRequest request,String companyName,String startTime,String endTime,
			ModelMap model) {
		Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils
				.getPageSize(request),companyName,startTime,endTime);
		model.addAttribute("pagination", pagination);
		return "indexrankinglog/list";
	}

	@Autowired
	private CmsSearchEngineLogMng manager;
}