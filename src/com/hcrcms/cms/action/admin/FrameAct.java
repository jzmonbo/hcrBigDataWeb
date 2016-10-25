package com.hcrcms.cms.action.admin;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.manager.main.ExpertMng;
@Controller
public class FrameAct {
	
	private static final Logger log = LoggerFactory.getLogger(FrameAct.class);
	
	@RequiresPermissions("frame:config_main")
	@RequestMapping("/frame/config_main.so")
	public String configMain(ModelMap model) {
		return "frame/config_main";
	}

	@RequiresPermissions("frame:config_left")
	@RequestMapping("/frame/config_left.so")
	public String configLeft(ModelMap model) {
		return "frame/config_left";
	}

	@RequiresPermissions("frame:config_right")
	@RequestMapping("/frame/config_right.so")
	public String configRight(ModelMap model) {
		return "frame/config_right";
	}

	@RequiresPermissions("frame:user_main")
	@RequestMapping("/frame/user_main.so")
	public String userMain(ModelMap model) {
		return "frame/user_main";
	}

	@RequiresPermissions("frame:user_left")
	@RequestMapping("/frame/user_left.so")
	public String userLeft(ModelMap model) {
		return "frame/user_left";
	}

	@RequiresPermissions("frame:user_right")
	@RequestMapping("/frame/user_right.so")
	public String userRight(ModelMap model) {
		return "frame/user_right";
	}

	@RequiresPermissions("frame:maintain_main")
	@RequestMapping("/frame/maintain_main.so")
	public String maintainMain(ModelMap model) {
		return "frame/maintain_main";
	}

	@RequiresPermissions("frame:maintain_left")
	@RequestMapping("/frame/maintain_left.so")
	public String maintainLeft(ModelMap model) {
		model.addAttribute("db", db);
		return "frame/maintain_left";
	}

	@RequiresPermissions("frame:maintain_right")
	@RequestMapping("/frame/maintain_right.so")
	public String maintainRight(ModelMap model) {
		return "frame/maintain_right";
	}
	

	@RequiresPermissions("frame:content_main")
	@RequestMapping("/frame/content_main.so")
	public String contentMain(String source,ModelMap model) {
		model.addAttribute("source", source);
		return "frame/content_main";
	}
	
	@RequiresPermissions("frame:statistic_main")
	@RequestMapping("/frame/statistic_main.so")
	public String statisticMain(ModelMap model) {
		return "frame/statistic_main";
	}
	
	@RequiresPermissions("frame:statistic_left")
	@RequestMapping("/frame/statistic_left.so")
	public String statisticLeft(){
		return "frame/statistic_left";
	}
	
	@RequiresPermissions("frame:statistic_right")
	@RequestMapping("/frame/statistic_right.so")
	public String statisticRight(){
		return "frame/statistic_right";
	}
	
	
	@RequiresPermissions("frame:expand_main")
	@RequestMapping("/frame/expand_main.so")
	public String expandMain(ModelMap model) {
		return "frame/expand_main";
	}
	
	@RequiresPermissions("frame:expand_left")
	@RequestMapping("/frame/expand_left.so")
	public String expandLeft(ModelMap model){
		model.addAttribute("menus", getMenus());
		return "frame/expand_left";
	}
	
	@RequiresPermissions("frame:expand_right")
	@RequestMapping("/frame/expand_right.so")
	public String expandRight(){
		return "frame/expand_right";
	}
	
	@RequiresPermissions("frame:expert_manage")
	@RequestMapping("/frame/expert_manage.so")
	public String expertManage(ModelMap model){
		List<Expert> expertList = expertMng.getList();
		model.addAttribute("expertList",expertList);
		return "frame/expert_right";
	}
	
	private Map<String,String> menus;
	//数据库种类(mysql、oracle、sqlserver、db2)
	private String db;

	public Map<String, String> getMenus() {
		return menus;
	}

	public void setMenus(Map<String, String> menus) {
		this.menus = menus;
	}
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}
	
	@Autowired
	private ExpertMng expertMng;
}
