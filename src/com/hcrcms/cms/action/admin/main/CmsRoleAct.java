package com.hcrcms.cms.action.admin.main;

import static com.hcrcms.common.page.SimplePage.cpn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.assist.CmsPlug;
import com.hcrcms.cms.manager.assist.CmsPlugMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsRole;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.manager.CmsRoleMng;
import com.hcrcms.core.manager.CmsUserMng;
import com.hcrcms.core.security.CmsAuthorizingRealm;
import com.hcrcms.core.web.WebErrors;

@Controller
public class CmsRoleAct {
	private static final Logger log = LoggerFactory.getLogger(CmsRoleAct.class);

	@RequiresPermissions("role:v_list")
	@RequestMapping("/role/v_list.so")
	public String list(HttpServletRequest request, ModelMap model) {
		List<CmsRole> list = manager.getList();
		model.addAttribute("list", list);
		return "role/list";
	}
	
	@RequiresPermissions("role:v_tree")
	@RequestMapping("/role/v_tree.so")
	public String tree(HttpServletRequest request, ModelMap model) {
		return "role/tree";
	}

	@RequiresPermissions("role:v_add")
	@RequestMapping("/role/v_add.so")
	public String add(ModelMap model) {
		List<CmsPlug>usedPlugs=plugMng.getList(null, true);
		model.addAttribute("plugs", usedPlugs);
		return "role/add";
	}

	@RequiresPermissions("role:v_edit")
	@RequestMapping("/role/v_edit.so")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		List<CmsPlug>usedPlugs=plugMng.getList(null, true);
		model.addAttribute("plugs", usedPlugs);
		model.addAttribute("cmsRole", manager.findById(id));
		return "role/edit";
	}

	@RequiresPermissions("role:o_save")
	@RequestMapping("/role/o_save.so")
	public String save(CmsRole bean, String[] perms,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.save(bean, splitPerms(perms));
		log.info("save CmsRole id={}", bean.getId());
		cmsLogMng.operating(request, "cmsRole.log.save", "id=" + bean.getId()
				+ ";name=" + bean.getName());
		return "redirect:v_list.so";
	}

	@RequiresPermissions("role:o_update")
	@RequestMapping("/role/o_update.so")
	public String update(CmsRole bean, String[] perms,boolean all,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.update(bean, splitPerms(perms));
		//权限更改 清空用户权限缓存
		if(hasChangePermission(all, perms, bean)){
			Set<CmsUser>admins=bean.getUsers();
			for(CmsUser admin:admins){
				authorizingRealm.removeUserAuthorizationInfoCache(admin.getUsername());
			}
		}
		log.info("update CmsRole id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsRole.log.update", "id=" + bean.getId()
				+ ";name=" + bean.getName());
		return list(request, model);
	}

	@RequiresPermissions("role:o_delete")
	@RequestMapping("/role/o_delete.so")
	public String delete(Integer[] ids, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsRole[] beans = manager.deleteByIds(ids);
		for (CmsRole bean : beans) {
			log.info("delete CmsRole id={}", bean.getId());
			cmsLogMng.operating(request, "cmsRole.log.delete", "id="
					+ bean.getId() + ";name=" + bean.getName());
		}
		return list(request, model);
	}
	
	@RequiresPermissions("role:v_list_members")
	@RequestMapping("/role/v_list_members.so")
	public String list_members(Integer roleId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		Pagination pagination = userMng.getAdminsByRoleId(roleId,cpn(pageNo), CookieUtils.getPageSize(request));
		model.addAttribute("pagination", pagination);
		model.addAttribute("roleId", roleId);
		return "role/members";
	}
	

	@RequiresPermissions("role:o_delete_member")
	@RequestMapping("/role/o_delete_member.so")
	public String delete_members(Integer roleId,Integer userIds[], Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		CmsRole role=manager.findById(roleId);
		manager.deleteMembers(role, userIds);
		return list_members(roleId, pageNo, request, model);
	}
	

	private boolean hasChangePermission(boolean all,String[] perms,CmsRole bean){
		CmsRole role=manager.findById(bean.getId());
		if(!role.getAll().equals(all)){
			return true;
		}
		if(!bean.getPerms().toArray().equals(perms)){
			return true;
		}
		return false;
	}

	private WebErrors validateSave(CmsRole bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (vldExist(id, errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsRole entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsRole.class, id)) {
			return true;
		}
		return false;
	}

	private Set<String> splitPerms(String[] perms) {
		Set<String> set = new HashSet<String>();
		if (perms != null) {
			for (String perm : perms) {
				for (String p : StringUtils.split(perm, ',')) {
					if (!StringUtils.isBlank(p)) {
						set.add(p);
					}
				}
			}
		}
		return set;
	}
	
	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsRoleMng manager;
	@Autowired
	private CmsUserMng userMng;
	@Autowired
	private CmsPlugMng plugMng;
	@Autowired
	private CmsAuthorizingRealm authorizingRealm;
}