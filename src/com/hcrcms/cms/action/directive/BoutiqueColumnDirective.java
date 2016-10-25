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

import org.apache.commons.lang.StringUtils;

import com.hcrcms.cms.action.directive.abs.AbstractBoutiqueColumnDirective;
import com.hcrcms.cms.entity.main.JingPinSportAssist;
import com.hcrcms.cms.entity.main.JingPinSportSet;
import com.hcrcms.common.web.freemarker.DirectiveUtils;
import com.hcrcms.common.web.freemarker.DirectiveUtils.InvokeType;
import com.hcrcms.common.web.freemarker.ParamsRequiredException;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.DateUtils;
import com.hcrcms.core.web.util.FrontUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 精品栏目标签
 */
public class BoutiqueColumnDirective extends AbstractBoutiqueColumnDirective {
	/**
	 * 模板名称
	 */
	public static final String TPL_NAME = "boutique_list";

	/**
	 * 输入参数，文章ID。允许多个文章ID，用","分开。排斥其他所有筛选参数。
	 */
	public static final String PARAM_IDS = "ids";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		List<JingPinSportSet> list = getList(params, env);
		
		List<JingPinSportAssist> lists = getResult(list.get(0));
		
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
		paramWrap.put(OUT_LIST, DEFAULT_WRAPPER.wrap(lists));
		
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
	protected List<JingPinSportSet> getList(Map<String, TemplateModel> params,
			Environment env) throws TemplateException {
		return (List<JingPinSportSet>) super.getData(params, env);
	}

	public List<JingPinSportAssist> getResult(JingPinSportSet jingPingSportSet){
		List<JingPinSportAssist> assists = new ArrayList<JingPinSportAssist>();
		try {
			if (jingPingSportSet != null){
				int count = 1;
				while (count <= 7){
					for (String key : ChannelCacheUtils.jingPinSportMap.keySet()){
						int val = ChannelCacheUtils.jingPinSportMap.get(key);
						if (val == 1 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getTwentyfour());
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 2 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getZhouZhouKanStart()) + "-" + jingPingSportSet.getZhouZhoukanEnd().getDate();
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 3 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getTouRongZiStart()) + "-" + jingPingSportSet.getTouRongZiEnd().getDate();
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 4 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getDaJiaZhouYuLuStart()) + "-" + jingPingSportSet.getDaJiaZhouYuLuEnd().getDate();
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 5 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getZhouPinHuiStart()) + "-" + jingPingSportSet.getZhouPinHuiEnd().getDate();
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 6 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYMD(jingPingSportSet.getOneBookWeekStart()) + "-" + jingPingSportSet.getOneBookWeekEnd().getDate();
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
						if (val == 7 && val == count){
							JingPinSportAssist jpa = new JingPinSportAssist();
							jpa.setDescription(key);
							String timeDisp = DateUtils.convertYM(jingPingSportSet.getJiDuYuGaoStart()) + "-" + (jingPingSportSet.getJiDuYuGaoEnd().getMonth());
							jpa.setTimeFrame(timeDisp);
							jpa.setUrl("jingPinHuoDongCategory.htm?c=" + val);
							jpa.setUrlMore("jingPinHuoDongCategoryDetail.htm?c=" + val + "&stepLength=" + 100);
							assists.add(jpa);
							break;
						}
					}
					count++;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return assists;
	}
	
	@Override
	protected boolean isPage() {
		return false;
	}
}
