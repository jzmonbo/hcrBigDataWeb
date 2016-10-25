package com.hcrcms.cms.dao.main.impl;


import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_END;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_EQ;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GT;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GTE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LIKE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LT;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LTE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_START;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.all;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.checked;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.contribute;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.draft;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.passed;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.prepared;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.recycle;
import static com.hcrcms.cms.entity.main.Content.ContentStatus.rejected;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.ContentDao;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Content.ContentStatus;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.web.util.DateUtils;

@Repository
public class ContentDaoImpl extends HibernateBaseDao<Content, Integer>
		implements ContentDao {
	public Pagination getPage(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize) {
		Finder f = Finder.create("select  bean from Content bean ");
		if (prepared == status || passed == status || rejected == status) {
			f.append(" join bean.contentCheckSet check");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
		} else if (siteId != null) {
			f.append(" where bean.site.id=:siteId  ");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		if (prepared == status) {
			f.append(" and check.checkStep<:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (passed == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (rejected == status) {
			//退回只有本级可以查看
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=true");
			f.setParam("checkStep", checkStep);
		}
		if(modelId!=null){
			f.append(" and bean.model.id=:modelId").setParam("modelId", modelId);
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		appendOrder(f, orderBy);
		return find(f, pageNo, pageSize);
	}
	

	//只能管理自己的数据不能审核他人信息
	public Pagination getPageBySelf(String title, Integer typeId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId, Integer userId, int orderBy, int pageNo,
			int pageSize) {
		Finder f = Finder.create("select  bean from Content bean");
		if (prepared == status || passed == status || rejected == status) {
			f.append(" join bean.contentCheckSet check");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
		}else if (siteId != null) {
			f.append(" where bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		f.append(" and bean.user.id=:userId");
		f.setParam("userId", userId);
		if (prepared == status) {
			f.append(" and check.checkStep<:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (passed == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (rejected == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=true");
			f.setParam("checkStep", checkStep);
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		if (prepared == status) {
			f.append(" order by check.checkStep desc,bean.id desc");
		} else {
			appendOrder(f, orderBy);
		}
		return find(f, pageNo, pageSize);
	}

	public Pagination getPageByRight(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId, Integer userId, boolean selfData, int orderBy,
			int pageNo, int pageSize) {
		Finder f = Finder.create("select  bean from Content bean ");
		if (prepared == status || passed == status || rejected == status) {
			f.append(" join bean.contentCheckSet check");
		}
		f.append(" join bean.channel channel join channel.users user");
		if (channelId != null) {
			f.append(",Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
			f.append(" and user.id=:userId");
			f.setParam("userId", userId);
		} else if (siteId != null) {
			f.append(" where user.id=:userId");
			f.setParam("userId", userId);
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where user.id=:userId");
			f.setParam("userId", userId);
		}
		if (selfData) {
			// userId前面已赋值
			f.append(" and bean.user.id=:userId");
		}
		if (prepared == status) {
			f.append(" and check.checkStep<:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (passed == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (rejected == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=true");
			f.setParam("checkStep", checkStep);
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		if (prepared == status) {
			f.append(" order by check.checkStep desc,bean.id desc");
		} else {
			appendOrder(f, orderBy);
		}
		return find(f, pageNo, pageSize);
	}
	

	public Pagination getPageForCollection(Integer siteId, Integer memberId, int pageNo, int pageSize){
		Finder f = Finder.create("select bean from Content bean join bean.collectUsers user where user.id=:userId").setParam("userId", memberId);
		if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		f.append(" and bean.status<>:status");
		f.setParam("status", ContentCheck.RECYCLE);
		return find(f, pageNo, pageSize);
	}

	private void appendQuery(Finder f, String title, Integer typeId,
			Integer inputUserId, ContentStatus status, boolean topLevel,
			boolean recommend) {
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (inputUserId != null) {
			f.append(" and bean.user.id=:inputUserId");
			f.setParam("inputUserId", inputUserId);
		}
		if (topLevel) {
			f.append(" and bean.topLevel>0");
		}
		if (recommend) {
			f.append(" and bean.recommend=true");
		}
		if (draft == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.DRAFT);
		}if (contribute == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CONTRIBUTE);
		} else if (checked == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKED);
		} else if (prepared == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKING);
		} else if (rejected == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.REJECT);
		}else if (passed == status) {
			f.append(" and (bean.status=:checking or bean.status=:checked)");
			f.setParam("checking", ContentCheck.CHECKING);
			f.setParam("checked", ContentCheck.CHECKED);
		} else if (all == status) {
			f.append(" and bean.status<>:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else if (recycle == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else {
			// never
		}
	}
	

	public Content getSide(Integer id, Integer siteId, Integer channelId,
			boolean next, boolean cacheable) {
		Finder f = Finder.create("from Content bean where 1=1");
		if (channelId != null) {
			f.append(" and bean.channel.id=:channelId");
			f.setParam("channelId", channelId);
		} else if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if (next) {
			f.append(" and bean.id>:id");
			f.setParam("id", id);
			f.append(" and bean.status=" + ContentCheck.CHECKED);
			f.append(" order by bean.id asc");
		} else {
			f.append(" and bean.id<:id");
			f.setParam("id", id);
			f.append(" and bean.status=" + ContentCheck.CHECKED);
			f.append(" order by bean.id desc");
		}
		Query query = f.createQuery(getSession());
		query.setCacheable(cacheable).setMaxResults(1);
		return (Content) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByIdsForTag(Integer[] ids, int orderBy) {
		Finder f = Finder.create("from Content bean where bean.id in (:ids)");
		f.setParamList("ids", ids);
		appendOrder(f, orderBy);
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,
				attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,attr,
				orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr, int orderBy, int option,int pageNo, int pageSize,String isPhone) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, attr,orderBy, option,pageSize,isPhone);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, Integer first, Integer count,String isPhone) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, attr,orderBy, option,count,isPhone);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title, Map<String,String[]>attr,int orderBy, int pageNo,
			int pageSize) {
		Finder f = byChannelPaths(paths, siteIds, typeIds, titleImg, recommend,
				title,attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,Map<String,String[]>attr, int orderBy, Integer first,
			Integer count) {
		Finder f = byChannelPaths(paths, siteIds, typeIds, titleImg, recommend,
				title, attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			int pageNo, int pageSize) {
		Finder f = byTopicId(topicId, siteIds, channelIds, typeIds, titleImg,
				recommend, title, attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title,Map<String,String[]>attr, int orderBy,
			Integer first, Integer count) {
		Finder f = byTopicId(topicId, siteIds, channelIds, typeIds, titleImg,
				recommend, title, attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize) {
		Finder f = byTagIds(tagIds, siteIds, channelIds, typeIds, excludeId,
				titleImg, recommend, title,attr, orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count) {
		Finder f = byTagIds(tagIds, siteIds, channelIds, typeIds, excludeId,
				titleImg, recommend, title,attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByTagIdsForTag(String[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count) {
		Finder f = byTagIdsByTags(tagIds, siteIds, channelIds, typeIds, excludeId,
				titleImg, recommend, title,attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}
	
	private Finder bySiteIds(Integer[] siteIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create("select  bean from Content bean");
		f.append(" join bean.contentExt as ext where 1=1");
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		//appendReleaseDate(f);
		appendSortDate(f);
		appendTypeIds(f, typeIds);
		appendSiteIds(f, siteIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	/*private Finder byChannelIds(Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			int option) {
		Finder f = Finder.create();
		int len = channelIds.length;
		// 如果多个栏目
		if (option == 0 || len > 1) {
			f.append("select  bean from Content bean ");
			f.append(" join bean.contentExt as ext");
			if (len == 1) {
				f.append(" where bean.channel.id=:channelId ");
				f.setParam("channelId", channelIds[0]);
			} else {
				f.append(" where bean.channel.id in (:channelIds) ");
				f.setParamList("channelIds", channelIds);
			}
		} else if (option == 1) {
			// 包含子栏目
			f.append("select  bean from Content bean");
			f.append(" join bean.contentExt as ext");
			f.append(" join bean.channel node,Channel parent");
			f.append(" where node.lft between parent.lft and parent.rgt");
			f.append(" and bean.site.id=parent.site.id");
			f.append(" and parent.id=:channelId ");
			f.setParam("channelId", channelIds[0]);
		} else if (option == 2) {
			// 包含副栏目
			f.append("select  bean from Content bean");
			f.append(" join bean.contentExt as ext");
			f.append(" join bean.channels as channel");
			f.append(" where channel.id=:channelId ");
			f.setParam("channelId", channelIds[0]);
		} else {
			throw new RuntimeException("option value must be 0 or 1 or 2.");
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}*/
	
	private Finder byChannelIds(Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			int option,int count,String isPhone) {
		Finder f = Finder.create();
		f.append("select  bean from Content bean ");
		f.append(" join bean.contentExt as ext");
		if (channelIds[1] == 1){			//首页大图
			f.append(" where bean.channel.id=:channelId and bean.isBigPicture=:isBigPicture");
			f.setParam("channelId", channelIds[0]);
			f.setParam("isBigPicture", channelIds[1]);
		}else if (channelIds[1] == 0){		//普通资讯
			if ("1".equals(isPhone)){		//手机端只显示新闻
				f.append(" where bean.channel.id in (70,84) ");
			}else{
				//首页里只显示新闻资讯和专题文章
				if (channelIds[0] == 70){
					f.append(" where bean.channel.id in (70,74,75,84) ");
				}else if (channelIds[0] == 75){
					//专访位置只显示专访和专题文章
					f.append(" where bean.channel.id in (75,84) ");
				}else{
					f.append(" where bean.channel.id=:channelId ");
					f.setParam("channelId", channelIds[0]);
				}
			}
		}else if (channelIds[1] == 2){		//指数排行大图
			f.append(" where bean.channel.id=:channelId and bean.isBigPicture=:isBigPicture");
			f.setParam("channelId", channelIds[0]);
			f.setParam("isBigPicture", channelIds[1]);
		}
		if (channelIds[1] == 0){
			if (channelIds[0] == 70){
				appendSortDate(f);
			}
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		//appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		if (channelIds[0] == 75){											//首页中专访需要包含人物专访和专题专访,按推荐排序
			if (count == 1){
				f.append(" and ((bean.channel.id=84 and bean.recommandView=1) or (bean.channel.id=75)) order by bean.recommandView desc, bean.sortDate desc");
			}else{
				f.append(" order by bean.sortDate desc");
			}
		}else{
			appendOrder(f, orderBy);
		}
		//appendIsPictureTop(f);											//文章列表按图片置顶降序
		return f;
	}

	private Finder byChannelPaths(String[] paths, Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		f.append("select  bean from Content bean join bean.channel channel ");
		f.append(" join bean.contentExt as ext");
		int len = paths.length;
		if (len == 1) {
			f.append(" where channel.path=:path ").setParam("path", paths[0]);
		} else {
			f.append(" where channel.path in (:paths)");
			f.setParamList("paths", paths);
		}
		if (siteIds != null) {
			len = siteIds.length;
			if (len == 1) {
				f.append(" and channel.site.id=:siteId ");
				f.setParam("siteId", siteIds[0]);
			} else if (len > 1) {
				f.append(" and channel.site.id in (:siteIds)");
				f.setParamList("siteIds", siteIds);
			}
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byTopicId(Integer topicId, Integer[] siteIds,
			Integer[] channelIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title, Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		f.append("select bean from Content bean join bean.topics topic");
		f.append(" join bean.contentExt as ext");
		f.append(" where topic.id=:topicId").setParam("topicId", topicId);
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendChannelIds(f, channelIds);
		appendSiteIds(f, siteIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byTagIds(Integer[] tagIds, Integer[] siteIds,
			Integer[] channelIds, Integer[] typeIds, Integer excludeId,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		int len = tagIds.length;
		if (len == 1) {
			f.append("select bean from Content bean join bean.tags tag");
			f.append(" join bean.contentExt as ext");
			f.append(" where tag.id=:tagId").setParam("tagId", tagIds[0]);
		} else {
			f.append("select bean from Content bean");
			f.append(" join bean.contentExt as ext");
			f.append(" join bean.tags tag");
			f.append(" where tag.id in(:tagIds)");
			f.setParamList("tagIds", tagIds);
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendChannelIds(f, channelIds);
		appendSiteIds(f, siteIds);
		if (excludeId != null) {
			f.append(" and bean.id<>:excludeId");
			f.setParam("excludeId", excludeId);
		}
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byTagIdsByTags(String[] tagIds, Integer[] siteIds,
			Integer[] channelIds, Integer[] typeIds, Integer excludeId,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		int len = tagIds.length;
		if (len == 1) {
			f.append("select bean from Content bean ");
			//f.append(" where tag.id=:tagId").setParam("tagId", tagIds[0]);
			f.append(" where bean.contentExt.title like '%" + tagIds[0] + "%' ");
		} else {
			f.append("select bean from Content bean");
			f.append(" where (");
			for (int i=0;i<tagIds.length;i++){
				if (i < tagIds.length-1){
					f.append(" bean.contentExt.title like '%" + tagIds[i] + "%' or");
				}else{
					f.append(" bean.contentExt.title like '%" + tagIds[i] + "%' ");
				}
			}
			f.append(") ");
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendChannelIds(f, channelIds);
		appendSiteIds(f, siteIds);
		if (excludeId != null) {
			f.append(" and bean.id<>:excludeId");
			f.setParam("excludeId", excludeId);
		}
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}
	
	private void appendSortDate(Finder f){
		try {
			f.append(" and bean.sortDate between '" + DateUtils.beforeDate(new Date()) + "' and '" + DateUtils.convertString(new Date())  + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void appendReleaseDate(Finder f) {
		f.append(" and bean.contentExt.releaseDate<:currentDate");
		f.setParam("currentDate", new Date());
	}

	private void appendTypeIds(Finder f, Integer[] typeIds) {
		int len;
		if (typeIds != null) {
			len = typeIds.length;
			if (len == 1) {
				f.append(" and bean.type.id=:typeId");
				f.setParam("typeId", typeIds[0]);
			} else if (len > 1) {
				f.append(" and bean.type.id in (:typeIds)");
				f.setParamList("typeIds", typeIds);
			}
		}
	}

	private void appendChannelIds(Finder f, Integer[] channelIds) {
		int len;
		if (channelIds != null) {
			len = channelIds.length;
			if (len == 1) {
				f.append(" and bean.channel.id=:channelId");
				f.setParam("channelId", channelIds[0]);
			} else if (len > 1) {
				f.append(" and bean.channel.id in (:channelIds)");
				f.setParamList("channelIds", channelIds);
			}
		}
	}

	private void appendSiteIds(Finder f, Integer[] siteIds) {
		int len;
		if (siteIds != null) {
			len = siteIds.length;
			if (len == 1) {
				f.append(" and bean.site.id=:siteId ");
				f.setParam("siteId", siteIds[0]);
			} else if (len > 1) {
				f.append(" and bean.site.id in (:siteIds)");
				f.setParamList("siteIds", siteIds);
			}
		}
	}
	

	private void appendAttr(Finder f, Map<String,String[]>attr){
		if(attr!=null&&!attr.isEmpty()){
			Set<String>keys=attr.keySet();
			Iterator<String>keyIterator=keys.iterator();
			while(keyIterator.hasNext()){
				String key=keyIterator.next();
				String[] mapValue=attr.get(key);
				String value=mapValue[0],operate=mapValue[1];
				if(operate.equals(PARAM_ATTR_EQ)){
					f.append(" and bean.attr[:k"+key+"]=:v"+key).setParam("k"+key, key).setParam("v"+key, value);
				}else if(operate.equals(PARAM_ATTR_START)){
					f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, value+"%");
				}else if(operate.equals(PARAM_ATTR_END)){
					f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, "%"+value);
				}else if(operate.equals(PARAM_ATTR_LIKE)){
					f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, "%"+value+"%");
				}else {
					//取绝对值比较大小
					Float floatValue=Float.valueOf(value);
					if(operate.equals(PARAM_ATTR_GT)){
						if(floatValue>=0){
							f.append(" and (bean.attr[:k"+key+"]>=0 and abs(bean.attr[:k"+key+"])>:v"+key+")").setParam("k"+key, key).setParam("v"+key, floatValue);
						}else{
							f.append(" and ((bean.attr[:k"+key+"]<0 and abs(bean.attr[:k"+key+"])<:v"+key+") or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
						}
				 	}else if(operate.equals(PARAM_ATTR_GTE)){
				 		if(floatValue>=0){
							f.append(" and (abs(bean.attr[:k"+key+"])>=:v"+key+" and bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
						}else{
							f.append(" and ((abs(bean.attr[:k"+key+"])<=:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
						}
					}else if(operate.equals(PARAM_ATTR_LT)){
						if(floatValue>=0){
							f.append(" and ((abs(bean.attr[:k"+key+"])<:v"+key+" and bean.attr[:k"+key+"]>=0) or bean.attr[:k"+key+"]<=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
						}else{
							f.append(" and ((abs(bean.attr[:k"+key+"])>:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
						}
					}else if(operate.equals(PARAM_ATTR_LTE)){
						if(floatValue>=0){
							f.append(" and ((abs(bean.attr[:k"+key+"])<=:v"+key+" and bean.attr[:k"+key+"]>=0) or bean.attr[:k"+key+"]<=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
						}else{
							f.append(" and ((abs(bean.attr[:k"+key+"])>=:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
						}
					}
				}
			}
		}
	}
	
	
	private void appendOrder(Finder f, int orderBy) {
		switch (orderBy) {
		case 1:
			// ID升序
			f.append(" order by bean.id asc");
			break;
		case 2:
			// 发布时间降序
			f.append(" order by bean.sortDate desc");
			break;
		case 3:
			// 发布时间升序
			f.append(" order by bean.sortDate asc");
			break;
		case 4:
			// 固顶级别降序、发布时间降序
			f.append(" order by bean.topLevel desc, bean.sortDate desc");
			break;
		case 5:
			// 固顶级别降序、发布时间升序
			f.append(" order by bean.topLevel desc, bean.sortDate asc");
			break;
		case 6:
			// 日访问降序
			f.append(" order by bean.contentCount.viewsDay desc, bean.id desc");
			break;
		case 7:
			// 周访问降序
			f.append(" order by bean.contentCount.viewsWeek desc");
			f.append(", bean.id desc");
			break;
		case 8:
			// 月访问降序
			f.append(" order by bean.contentCount.viewsMonth desc");
			f.append(", bean.id desc");
			break;
		case 9:
			// 总访问降序
			f.append(" order by bean.contentCount.views desc");
			f.append(", bean.id desc");
			break;
		case 10:
			// 日评论降序
			f.append(" order by bean.commentsDay desc, bean.id desc");
			break;
		case 11:
			// 周评论降序
			f.append(" order by bean.contentCount.commentsWeek desc");
			f.append(", bean.id desc");
			break;
		case 12:
			// 月评论降序
			f.append(" order by bean.contentCount.commentsMonth desc");
			f.append(", bean.id desc");
			break;
		case 13:
			// 总评论降序
			f.append(" order by bean.contentCount.comments desc");
			f.append(", bean.id desc");
			break;
		case 14:
			// 日下载降序
			f.append(" order by bean.downloadsDay desc, bean.id desc");
			break;
		case 15:
			// 周下载降序
			f.append(" order by bean.contentCount.downloadsWeek desc");
			f.append(", bean.id desc");
			break;
		case 16:
			// 月下载降序
			f.append(" order by bean.contentCount.downloadsMonth desc");
			f.append(", bean.id desc");
			break;
		case 17:
			// 总下载降序
			f.append(" order by bean.contentCount.downloads desc");
			f.append(", bean.id desc");
			break;
		case 18:
			// 日顶降序
			f.append(" order by bean.upsDay desc, bean.id desc");
			break;
		case 19:
			// 周顶降序
			f.append(" order by bean.contentCount.upsWeek desc");
			f.append(", bean.id desc");
			break;
		case 20:
			// 月顶降序
			f.append(" order by bean.contentCount.upsMonth desc");
			f.append(", bean.id desc");
			break;
		case 21:
			// 总顶降序
			f.append(" order by bean.contentCount.ups desc, bean.id desc");
			break;
		case 22:
			//首页大图升序
			f.append(" order by bean.classify");
			break;
		case 23:
			//页面咨询列表置顶降序，发布时间降序
			//f.append(" order by bean.isPictureTop=2 desc,bean.sortDate desc");
			f.append(" order by bean.sortDate desc");
			break;
		case 24:
			// 活动时间降序
			f.append(" order by bean.sportDate desc");
			break;
		default:
			// 默认： ID降序
			f.append(" order by bean.id desc");
		}
	}

	public void appendIsPictureTop(Finder f){
		f.append(",bean.isPictureTop desc");
	}
	
	public int countByChannelId(int channelId) {
		String hql = "select count(*) from Content bean"
				+ " join bean.channel channel,Channel parent"
				+ " where channel.lft between parent.lft and parent.rgt"
				+ " and channel.site.id=parent.site.id"
				+ " and parent.id=:parentId";
		Query query = getSession().createQuery(hql);
		query.setParameter("parentId", channelId);
		return ((Number) (query.iterate().next())).intValue();
	}

	public Content findById(Integer id) {
		Content entity = get(id);
		return entity;
	}

	public Content save(Content bean) {
		getSession().save(bean);
		return bean;
	}

	public Content deleteById(Integer id) {
		Content entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	/**不知为何要重写，此处先注释
	protected Pagination find(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		System.out.println("totalCount="+totalCount);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	protected int countQueryResult(Finder finder) {
		Query query = getSession().createQuery(finder.getRowCountHql());
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return ((Number) query.iterate().next()).intValue();
	} 
	*/

	public void updateBigPicture(Integer channel,Integer contentId, Integer isBigPicture,
			String article,String classify) {
		Finder ft = Finder.create("update ContentExt ext set ext.bigPictureImg=null where ext.id in (select bean.id from Content bean ");
		ft.append("where bean.channel=" + channel + " and bean.isBigPicture='" + isBigPicture + "' and bean.classify='" + classify + "' and bean.id <> "+contentId+")");
		Query queryt = getSession().createQuery(ft.getOrigHql());
		queryt.executeUpdate();
		Finder f = Finder.create("update Content set ");
		f.append("isBigPicture=0,");
		f.append("article='',");
		f.append("classify='' ");
		f.append("where channel=" + channel + " and isBigPicture='" + isBigPicture + "' and classify='" + classify + "' and id <> "+contentId);
		Query query = getSession().createQuery(f.getOrigHql());
		query.executeUpdate();
	}
	
	public void updateBigPictureIndexRank(Integer channel,Integer contentId, Integer isBigPicture,
			String article,String classify) {
		Finder ft = Finder.create("update ContentExt ext set ext.bigPictureImg=null where ext.id in (select bean.id from Content bean ");
		ft.append("where bean.channel in (70,85) and bean.isBigPicture='" + isBigPicture + "' and bean.classify='" + classify + "' and bean.id <> "+contentId+")");
		Query queryt = getSession().createQuery(ft.getOrigHql());
		queryt.executeUpdate();
		Finder f = Finder.create("update Content set ");
		f.append("isBigPicture=0,");
		f.append("article='',");
		f.append("classify='' ");
		f.append("where channel in (70,85) and isBigPicture='" + isBigPicture + "' and classify='" + classify + "' and id <> "+contentId);
		Query query = getSession().createQuery(f.getOrigHql());
		query.executeUpdate();
	}
	
	public void updatePictureTop(Integer pictureTop,Integer id) {
		Finder ft;
		if (id != null){
			ft = Finder.create("update Content set isPictureTop=0 where isPictureTop=2 and id<>"+id);
		}else{
			ft = Finder.create("update Content set isPictureTop=0 where isPictureTop=2");
		}
		Query query = getSession().createQuery(ft.getOrigHql());
		query.executeUpdate();
	}


	public void updateSpecialRecommandView(Integer val) {
		Finder ft = Finder.create("update Content set recommandView=0 where recommand_view=1");
		Query query = getSession().createQuery(ft.getOrigHql());
		query.executeUpdate();
	}


	public List<Content> getListForAbout(String extTitle) {
		Finder f = Finder.create("select bean from Content bean join bean.contentExt ext where ext.title='"+extTitle+"'");
		System.out.println("---  " + f.getOrigHql() + "   ---");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}


	public List<Content> getZhiShuPaiHangDaTu() {
		Finder f = Finder.create("select bean from Content bean where 1=1 ");
		f.append(" and bean.isBigPicture=2 order by bean.classify");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	public List<Content> getListForGunDong() {
		Finder f = Finder.create();
		f.append("select  bean from Content bean ");
		f.append(" join bean.contentExt as ext");
		f.append(" where bean.channel.id in (70,84) order by bean.sortDate limit 50");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	public List<Content> getListForCurrentDay() {
		Finder f = Finder.create();
		f.append("select bean from Content bean ");
		f.append(" join bean.contentExt as ext ");
		f.append(" where bean.status=2 and date(bean.sortDate) = curdate()");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 获取未推送给百度的文章
	 * @return
	 */
	public List<Content> getNoPushBaiduList() {
		Finder f= Finder.create("select bean from Content bean ");
		f.append(" join bean.contentExt as ext ");
		f.append(" where bean.status=2 and date(bean.sortDate) = curdate() and bean.isPushBaidu=0");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 更新已经推送给百度文章状态
	 */
	public void updatePushBaiduArticle(String ids) {
		Finder f = Finder.create("update Content bean set bean.isPushBaidu=1 where bean.id in (" + ids + ")");
		Query query = getSession().createQuery(f.getOrigHql());
		query.executeUpdate();
	}

	/**
	 * 加载更多文章
	 */
	public List<Content> getMoreArticles(int maxId, int minId,String category) {
		Finder f = Finder.create();
		f.append("select  bean from Content bean ");
		f.append(" join bean.contentExt as ext where ");
		if ("article".equals(category)){
			f.append("bean.channel.id in (70,84) ");
		}else if ("kuaibo".equals(category)){
			f.append("bean.channel.id in (71) ");
		}else if ("remenzhiye".equals(category)){
			f.append("bean.channel.id in (79) ");
		}
		f.append(" and bean.id<"+maxId + " and bean.id>"+minId+" and status=2 ");
		f.append(" order by bean.sortDate desc");
		f.setMaxResults(100);
		return find(f);
	}

	/**
	 * 移动端
	 * 加载更新文章(上拉和下拉刷新)
	 * Param maxLengthId 页面最大文章ID
	 * Param articleLength 获取文章条数
	 * Param toward 刷新方向(true下拉,false上拉)
	 */
	public List<Content> getMoreArticles(int maxLengthId, int articleLength,
			boolean toward) {
		Finder f = Finder.create();
		if (toward){
			f.append("select  bean from Content bean ");
			f.append(" join bean.contentExt as ext");
			f.append(" where bean.channel.id in (70,84) ");
			f.append(" and bean.id>"+maxLengthId+" and status=2 ");
			f.append(" order by bean.sortDate desc");
			f.setMaxResults(articleLength);
		}else{
			f.append("select  bean from Content bean ");
			f.append(" join bean.contentExt as ext");
			f.append(" where bean.channel.id in (70,84) ");
			f.append(" and bean.id<"+maxLengthId+" and status=2 ");
			f.append(" order by bean.sortDate desc");
			f.setMaxResults(articleLength);
		}
		return find(f);
	}

	/**
	 * 查询头条新闻
	 */
	public Content findTouTiao() {
		Finder f = Finder.create("from Content bean where bean.isPictureTop=2");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		if (contents != null && contents.size() > 0){
			return contents.get(0);
		}else{
			return null;
		}
	}


	/**
	 * 查询从昨天15点到今天15点最新10篇文章(用作更新网站右侧大家都在搜)
	 */
	public List<Content> findNewTenXinwen(String beforeDate, String afterDate) {
		Finder f = Finder.create("from Content bean where bean.channel.id=70 and bean.status=2 and bean.sortDate between '"+beforeDate+"' and '"+afterDate+"' order by bean.id desc");
		f.setMaxResults(10);
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 查询人物专访文章
	 */
	public List<Content> findRenWuZhuanFangArticles() {
		Finder f = Finder.create("from Content bean where bean.channel.id in (75,84) and bean.status=2 ");
		//f.append(" order by bean.recommendInterview desc,bean.sortDate desc");			//人物专访最上面是推荐在前面，然后才是按时间倒序
		f.append(" order by bean.sortDate desc");											//现在暂时用时间倒序
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 查询学堂文章
	 */
	public List<Content> findXueTangArticles() {
		Finder f = Finder.create(" from Content bean where bean.channel.id=76 and bean.status=2 order by bean.sortDate desc");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 根据大数据学堂类型查询
	 */
	public List<Content> findXueTangByTypeArticls(String type) {
		Finder f = Finder.create("from Content bean where bean.channel.id=76 and bean.status=2 ");
		if ("peixun".equals(type)){
			f.append(" and bean.classify='培训' ");
		}else if ("shudan".equals(type)){
			f.append(" and bean.classify='书单' ");
		}else if ("baike".equals(type)){
			f.append(" and bean.classify='百科' ");
		}
		f.append(" order by bean.sortDate desc");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 普通活动列表
	 */
	public List<Content> findPuTongSports() {
		Finder f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport=0 order by bean.sortDate desc");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 普通活动列表(手机)
	 */
	public List<Content> findPuTongSports(int maxId, int articleLength,String type) {
		Finder f = null;
		if ("up".equals(type)){
			f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport=0 and bean.id<"+maxId+" order by bean.sortDate desc");
		}else if ("down".equals(type)){
			f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport=0 and bean.id>"+maxId+" order by bean.sortDate desc");
		}
		
		f.setMaxResults(articleLength);
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 普通精品活动列表(手机)
	 */
	public List<Content> findPuTongJPSports(int maxId, int articleLength,String type,String jpType) {
		Finder f = null;
		if ("up".equals(type)){
			f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport=0 and bean.mySport="+jpType+" and bean.id<"+maxId+" order by bean.sortDate desc");
		}else if ("down".equals(type)){
			f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport=0 and bean.mySport="+jpType+" and bean.id>"+maxId+" order by bean.sortDate desc");
		}
		
		f.setMaxResults(articleLength);
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 我的活动列表
	 */
	public List<Content> findMySport(Integer num) {
		Finder f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.mySport>0 order by bean.sortDate desc");
		if (num != null){
			f.setMaxResults(num);
		}
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType, String startTime,
			String endTime) {
		Finder f = Finder.create("from Content bean where bean.status=2 and bean.mySport="+jpType+" and bean.sportDate between '"+startTime+"' and '"+endTime+"' order by bean.sportDate desc");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType) {
		Finder f = Finder.create("from Content bean where bean.status=2 and bean.mySport="+jpType+" order by bean.sportDate desc");
		Query query = getSession().createQuery(f.getOrigHql());
		query.setMaxResults(1);
		List<Content> contents = query.list();
		return contents;
	}


	/**
	 * 精品活动分类列表总览
	 */
	public List<Content> findMySportByCategoryDetail(String jpType,
			String startId, String stepLength) {
		Finder f = Finder.create("from Content bean where bean.status=2 and bean.mySport="+jpType);
		if (startId != null){
			f.append(" and bean.id<" + startId);
		}
		f.append(" order by bean.sportDate desc");
		if (stepLength != null){
			int ocuntMax = Integer.parseInt(stepLength);
			f.setMaxResults(ocuntMax);
		}
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}


	/**
	 * 查询当前活动列表
	 */
	public List<Content> findCurrentMonthSports(String startTime, String endTime) {
		Finder f = Finder.create("from Content bean where bean.channel.id=74 and bean.status=2 and bean.sportDate between '"+startTime+"' and '"+endTime+"'");
		Query query = getSession().createQuery(f.getOrigHql());
		List<Content> contents = query.list();
		return contents;
	}

	/**
	 * 查看最大文章数
	 */
	public int findMaxArticleNum() {
		Finder f = Finder.create("select max(bean.id) from Content bean");
		Query query = getSession().createQuery(f.getOrigHql());
		Integer maxId = (Integer) query.uniqueResult();
		return maxId;
	}

	/**
	 * 更新文章中更多分类和ID
	 */
	public void updateMoreCategoryAndId(String oid, String releaseMoreCategory,
			String releaseMoreId) {
		Finder f = f = Finder.create("update Content bean set bean.releaseMoreCategory='"+releaseMoreCategory+"',bean.releaseMoreId='"+releaseMoreId+"' where bean.id="+oid);
		Query query = getSession().createQuery(f.getOrigHql());
		query.executeUpdate();
	}


	@Override
	protected Class<Content> getEntityClass() {
		return Content.class;
	}
}