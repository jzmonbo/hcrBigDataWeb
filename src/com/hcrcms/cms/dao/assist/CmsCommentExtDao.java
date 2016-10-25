package com.hcrcms.cms.dao.assist;

import java.util.List;

import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.page.Pagination;

public interface CmsCommentExtDao {
	public Pagination getPage(int pageNo, int pageSize);

	public CmsCommentExt findById(Integer id);

	public CmsCommentExt save(CmsCommentExt bean);

	public CmsCommentExt updateByUpdater(Updater<CmsCommentExt> updater);

	public int deleteByContentId(Integer contentId);

	public CmsCommentExt deleteById(Integer id);
	
	public void updateReplay(CmsComment bean, CmsCommentExt ext,String reply,String commentId);
	
	public List<CmsCommentExt> getReplyByCommentId(String commentId);
	
	public void updateExts(List<CmsCommentExt> exts);
	
	public void deleteComment(int commentId, int local);
	
	public void deleteCommentExt(int commentId, int sort, int rsort);
}