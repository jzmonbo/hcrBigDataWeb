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

import com.hcrcms.cms.action.directive.abs.AbstractTagsDirective;
import com.hcrcms.cms.entity.main.CmsTags;
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
 * 内容列表标签
 */
public class CmsTagsListDirective extends AbstractTagsDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "content_list";

	/**
	 * 输入参数，文章ID。允许多个文章ID，用","分开。排斥其他所有筛选参数。
	 */
	public static final String PARAM_IDS = "ids";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		List<CmsTags> list = getList(params, env);
		
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
		paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(list));
		paramWrap.put("totals", DEFAULT_WRAPPER.wrap(list!=null?list.size():0));
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

	/*@SuppressWarnings("unchecked")
	protected List<Content> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		Integer[] ids = DirectiveUtils.getIntArray(PARAM_IDS, params);
		if (ids != null) {
			return contentMng.getListByIdsForTag(ids, getOrderBy(params));
		} else {
			return (List<Content>) super.getData(params, env);
		}
	}*/
	
	@SuppressWarnings("unchecked")
	protected List<CmsTags> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<CmsTags>) super.getData(params, env);
	}

	@Override
	protected boolean isPage() {
		return false;
	}
}
