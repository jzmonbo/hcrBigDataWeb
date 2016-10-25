package com.hcrcms.cms.action.admin.assist;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.main.JingPinSportSet;
import com.hcrcms.cms.manager.main.BoutiqueColumnMng;

@Controller
public class CmsRoutiqueColumnAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsRoutiqueColumnAct.class);

	@RequiresPermissions("boutiqueColumn:v_edit")
	@RequestMapping("/boutiqueColumn/v_edit.so")
	public String edit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsJingPinSport",boutiqueColumnMng.getList().get(0));
		return "jingpinsport/edit";
	}

	@RequiresPermissions("boutiqueColumn:o_update")
	@RequestMapping("/boutiqueColumn/o_update.so")
	public String update(JingPinSportSet bean,
			HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		boutiqueColumnMng.update(bean);
		model.addAttribute("cmsJingPinSport",bean);
		String result = "<script type='text/javascript'>alert('修改成功')</script>";
		try {
			response.setCharacterEncoding("utf-8");
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "jingpinsport/edit";
	}

	@Autowired
	private BoutiqueColumnMng boutiqueColumnMng;
}