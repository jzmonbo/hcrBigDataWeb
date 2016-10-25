package com.hcrcms.cms.action.directive;

import static com.hcrcms.cms.Constants.TPL_STYLE_LIST;
import static com.hcrcms.cms.Constants.TPL_SUFFIX;
import static com.hcrcms.common.web.Constants.UTF8;
import static com.hcrcms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.hcrcms.core.web.util.FrontUtils.PARAM_STYLE_LIST;
import static freemarker.template.ObjectWrapper.DEFAULT_WRAPPER;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hcrcms.cms.action.directive.abs.AbstractInnerLinkDirective;
import com.hcrcms.cms.entity.main.ArticleBottomLink;
import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.cms.entity.main.HotSpotNavigation;
import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.cms.entity.main.TopLink;
import com.hcrcms.common.web.freemarker.DirectiveUtils;
import com.hcrcms.common.web.freemarker.DirectiveUtils.InvokeType;
import com.hcrcms.common.web.freemarker.ParamsRequiredException;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 内链列表标签
 */
public class InnerLinkListDirective extends AbstractInnerLinkDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "link_list";

	/**
	 * 输入参数，文章ID。允许多个文章ID，用","分开。排斥其他所有筛选参数。
	 */
	public static final String PARAM_IDS = "ids";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		List<HotSpotNavigation> hotSpotNavigations = null;
		List<TopLink> topLinks = null;
		List<SiteRightLink> siteRightLinks = null;
		List<ArticleTopLink> articleTopLinks = null;
		List<ArticleBottomLink> articleBottomLinks = null;
		
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
		String linkId = getLinkId(params);
		int linkid = Integer.parseInt(linkId);
		if (linkid == 1){								//热点导航
			hotSpotNavigations = getHotSpotNavigationList(params, env);
			paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(hotSpotNavigations));
		}
		if (linkid == 2){								//顶部搜索下方内链
			topLinks = getTopLinkList(params, env);
			paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(topLinks));
		}
		if (linkid == 3){								//整站右侧链接模块
			siteRightLinks = getSiteRightLinkList(params, env);
			paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(siteRightLinks));
		}
		if (linkid == 4){								//文章顶部链接
			articleTopLinks = getArticleTopLinkList(params, env);
			paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(articleTopLinks));
		}
		if (linkid == 5){								//文章正文底部链接
			articleBottomLinks = getArticleBottomLinkList(params, env);
			paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(articleBottomLinks));
		}
		
		Map<String, TemplateModel> origMap = DirectiveUtils
				.addParamsToVariable(env, paramWrap);
		InvokeType type = DirectiveUtils.getInvokeType(params);
		String listStyle = DirectiveUtils.getString(PARAM_STYLE_LIST, params);
		if (InvokeType.sysDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			env.include(TPL_STYLE_LIST + listStyle + TPL_SUFFIX, UTF8, true);
		} else if (InvokeType.userDefined == type) {
			if (StringUtils.isBlank(listStyle)) {
				throw new ParamsRequiredException(PARAM_STYLE_LIST);
			}
			FrontUtils.includeTpl(TPL_STYLE_LIST, site, env);
		} else if (InvokeType.custom == type) {
			FrontUtils.includeTpl(TPL_NAME, site, params, env);
		} else if (InvokeType.body == type) {
			body.render(env.getOut());
		} else {
			throw new RuntimeException("invoke type not handled: " + type);
		}
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}

	@SuppressWarnings("unchecked")
	protected List<HotSpotNavigation> getHotSpotNavigationList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<HotSpotNavigation>) super.getHotSpotNavigationData(params, env);
	}
	
	@SuppressWarnings("unchecked")
	protected List<TopLink> getTopLinkList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<TopLink>) super.getTopLinkData(params, env);
	}

	@SuppressWarnings("unchecked")
	protected List<SiteRightLink> getSiteRightLinkList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<SiteRightLink>) super.getSiteRightLinkData(params, env);
	}
	
	@SuppressWarnings("unchecked")
	protected List<ArticleTopLink> getArticleTopLinkList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<ArticleTopLink>) super.getAritcleTopLinkData(params, env);
	}
	
	@SuppressWarnings("unchecked")
	protected List<ArticleBottomLink> getArticleBottomLinkList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<ArticleBottomLink>) super.getArticleBottomLinkData(params, env);
	}
	
	@Override
	protected boolean isPage() {
		return false;
	}
}
