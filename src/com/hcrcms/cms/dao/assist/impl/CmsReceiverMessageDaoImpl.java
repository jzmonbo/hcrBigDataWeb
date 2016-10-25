package com.hcrcms.cms.dao.assist.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.hcrcms.cms.dao.assist.CmsReceiverMessageDao;
import com.hcrcms.cms.entity.assist.CmsReceiverMessage;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateBaseDao;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;


public class CmsReceiverMessageDaoImpl extends
		HibernateBaseDao<CmsReceiverMessage, Integer> implements
		CmsReceiverMessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
			int pageNo, int pageSize) {
		String hql = " select msg from CmsReceiverMessage msg where 1=1 ";
		Finder finder = Finder.create(hql);
		if (siteId != null) {
			finder.append(" and msg.site.id=:siteId")
					.setParam("siteId", siteId);
		}
		// 垃圾箱
		if (sendUserId != null && receiverUserId != null) {
			finder
					.append(
							" and ((msg.msgReceiverUser.id=:receiverUserId  and msg.msgBox =:box) or (msg.msgSendUser.id=:sendUserId  and msg.msgBox =:box) )")
					.setParam("sendUserId", sendUserId).setParam(
							"receiverUserId", receiverUserId).setParam("box",
							box);
		} else {
			if (sendUserId != null) {
				finder.append(" and msg.msgSendUser.id=:sendUserId").setParam(
						"sendUserId", sendUserId);
			}
			if (receiverUserId != null) {
				finder.append(" and msg.msgReceiverUser.id=:receiverUserId")
						.setParam("receiverUserId", receiverUserId);
			}
			if (box != null) {
				finder.append(" and msg.msgBox =:box").setParam("box", box);
			}
		}
		if (StringUtils.isNotBlank(title)) {
			finder.append(" and msg.msgTitle like:title").setParam("title",
					"%" + title + "%");
		}
		if (sendBeginTime != null) {
			finder.append(" and msg.sendTime >=:sendBeginTime").setParam(
					"sendBeginTime", sendBeginTime);
		}
		if (sendEndTime != null) {
			finder.append(" and msg.sendTime <=:sendEndTime").setParam(
					"sendEndTime", sendEndTime);
		}
		if (status != null) {
			if (status) {
				finder.append(" and msg.msgStatus =true");
			} else {
				finder.append(" and msg.msgStatus =false");
			}
		}
		finder.append(" order by msg.id desc");

		return find(finder, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<CmsReceiverMessage> getList(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable) {
		String hql = " select msg from CmsReceiverMessage msg where 1=1  ";
		Finder finder = Finder.create(hql);
		if (siteId != null) {
			finder.append(" and msg.site.id=:siteId")
					.setParam("siteId", siteId);
		}
		// 垃圾箱
		if (sendUserId != null && receiverUserId != null) {
			finder
					.append(
							" and ((msg.msgReceiverUser.id=:receiverUserId  and msg.msgBox =:box) or (msg.msgSendUser.id=:sendUserId  and msg.msgBox =:box) )")
					.setParam("sendUserId", sendUserId).setParam(
							"receiverUserId", receiverUserId).setParam("box",
							box);
		}  else {
			if (sendUserId != null) {
				finder.append(" and msg.msgSendUser.id=:sendUserId").setParam(
						"sendUserId", sendUserId);
			}
			if (receiverUserId != null) {
				finder.append(" and msg.msgReceiverUser.id=:receiverUserId")
						.setParam("receiverUserId", receiverUserId);
			}
			if (box != null) {
				finder.append(" and msg.msgBox =:box").setParam("box", box);
			}
		}
		if (StringUtils.isNotBlank(title)) {
			finder.append(" and msg.msgTitle like:title").setParam("title",
					"%" + title + "%");
		}
		if (sendBeginTime != null) {
			finder.append(" and msg.sendTime >=:sendBeginTime").setParam(
					"sendBeginTime", sendBeginTime);
		}
		if (sendEndTime != null) {
			finder.append(" and msg.sendTime <=:sendEndTime").setParam(
					"sendEndTime", sendEndTime);
		}
		if (status != null) {
			if (status) {
				finder.append(" and msg.msgStatus =true");
			} else {
				finder.append(" and msg.msgStatus =false");
			}
		}
		finder.append(" order by msg.id desc");
		return find(finder);
	}

	public CmsReceiverMessage findById(Integer id) {
		return super.get(id);
	}

	public CmsReceiverMessage save(CmsReceiverMessage bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsReceiverMessage update(CmsReceiverMessage bean) {
		getSession().update(bean);
		return bean;
	}

	public CmsReceiverMessage deleteById(Integer id) {
		CmsReceiverMessage entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public CmsReceiverMessage[] deleteByIds(Integer[] ids) {
		CmsReceiverMessage[] messages = new CmsReceiverMessage[ids.length];
		for (int i = 0; i < ids.length; i++) {
			messages[i] = get(ids[i]);
			deleteById(ids[i]);
		}
		return messages;
	}

	public List<CmsReceiverMessage> getMyReceiverMessages(CmsSite site,CmsUser user) {
		List<CmsReceiverMessage> receiverMessages = null;
		String hql = " select msg from CmsReceiverMessage msg where 1=1 ";
		Finder finder = Finder.create(hql);
		if (site != null) {
			finder.append(" and msg.site.id=:siteId")
					.setParam("siteId", site.getId());
		}
		if (user != null) {
			finder.append(" and msg.msgReceiverUser.id=:receiverUserId")
					.setParam("receiverUserId", user.getId());
		}
		finder.append(" order by msg.msgStatus desc,msg.sendTime desc");
		receiverMessages = find(finder);
		return receiverMessages;
	}

	public void alreadyReadMessages(Integer rmid) {
		Finder finder = Finder.create("update CmsReceiverMessage msg set msg.msgStatus=1 where msg.id=" + rmid);
		Query query = getSession().createQuery(finder.getOrigHql());
		query.executeUpdate();
	}

	@Override
	protected Class<CmsReceiverMessage> getEntityClass() {
		return CmsReceiverMessage.class;
	}

}
