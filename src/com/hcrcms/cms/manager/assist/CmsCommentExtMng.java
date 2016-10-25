package com.hcrcms.cms.manager.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;

public interface CmsCommentExtMng {
	public CmsCommentExt save(String ip, String text, CmsComment comment);
	
	public void save(CmsCommentExt ext);
	
	public void saveMany(List<CmsCommentExt> exts);
	
	public CmsCommentExt saveAnonymous(String ip, String text, CmsComment comment,String anonymousName,String anonymousImg);

	public CmsCommentExt update(CmsCommentExt bean);
	
	public void deleteComment(int commentId,int local);
	
	public void deleteCommentExt(int commentId,int sort,int rsort);
	
	public void update(List<CmsCommentExt> exts);

	public int deleteByContentId(Integer contentId);
	
	public void updateReplay(CmsComment bean, CmsCommentExt ext,String reply,String commentId);
	
	public List<CmsCommentExt> getReplyByCommentId(String commentId);
}