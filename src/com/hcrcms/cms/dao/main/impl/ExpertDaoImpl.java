package com.hcrcms.cms.dao.main.impl;

import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_END;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_EQ;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GT;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GTE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LIKE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LT;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LTE;
import static com.hcrcms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_START;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.main.ExpertDao;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.cms.entity.main.Expert;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.web.util.DateUtils;

@Repository
public class ExpertDaoImpl extends HibernateBaseDao<Expert, Integer>
		implements ExpertDao {
	@SuppressWarnings("unchecked")
	public List<Expert> getList() {
		Finder f = Finder.create("from Expert bean where 1=1 ");
		f.append(" order by bean.expertId");
		return find(f);
	}

	public Expert findById(Integer id) {
		Expert entity = get(id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public Expert findByPath(String path){
		String hql = "from Expert bean where bean.path=:path";
		List<Expert> list = getSession().createQuery(hql).setParameter("path", path).setMaxResults(1).list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Expert save(Expert bean) {
		getSession().save(bean);
		return bean;
	}

	public Expert deleteById(Integer id) {
		Expert entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public List<Expert> findByName(String name) {
		Finder f = Finder.create("from Expert bean where 1=1 ");
		f.append(" and author='"+name+"' order by bean.expertId");
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, Integer first, Integer count,String expertId,String author) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, attr,orderBy, option,expertId,author);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}
	
	private Finder byChannelIds(Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			int option,String expertId,String author) {
		Finder f = Finder.create("select  bean from Expert bean where 1=1");
		if (expertId != null && !"".equals(expertId)){
			f.append(" and bean.expertId=:expertId");
			f.setParam("expertId", expertId);
		}
		if (author != null && !"".equals(author)){
			f.append(" and bean.author like '%:author%'");
			f.setParam("author",author);
			
		}
		//appendSortDate(f);
		f.append(" order by bean.sort");
		/*if (titleImg != null) {
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
		appendOrder(f, orderBy);*/
		return f;
	}
	
	private void appendSortDate(Finder f){
		try {
			f.append(" and bean.releaseDate between '" + DateUtils.beforeDate(new Date()) + "' and '" + DateUtils.convertString(new Date())  + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		default:
			// 默认： ID降序
			f.append(" order by bean.id desc");
		}
	}
	
	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr, int orderBy, int option,int pageNo, int pageSize,String expertId,String author) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, attr,orderBy, option,expertId,author);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}
	
	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize,String expertId,String author) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,
				attr,orderBy,expertId,author);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}
	
	private Finder bySiteIds(Integer[] siteIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,String expertId,String author) {
		Finder f = Finder.create("select  bean from Expert bean where 1=1");
		if (expertId != null && !"".equals(expertId)){
			f.append(" and bean.expertId=:expertId");
			f.setParam("expertId", expertId);
		}
		if (author != null && !"".equals(author)){
			f.append(" and bean.author like '%:author%'");
			f.setParam("author",author);
			
		}
		//appendSortDate(f);
		f.append(" order by bean.sort");
		/*if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendSiteIds(f, siteIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);*/
		return f;
	}
	
	private void appendReleaseDate(Finder f) {
		f.append(" and ext.releaseDate<:currentDate");
		f.setParam("currentDate", new Date());
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
	
	@SuppressWarnings("unchecked")
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count,String expertId,String author) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,attr,
				orderBy,expertId,author);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}
	
	public List<Content> getListByName(String username) {
		Finder f = Finder.create();
		f.append("select  bean from Content bean ");
		f.append(" join bean.contentExt as ext");
		f.append(" where ext.author=:author");
		f.append(" and bean.channel.id not in (71,78,79)");
		f.append(" order by bean.sortDate desc");
		f.setParam("author",username);
		return find(f);
	}

	public List<Content> getListByTime(String startTime, String endTime) {
		Finder f = Finder.create();
		try {
			f.append("select  bean from Content bean ");
			f.append(" join bean.contentExt as ext");
			f.append(" where ext.releaseDate between :startTime and :endTime");
			f.append(" and bean.channel.id not in (71,78,79)");
			f.append(" order by bean.sortDate desc");
			f.setParam("startTime",DateUtils.convertDate(startTime));
			f.setParam("endTime",DateUtils.convertDate(endTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return find(f);
	}

	/**
	 * 按所有专家文章时间倒序查询
	 * @return
	 */
	public List<Content> getListByDate() {
		Finder f = Finder.create("select  bean from Content bean where bean.isExpert=1 and bean.status=2 order by bean.sortDate desc");
		return find(f);
	}

	@Override
	protected Class<Expert> getEntityClass() {
		return Expert.class;
	}
}