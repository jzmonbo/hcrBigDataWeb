package com.hcrcms.cms.action.directive;

import static com.hcrcms.cms.Constants.TPL_STYLE_LIST;
import static com.hcrcms.cms.Constants.TPL_SUFFIX;
import static com.hcrcms.common.web.Constants.UTF8;
import static com.hcrcms.common.web.freemarker.DirectiveUtils.OUT_LIST;
import static com.hcrcms.core.web.util.FrontUtils.PARAM_STYLE_LIST;
import static freemarker.template.ObjectWrapper.DEFAULT_WRAPPER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.hcrcms.cms.action.directive.abs.AbstractContentDirective;
import com.hcrcms.cms.entity.main.Content;
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
public class ContentListDirective extends AbstractContentDirective {
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
		List<Content> list = getList(params, env);
		
		String contentId = DirectiveUtils.getString(PARAM_CONTENT_ID, params);
		
		//如果传入参数中有文章ID，则查询同类文章需要排除当前文章
		if (contentId != null){
			Content content = null;
			if (list != null && list.size() > 0){
				for (Content con : list){
					if (contentId.equals(""+con.getId())){
						content = con;
						break;
					}
				}
			}
			if (content != null){
				list.remove(content);
			}
		}
		
		if ("新闻咨讯".equals(params.get("channelId").toString())){
			List<Content> lists = new ArrayList<Content>();
			if (list != null && list.size() > 0){
				Content content = null;
				for (Content con : list){
					if (con.getIsPictureTop() == 2){
						content = con;
						break;
					}
				}
				if (content != null){
					list.remove(content);
					lists.add(content);
					lists.addAll(list);
					list = lists;
				}
			}
		}
		if ("人物专访".equals(params.get("channelId").toString())){
			List<Content> rList = new ArrayList<Content>();
			if (list != null && list.size() > 0){
				Content content = null;
				for (Content con : list){
					if (con.getChannel().getId() == 84 && con.getSpecialTopicContent().getTopicType() == 1){
						content = con;
						rList.add(content);
					}
				}
				list.removeAll(rList);
			}
		}
		//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
		if (list != null && list.size() > 0){
			String regex = ".*[a-zA-Z]+.*";
			Matcher m = null;
			for (Content content : list){
				if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
					m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
					if (m.matches()){
						content.setIncludeLetter(1);
					}else{
						content.setIncludeLetter(0);
					}
				}else{
					content.setIncludeLetter(0);
				}
			}
		}
		
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(list));
		paramWrap.put("totals", DEFAULT_WRAPPER.wrap(list!=null?list.size():0));
		//下面把文章最大和最小ID放在消息中,方便页面处理刷新文章用
		if (list != null && list.size() > 0){
			int maxId = list.get(0).getId();
			if (list.size() > 1 && list.get(0).getId() < list.get(1).getId()){
				maxId = list.get(1).getId();
			}
			int minId = list.get(list.size()-1).getId();
			paramWrap.put("maxLengthId", DEFAULT_WRAPPER.wrap(maxId));
			paramWrap.put("minLengthId", DEFAULT_WRAPPER.wrap(minId));
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
	protected List<Content> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<Content>) super.getData(params, env);
	}

	@Override
	protected boolean isPage() {
		return false;
	}
}
