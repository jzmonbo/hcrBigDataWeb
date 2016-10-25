package com.hcrcms.cms.dao.assist.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.dao.assist.CmsCommentExtDao;
import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;

@Repository
public class CmsCommentExtDaoImpl extends
		HibernateBaseDao<CmsCommentExt, Integer> implements CmsCommentExtDao {
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public CmsCommentExt findById(Integer id) {
		CmsCommentExt entity = get(id);
		return entity;
	}

	public CmsCommentExt save(CmsCommentExt bean) {
		getSession().save(bean);
		return bean;
	}

	public int deleteByContentId(Integer contentId) {
		String hql = "delete from CmsCommentExt bean where bean.id in"
				+ " (select c.id from CmsComment c where c.content.id=:contentId)";
		return getSession().createQuery(hql).setParameter("contentId",
				contentId).executeUpdate();
	}

	public CmsCommentExt deleteById(Integer id) {
		CmsCommentExt entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public void updateReplay(CmsComment bean, CmsCommentExt ext, String reply,String commentId) {
		/*Finder f = Finder.create("update CmsCommentExt ext set ext.reply=:reply where ext.id=:commentId");
		f.setParam("reply",reply);
		f.setParam("commentId", Integer.parseInt(commentId));*/
		ext.setId(Integer.parseInt(commentId));
		ext.setReply(reply);
		String str = "update CmsCommentExt ext set ext.reply='"+reply+"' where ext.id="+commentId;
		Query query = getSession().createQuery(str);
		query.executeUpdate();
	}
	
	public List<CmsCommentExt> getReplyByCommentId(String commentId) {
		Finder fd = Finder.create("from CmsCommentExt ext where 1=1 and ext.comment.id="+commentId);
		return find(fd);
	}

	public void updateExts(List<CmsCommentExt> exts) {
		/*Transaction tx = getSession().beginTransaction();
		try {
			tx.begin();
			for (int i = 0; i < exts.size(); i++) {
				if (i > 9) {
					getSession().flush();
					getSession().clear();
				}
				getSession().update(exts.get(i));
			}
			getSession().flush();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}finally{
			
		}*/
		
		for (int i = 0; i < exts.size(); i++) {
			getSession().update(exts.get(i));
		}
	}

	public void deleteComment(int commentId, int local) {
		String str = "delete from CmsCommentExt ext where ext.comment.id=" + commentId + " and ext.sort=" + local;
		Query query = getSession().createQuery(str);
		query.executeUpdate();
	}

	public void deleteCommentExt(int commentId, int sort, int rsort) {
		String str = "delete from CmsCommentExt ext where ext.comment.id=" + commentId + " and ext.sort=" + sort + " and ext.rsort=" + rsort;
		Query query = getSession().createQuery(str);
		query.executeUpdate();
	}

	@Override
	protected Class<CmsCommentExt> getEntityClass() {
		return CmsCommentExt.class;
	}
}