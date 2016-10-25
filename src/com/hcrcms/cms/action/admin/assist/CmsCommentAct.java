package com.hcrcms.cms.action.admin.assist;

import static com.hcrcms.common.page.SimplePage.cpn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.manager.assist.CmsCommentExtMng;
import com.hcrcms.cms.manager.assist.CmsCommentMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserExt;
import com.hcrcms.core.manager.CmsLogMng;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.RandomUtils;
import com.hcrcms.core.web.util.RsortComment;
import com.hcrcms.core.web.util.SortComment;

@Controller
public class CmsCommentAct {
	private static final Logger log = LoggerFactory
			.getLogger(CmsCommentAct.class);

	@RequiresPermissions("comment:v_list")
	@RequestMapping("/comment/v_list.so")
	public String list(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		if (queryRecommend == null) {
			queryRecommend = false;
		}
		CmsSite site = CmsUtils.getSite(request);
		Pagination pagination = manager.getPage(site.getId(), queryContentId,
				null, queryChecked, queryRecommend, true, cpn(pageNo),
				CookieUtils.getPageSize(request));
		List<CmsComment> list = (List<CmsComment>) pagination.getList();
		//给回复做排序
		if (list != null && list.size() > 0){
			SortComment sortComment = new SortComment();
			RsortComment rsortComment = new RsortComment();
			for (CmsComment comment : list){
				List<CmsCommentExt> exts = new ArrayList<CmsCommentExt>(comment.getCommentExts());
				Collections.sort(exts,rsortComment);
				Collections.reverse(exts);
				if (exts != null && exts.size() > 0){
					List<List<CmsCommentExt>> sortExts = new ArrayList<List<CmsCommentExt>>();
					int rSort = -1;
					List<CmsCommentExt> optionExts = new ArrayList<CmsCommentExt>();
					for (CmsCommentExt commentExt : exts){
						if (commentExt.getReply() == null){
							optionExts.add(commentExt);
						}else{
							if (rSort == -1){
								rSort = commentExt.getRsort();
								optionExts.clear();
								optionExts.add(commentExt);
							}else{
								if (rSort == commentExt.getRsort()){
									rSort = commentExt.getRsort();
									optionExts.add(commentExt);
								}else{
									List<CmsCommentExt> optionTwoExts = new ArrayList<CmsCommentExt>();
									optionTwoExts.addAll(optionExts);
									Collections.sort(optionTwoExts,sortComment);
									sortExts.add(optionTwoExts);
									rSort = commentExt.getRsort();
									optionExts.clear();
									optionExts.add(commentExt);
								}
							}
						}
					}
					if (optionExts != null && optionExts.size() > 0){
						Collections.sort(optionExts,sortComment);
						sortExts.add(optionExts);
					}
					comment.setCommentLists(sortExts);
				}
				comment.setCommentContent(exts.get(0).getText());
			}
		}
		pagination.setList(list);
		model.addAttribute("pagination", pagination);
		model.addAttribute("list",list);
		model.addAttribute("queryContentId",queryContentId);
		return "comment/list";
	}

	@RequiresPermissions("comment:v_add")
	@RequestMapping("/comment/v_add.so")
	public String add(ModelMap model) {
		return "comment/add";
	}

	@RequiresPermissions("comment:v_edit")
	@RequestMapping("/comment/v_edit.so")
	public String edit(Integer id, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		model.addAttribute("cmsComment", manager.findById(id));
		return "comment/edit";
	}

	@RequiresPermissions("comment:o_update")
	@RequestMapping("/comment/o_update.so")
	public String update(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend,String reply, CmsComment bean, CmsCommentExt ext,
			Integer pageNo, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateUpdate(bean.getId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		//若回复内容不为空而且回复更新，则设置回复时间，已最新回复时间为准
		if(StringUtils.isNotBlank(ext.getReply())&&!reply.equals(ext.getReply())){
			bean.setReplayTime(new Date());
		}
		bean = manager.update(bean, ext);
		log.info("update CmsComment id={}.", bean.getId());
		cmsLogMng.operating(request, "cmsComment.log.update", "id="
				+ bean.getId());
		return list(queryContentId, queryChecked, queryRecommend, pageNo,
				request, model);
	}

	@RequiresPermissions("comment:o_delete")
	@RequestMapping("/comment/o_delete.so")
	public String delete(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.deleteByIds(ids);
		for (CmsComment bean : beans) {
			log.info("delete CmsComment id={}", bean.getId());
			cmsLogMng.operating(request, "cmsComment.log.delete", "id="
					+ bean.getId());
		}
		return list(queryContentId, queryChecked, queryRecommend, pageNo,
				request, model);
	}
	
	@RequiresPermissions("comment:o_deleteExt")
	@RequestMapping("/comment/o_deleteExt.so")
	public String deleteExt(Integer queryContentId, Boolean queryChecked,
			Boolean queryRecommend, Integer[] ids,Integer commentId,Integer sort,Integer rsort, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		commentExtMng.deleteCommentExt(commentId, sort, rsort);
		return list(queryContentId, queryChecked, queryRecommend, pageNo,
				request, model);
	}
	
	@RequiresPermissions("comment:o_check")
	@RequestMapping("/comment/o_check.so")
	public String check(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),true);
		for (CmsComment bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			//cmsLogMng.operating(request, "cmsComment.log.check", "id="
			//		+ bean.getId() + ";title=" + bean.getReplayHtml());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}
	
	@RequiresPermissions("comment:o_check_cancel")
	@RequestMapping("/comment/o_check_cancel.so")
	public String cancelCheck(Integer queryCtgId, Boolean queryRecommend,
			Boolean queryChecked, Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsComment[] beans = manager.checkByIds(ids,CmsUtils.getUser(request),false);
		for (CmsComment bean : beans) {
			log.info("delete CmsGuestbook id={}", bean.getId());
			//cmsLogMng.operating(request, "cmsComment.log.cancelCheck", "id="
			//		+ bean.getId() + ";title=" + bean.getReplayHtml());
		}
		return list(queryCtgId, queryRecommend, queryChecked, pageNo, request,
				model);
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
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
		CmsComment entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsComment.class, id)) {
			return true;
		}
		if (!entity.getSite().getId().equals(siteId)) {
			errors.notInSite(CmsComment.class, id);
			return true;
		}
		return false;
	}

	@Autowired
	private CmsLogMng cmsLogMng;
	@Autowired
	private CmsCommentMng manager;
	@Autowired
	private CmsCommentExtMng commentExtMng;
}