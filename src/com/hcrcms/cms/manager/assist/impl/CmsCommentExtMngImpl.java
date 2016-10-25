package com.hcrcms.cms.manager.assist.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.assist.CmsCommentExtDao;
import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.cms.manager.assist.CmsCommentExtMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class CmsCommentExtMngImpl implements CmsCommentExtMng {
	public CmsCommentExt save(String ip, String text, CmsComment comment) {
		CmsCommentExt ext = new CmsCommentExt();
		ext.setText(text);
		ext.setIp(ip);
		ext.setComment(comment);
		//comment.setCommentExt(ext);
		dao.save(ext);
		return ext;
	}

	public void save(CmsCommentExt ext) {
		dao.save(ext);
	}

	public void saveMany(List<CmsCommentExt> exts) {
		if (exts != null && exts.size() > 0){
			for (CmsCommentExt ext : exts){
				save(ext);
			}
		}
	}

	public CmsCommentExt saveAnonymous(String ip, String text, CmsComment comment,String anonymousName,String anonymousImg) {
		CmsCommentExt ext = new CmsCommentExt();
		ext.setText(text);
		ext.setIp(ip);
		ext.setAnonymousName(anonymousName);
		ext.setAnonymousImg(anonymousImg);
		ext.setComment(comment);
		//comment.setCommentExt(ext);
		dao.save(ext);
		return ext;
	}
	
	public CmsCommentExt update(CmsCommentExt bean) {
		Updater<CmsCommentExt> updater = new Updater<CmsCommentExt>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	public void deleteComment(int commentId, int local) {
		dao.deleteComment(commentId,local);
	}

	public void deleteCommentExt(int commentId, int sort, int rsort) {
		dao.deleteCommentExt(commentId,sort,rsort);
	}

	public void update(List<CmsCommentExt> exts) {
		dao.updateExts(exts);
	}

	public int deleteByContentId(Integer contentId) {
		return dao.deleteByContentId(contentId);
	}

	public void updateReplay(CmsComment bean, CmsCommentExt ext, String reply,String commentId) {
		dao.updateReplay(bean,ext,reply,commentId);
	}
	
	public List<CmsCommentExt> getReplyByCommentId(String commentId) {
		return dao.getReplyByCommentId(commentId);
	}

	private CmsCommentExtDao dao;

	@Autowired
	public void setDao(CmsCommentExtDao dao) {
		this.dao = dao;
	}
}