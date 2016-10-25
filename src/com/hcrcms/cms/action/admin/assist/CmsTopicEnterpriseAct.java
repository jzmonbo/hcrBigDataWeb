package com.hcrcms.cms.action.admin.assist;

import static com.hcrcms.common.page.SimplePage.cpn;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcrcms.cms.entity.assist.CmsTopicEnterprise;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.manager.assist.CmsTopicEnterpriseMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicContentMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.CookieUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;

@Controller
public class CmsTopicEnterpriseAct {
	private static final Logger log = LoggerFactory.getLogger(CmsTopicEnterpriseAct.class);

	@RequiresPermissions("topicenterprise:v_list")
	@RequestMapping("/topicenterprise/v_list.so")
	public String list(Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils
				.getPageSize(request));
		List<CmsSpecialTopic> specialTopics = (List<CmsSpecialTopic>) pagination.getList();
		List<CmsTopicEnterprise>  enterprises = enterpriseMng.getListForTag(null, true, 0);
		if (enterprises != null && enterprises.size() > 0){
			for (CmsSpecialTopic specialTopic : specialTopics){
				for (CmsTopicEnterprise enterprise : enterprises){
					if (specialTopic.getId() == enterprise.getMeetId()){
						specialTopic.setHasTemplate(enterprise.getEntId());
						break;
					}
				}
			}
		}
		model.addAttribute("pagination", specialTopics);
		return "topicenterprise/list";
	}

	@RequiresPermissions("topicenterprise:v_add")
	@RequestMapping("/topicenterprise/v_add.so")
	public String add(Integer mid,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsSpecialTopic specialTopic = manager.findById(mid);
		if (specialTopic.getSpecialTopicContents() != null){
			List<CmsSpecialTopicContent> cmsSpecialShares = new ArrayList<CmsSpecialTopicContent>();
			List<CmsSpecialTopicContent> cmsSpecialInterviews = new ArrayList<CmsSpecialTopicContent>();
			for (CmsSpecialTopicContent content : specialTopic.getSpecialTopicContents()){
				if (content.getTopicType() == 1){
					cmsSpecialShares.add(content);
				}else if (content.getTopicType() == 0){
					cmsSpecialInterviews.add(content);
				}
			}
			model.addAttribute("cmsSpecialShares",cmsSpecialShares);
			model.addAttribute("cmsSpecialInterviews",cmsSpecialInterviews);
		}
		model.addAttribute("specialTopic",specialTopic);
		return "topicenterprise/add";
	}

	@RequiresPermissions("topicenterprise:v_edit")
	@RequestMapping("/topicenterprise/v_edit.so")
	public String edit(Integer id,CmsTopicEnterprise bean, HttpServletRequest request, ModelMap model) {
		try {
			/*WebErrors errors = validateEdit(id, request);
				if (errors.hasErrors()) {
					return errors.showErrorPage(model);
				}*/
			CmsTopicEnterprise topicEnterprise = enterpriseMng.findById(id);
			String shareTopic = topicEnterprise.getShareTopic();
			String interview = topicEnterprise.getInterview();
			CmsSpecialTopic specialTopic = manager.findById(topicEnterprise
					.getMeetId());
			if (specialTopic.getSpecialTopicContents() != null) {
				List<CmsSpecialTopicContent> cmsSpecialShares = new ArrayList<CmsSpecialTopicContent>();
				List<CmsSpecialTopicContent> cmsSpecialInterviews = new ArrayList<CmsSpecialTopicContent>();
				List<CmsSpecialTopicContent> cmsSpecialBigShares = new ArrayList<CmsSpecialTopicContent>();
				for (CmsSpecialTopicContent content : specialTopic
						.getSpecialTopicContents()) {
					if (content.getTopicType() == 1) {
						cmsSpecialShares.add(content);
						cmsSpecialBigShares.add(cloneContent(content));
					} else if (content.getTopicType() == 0) {
						cmsSpecialInterviews.add(content);
					}
				}
				if (cmsSpecialShares != null && cmsSpecialShares.size() > 0
						&& shareTopic != null && shareTopic.length() > 0) {
					//标识专题文章是否被选中
					String[] shares = shareTopic.split(",");
					for (String s : shares) {
						int share = Integer.parseInt(s);
						for (CmsSpecialTopicContent content : cmsSpecialShares) {
							if (share == content.getId()) {
								content.setIsSelect(1);
								break;
							}
						}
					}
				}
				if (cmsSpecialBigShares != null
						&& cmsSpecialBigShares.size() > 0) {
					//标识专题文章是否被选中
					String[] shares = topicEnterprise.getLongVideo().split(",");
					for (String s : shares) {
						int share = Integer.parseInt(s);
						for (CmsSpecialTopicContent content : cmsSpecialBigShares) {
							if (share == content.getId()) {
								content.setIsSelect(1);
								break;
							}
						}
					}
				}
				if (cmsSpecialInterviews != null
						&& cmsSpecialInterviews.size() > 0 && interview != null
						&& interview.length() > 0) {
					//标识专题文章是否被选中
					String[] views = interview.split(",");
					for (String v : views) {
						int interw = Integer.parseInt(v);
						for (CmsSpecialTopicContent content : cmsSpecialInterviews) {
							if (interw == content.getId()) {
								content.setIsSelect(1);
								break;
							}
						}
					}
				}
				model.addAttribute("cmsSpecialShares", cmsSpecialShares);
				model.addAttribute("cmsSpecialInterviews", cmsSpecialInterviews);
				model.addAttribute("cmsSpecialBigShares", cmsSpecialBigShares);
			}
			model.addAttribute("cmsTopicEnterprise", topicEnterprise);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "topicenterprise/edit";
	}

	@RequiresPermissions("topicenterprise:o_save")
	@RequestMapping("/topicenterprise/o_save.so")
	public String save(Integer mid,Integer[] shareids,Integer[] sharebigids,Integer[] interviewids,CmsTopicEnterprise bean,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		bean.setMeetId(mid);
		CmsSpecialTopic specialTopic = manager.findById(mid);
		bean.setTopicName(specialTopic.getTopicName());
		if (shareids != null){
			bean.setShareTopic(pinString(shareids));
		}else{
			bean.setShareTopic("");
		}
		if (sharebigids != null && sharebigids.length > 0){
			bean.setLongVideo(""+sharebigids[0]);
		}
		if (interviewids != null){
			bean.setInterview(pinString(interviewids));
		}else{
			bean.setInterview("");
		}
		if (bean.getDescription() == null){
			bean.setDescription("");
		}
		enterpriseMng.save(bean,null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("topicenterprise:o_update")
	@RequestMapping("/topicenterprise/o_update.so")
	public String update(CmsTopicEnterprise bean,
			Integer pageNo,HttpServletRequest request, ModelMap model) {
		if (bean.getLongVideo() != null){
			bean.setLongVideo(bean.getLongVideo().replace(",",""));
		}
		enterpriseMng.update(bean, null);
		return "redirect:v_list.so";
	}

	@RequiresPermissions("topicenterprise:o_delete")
	@RequestMapping("/topicenterprise/o_delete.so")
	public String delete(Integer[] ids, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		/*WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}*/
		enterpriseMng.deleteByIds(ids);
		return list(pageNo, request, model);
	}

	/**
	 * 把数组拼接成字符串
	 * @return
	 */
	public String pinString(Integer[] ids){
		String result = null;
		if (ids != null && ids.length > 0){
			StringBuilder sb = new StringBuilder();
			for (Integer id : ids){
				sb.append(id).append(",");
			}
			if (sb.length() > 0){
				sb.setLength(sb.length()-1);
			}
			result = sb.toString();
		}
		return result;
	}
	
	private WebErrors validateSave(CmsTopic bean, HttpServletRequest request) {
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
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, errors);
		}
		return errors;
	}

	private WebErrors validatePriority(Integer[] wids, Integer[] priority,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifEmpty(wids, "wids")) {
			return errors;
		}
		if (errors.ifEmpty(priority, "priority")) {
			return errors;
		}
		if (wids.length != priority.length) {
			errors.addErrorString("wids length not equals priority length");
			return errors;
		}
		for (int i = 0, len = wids.length; i < len; i++) {
			if (vldExist(wids[i], errors)) {
				return errors;
			}
			if (priority[i] == null) {
				priority[i] = 0;
			}
		}
		return errors;
	}

	private boolean vldExist(Integer id, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		CmsSpecialTopic entity = manager.findById(id);
		if (errors.ifNotExist(entity, CmsTopic.class, id)) {
			return true;
		}
		return false;
	}
	
	private CmsSpecialTopicContent cloneContent(CmsSpecialTopicContent content){
		CmsSpecialTopicContent ccontent = new CmsSpecialTopicContent();
		ccontent.setAuthor(content.getAuthor());
		ccontent.setBigPictureImg(content.getBigPictureImg());
		ccontent.setContent(content.getContent());
		ccontent.setContentId(content.getContentId());
		ccontent.setContentImg(content.getContentImg());
		ccontent.setDescription(content.getDescription());
		ccontent.setId(content.getId());
		ccontent.setIsBold(content.getIsBold());
		ccontent.setIsSelect(content.getIsSelect());
		ccontent.setLink(content.getLink());
		ccontent.setMainPlace(content.getMainPlace());
		ccontent.setMediaPath(content.getMediaPath());
		ccontent.setMediaType(content.getMediaType());
		ccontent.setNeedRegenerate(content.getNeedRegenerate());
		ccontent.setOrigin(content.getOrigin());
		ccontent.setOriginUrl(content.getOriginUrl());
		ccontent.setReleaseDate(content.getReleaseDate());
		ccontent.setShortTitle(content.getShortTitle());
		ccontent.setSiteLocation(content.getSiteLocation());
		ccontent.setSpecialTopic(content.getSpecialTopic());
		ccontent.setSubPlace(content.getSubPlace());
		ccontent.setTitle(content.getTitle());
		ccontent.setTitleColor(content.getTitleColor());
		ccontent.setTitleImg(content.getTitleImg());
		ccontent.setTopicType(content.getTopicType());
		ccontent.setTplContent(content.getTplContent());
		ccontent.setTypeImg(content.getTypeImg());
		ccontent.setWatermarkerImg(content.getWatermarkerImg());
		return ccontent;
	}
	
	@Autowired
	private CmsSpecialTopicMng manager;
	@Autowired
	private CmsTopicEnterpriseMng enterpriseMng;
	@Autowired
	private CmsSpecialTopicContentMng cmsSpecialTopicContentMng;
}