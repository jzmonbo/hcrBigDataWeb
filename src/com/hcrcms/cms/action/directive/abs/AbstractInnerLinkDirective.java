package com.hcrcms.cms.action.directive.abs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hcrcms.cms.entity.main.ArticleBottomLink;
import com.hcrcms.cms.entity.main.ArticleTopLink;
import com.hcrcms.cms.entity.main.HotSpotNavigation;
import com.hcrcms.cms.entity.main.SiteRightLink;
import com.hcrcms.cms.entity.main.TopLink;
import com.hcrcms.cms.manager.main.ArticleBottomLinkMng;
import com.hcrcms.cms.manager.main.ArticleTopLinkMng;
import com.hcrcms.cms.manager.main.HotSpotNavigationMng;
import com.hcrcms.cms.manager.main.SiteRightLinkMng;
import com.hcrcms.cms.manager.main.TopLinkMng;
import com.hcrcms.common.web.freemarker.DirectiveUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 专家标签基类
 */
public abstract class AbstractInnerLinkDirective implements
	TemplateDirectiveModel{

	/**
	 * 输入参数，栏目选项。用于单栏目情况下。0：自身栏目；1：包含子栏目；2：包含副栏目。
	 */
	public static final String PARAM_CHANNEL_OPTION = "channelOption";
	/**
	 * 输入参数，栏目ID。允许多个栏目ID，用","分开。和channelPath之间二选一，ID优先级更高。
	 */
	public static final String PARAM_CHANNEL_ID = "channelId";
	/**
	 * 输入参数，标题。可以为null。
	 */
	public static final String PARAM_TITLE = "title";
	/**
	 * 输入参数，站点ID。可选。允许多个站点ID，用","分开。
	 */
	public static final String PARAM_SITE_ID = "siteId";
	/**
	 * 输入参数，类型ID。可选。允许多个类型ID,用","分开。
	 */
	public static final String PARAM_TYPE_ID = "typeId";
	/**
	 * 输入参数，推荐。0：所有；1：推荐；2：不推荐。默认所有。
	 */
	public static final String PARAM_RECOMMEND = "recommend";
	/**
	 * 输入参数，标题图片。0：所有；1：有；2：没有。默认所有。
	 */
	public static final String PARAM_IMAGE = "image";
	/**
	 * 输入参数，内链ID
	 */
	public static final String PARAM_LINK_ID = "linkId";
	/**
	 * 输入参数，作家名字
	 */
	public static final String PARAM_AUTHOR = "author";
	/**
	 * 输入参数，排序方式。
	 * <ul>
	 * <li>0：ID降序
	 * <li>1：ID升序
	 * <li>2：发布时间降序
	 * <li>3：发布时间升序
	 * <li>4：固定级别降序,发布时间降序
	 * <li>5：固定级别降序,发布时间升序
	 * 
	 * <li>6：日访问降序（推荐）
	 * <li>7：周访问降序
	 * <li>8：月访问降序
	 * <li>9：总访问降序
	 * 
	 * <li>10：日评论降序（推荐）
	 * <li>11：周评论降序
	 * <li>12：月评论降序
	 * <li>13：总评论降序
	 * 
	 * <li>14：日下载降序（推荐）
	 * <li>15：周下载降序
	 * <li>16：月下载降序
	 * <li>17：总下载降序
	 * 
	 * <li>18：周顶降序（推荐）
	 * <li>19：周顶降序
	 * <li>20：周顶降序
	 * <li>21：周顶降序
	 * </ul>
	 */
	public static final String PARAM_ORDER_BY = "orderBy";
	/**
	 * 自定义字段前缀(类似string_author)
	 */
	public static final String PARAM_ATTR_STRING_PERFIX = "s_";
	/**
	 * 自定义字段运算操作前缀
	 */
	public static final String PARAM_ATTR_OPERATE_PREFIX = "o_";
	/**
	 * （start左包含，end右包含，like包含，eq等于，gt大于，gte大于等于，lt小于，lte小于等于，默认等于）
	 */
	public static final String PARAM_ATTR_START = "start";
	public static final String PARAM_ATTR_END = "end";
	public static final String PARAM_ATTR_LIKE = "like";
	public static final String PARAM_ATTR_EQ = "eq";
	public static final String PARAM_ATTR_GT = "gt";
	public static final String PARAM_ATTR_GTE = "gte";
	public static final String PARAM_ATTR_LT = "lt";
	public static final String PARAM_ATTR_LTE = "lte";
	
	protected Boolean getHasTitleImg(Map<String, TemplateModel> params)
			throws TemplateException {
		String titleImg = DirectiveUtils.getString(PARAM_IMAGE, params);
		if ("1".equals(titleImg)) {
			return true;
		} else if ("2".equals(titleImg)) {
			return false;
		} else {
			return null;
		}
	}
	
	protected int getChannelOption(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer option = DirectiveUtils.getInt(PARAM_CHANNEL_OPTION, params);
		if (option == null || option < 0 || option > 2) {
			return 0;
		} else {
			return option;
		}
	}
	
	/*protected Integer[] getChannelIds(Map<String, TemplateModel> params)
		throws TemplateException {
	Integer[] ids = DirectiveUtils.getIntArray(PARAM_CHANNEL_ID, params);
	if (ids != null && ids.length > 0) {
		return ids;
	} else {
		return null;
	}
	}*/
	
	protected Integer[] getChannelIds(Map<String, TemplateModel> params)
		throws TemplateException {
		Integer[] ids = DirectiveUtils.getChannelArray(PARAM_CHANNEL_ID, params);
		if (ids != null && ids.length > 0) {
			return ids;
		} else {
			return null;
		}
	}
	
	protected String getLinkId(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getString(PARAM_LINK_ID, params);
	}
	
	protected String getAuthor(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getString(PARAM_AUTHOR, params);
	}
	
	protected String getTitle(Map<String, TemplateModel> params)
			throws TemplateException {
		return DirectiveUtils.getString(PARAM_TITLE, params);
	}
	
	protected Integer[] getSiteIds(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer[] siteIds = DirectiveUtils.getIntArray(PARAM_SITE_ID, params);
		return siteIds;
	}
	
	protected Integer[] getTypeIds(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer[] typeIds = DirectiveUtils.getIntArray(PARAM_TYPE_ID, params);
		return typeIds;
	}
	
	protected Boolean getRecommend(Map<String, TemplateModel> params)
			throws TemplateException {
		String recommend = DirectiveUtils.getString(PARAM_RECOMMEND, params);
		if ("1".equals(recommend)) {
			return true;
		} else if ("2".equals(recommend)) {
			return false;
		} else {
			return null;
		}
	}
	
	protected int getOrderBy(Map<String, TemplateModel> params)
			throws TemplateException {
		Integer orderBy = DirectiveUtils.getInt(PARAM_ORDER_BY, params);
		if (orderBy == null) {
			return 0;
		} else {
			return orderBy;
		}
	}
	
	/**
	 * 返回key为字段名 value为字段值、字段类型、查询操作（大于或者等于等）
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	protected Map<String,String[]> getAttrMap(Map<String, TemplateModel> params)
	throws TemplateException {
		Set<String>keys = DirectiveUtils.getKeysByPrefix(PARAM_ATTR_STRING_PERFIX, params);
		Map<String,String[]>attrStringMap=new HashMap<String, String[]>();
		if (keys==null) {
			return null;
		}
		for(String key:keys){
			String value=DirectiveUtils.getString(key, params);
			key=key.split(PARAM_ATTR_STRING_PERFIX)[1];
			String operate=DirectiveUtils.getString(PARAM_ATTR_OPERATE_PREFIX+key, params);
			//默认操作等于
			if(StringUtils.isBlank(operate)){
				operate=PARAM_ATTR_EQ;
			}
			String[] mapValue=new String[]{value,operate};
			//去除前缀
			attrStringMap.put(key, mapValue);
		}
		return attrStringMap;
	}

	//热点导航
	protected List<HotSpotNavigation> getHotSpotNavigationData(Map<String, TemplateModel> params, Environment env)
			throws TemplateException {
		List<HotSpotNavigation> hotSpotNavigations = cmsHotSpotNavigationMng.getList();
		return hotSpotNavigations;
	}
	//顶部搜索下方内链
	protected Object getTopLinkData(Map<String, TemplateModel> params, Environment env)
			throws TemplateException {
		List<TopLink> topLinks = topLinkMng.getList();
		return topLinks;
	}
	//整站右侧链接模块
	protected Object getSiteRightLinkData(Map<String, TemplateModel> params, Environment env)
			throws TemplateException {
		List<SiteRightLink> siteRightLinks = siteRightLinkMng.getList();
		return siteRightLinks;
	}
	//文章顶部链接
	protected Object getAritcleTopLinkData(Map<String, TemplateModel> params, Environment env)
			throws TemplateException {
		List<ArticleTopLink> articleTopLinks = articleTopLinkMng.getList();
		return articleTopLinks;
	}
	//文章正文底部链接
	protected Object getArticleBottomLinkData(Map<String, TemplateModel> params, Environment env)
			throws TemplateException {
		List<ArticleBottomLink> articleBottomLinks = articleBottomLinkMng.getList();
		return articleBottomLinks;
	}
	
	abstract protected boolean isPage();
	
	@Autowired
	protected HotSpotNavigationMng cmsHotSpotNavigationMng;
	@Autowired
	protected TopLinkMng topLinkMng;
	@Autowired
	protected SiteRightLinkMng siteRightLinkMng;
	@Autowired
	protected ArticleTopLinkMng articleTopLinkMng;
	@Autowired
	protected ArticleBottomLinkMng articleBottomLinkMng;
}
