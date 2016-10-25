package com.hcrcms.cms.manager.main;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Content.ContentStatus;
import com.hcrcms.cms.entity.main.ContentExt;
import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.cms.staticpage.exception.ContentNotCheckedException;
import com.hcrcms.cms.staticpage.exception.GeneratedZeroStaticPageException;
import com.hcrcms.cms.staticpage.exception.StaticPageNotOpenException;
import com.hcrcms.cms.staticpage.exception.TemplateNotFoundException;
import com.hcrcms.cms.staticpage.exception.TemplateParseException;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.core.entity.CmsUser;

public interface ContentMng {
	public Pagination getPageByRight(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId,Integer userId, int orderBy, int pageNo,
			int pageSize);
	
	
	public Pagination getPageBySite(String title, Integer typeId,Integer inputUserId,boolean topLevel,
			boolean recommend,ContentStatus status, Integer siteId,int orderBy, int pageNo,int pageSize);

	/**
	 * 获得文章分页。供会员中心使用。
	 * 
	 * @param title
	 *            文章标题
	 * @param channelId
	 *            栏目ID
	 * @param siteId
	 *            站点ID
	 * @param memberId
	 *            会员ID
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页大小
	 * @return 文章分页对象
	 */
	public Pagination getPageForMember(String title, Integer channelId,
			Integer siteId, Integer modelId,Integer memberId, int pageNo, int pageSize);
	

	/**
	 * 根据内容ID数组获取文章列表
	 * 
	 * @param ids
	 * @param orderBy
	 * @return
	 */
	public List<Content> getListByIdsForTag(Integer[] ids, int orderBy);

	public Content getSide(Integer id, Integer siteId, Integer channelId,
			boolean next);

	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr,int orderBy, int pageNo, int pageSize);

	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count);

	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr, int orderBy, int option,int pageNo, int pageSize,String isPhone);

	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, Integer first, Integer count,String isPhone);

	public Pagination getPageByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title, Map<String,String[]>attr,int orderBy, int pageNo,
			int pageSize);

	public List<Content> getListByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,Map<String,String[]>attr, int orderBy, Integer first,
			Integer count);

	public Pagination getPageByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title,Map<String,String[]>attr, int orderBy,
			int pageNo, int pageSize);

	public List<Content> getListByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title,Map<String,String[]>attr, int orderBy,
			Integer first, Integer count);

	public Pagination getPageByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize);

	public List<Content> getListByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count);

	public List<Content> getListByTagIdsForTag(String[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, Integer first, Integer count);
	
	public Content findById(Integer id);

	public Content save(Content bean, ContentExt ext, ContentTxt txt,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] tagArr, String[] attachmentPaths,
			String[] attachmentNames, String[] attachmentFilenames,
			String[] picPaths, String[] picDescs, Integer channelId,
			Integer typeId, Boolean draft,Boolean contribute, CmsUser user, boolean forMember,Integer meetingId,Integer siteLocation,Integer topicType);

	public Content save(Content bean, ContentExt ext, ContentTxt txt,
			Integer channelId,Integer typeId, Boolean draft, CmsUser user, boolean forMember);

	public Content update(Content bean, ContentExt ext, ContentTxt txt,
			String[] tagArr, Integer[] channelIds, Integer[] topicIds,
			Integer[] viewGroupIds, String[] attachmentPaths,
			String[] attachmentNames, String[] attachmentFilenames,
			String[] picPaths, String[] picDescs, Map<String, String> attr,
			Integer channelId, Integer typeId, Boolean draft, CmsUser user,
			boolean forMember,String queryStatus,Integer queryTypeId,CmsSpecialTopicContent topicContent,Integer meetingId,Integer siteLocation,
			Integer specialTopicContentId,String subPlaceStr,Boolean queryTopLevel,Boolean queryRecommend,Integer queryOrderBy);
	
	public Content update(Content bean);
	
	public Content updateByChannelIds(Integer contentId,Integer[]channelIds);
	
	public Content addContentToTopics(Integer contentId,Integer[]topicIds);

	public Content check(Integer id, CmsUser user);

	public Content[] check(Integer[] ids, CmsUser user);

	public Content reject(Integer id, CmsUser user,Byte step, String opinion);

	public Content[] reject(Integer[] ids, CmsUser user,Byte step,String opinion);

	public Content cycle(Integer id);

	public Content[] cycle(Integer[] ids);

	public Content recycle(Integer id);

	public Content[] recycle(Integer[] ids);

	public Content deleteById(Integer id);

	public Content[] deleteByIds(Integer[] ids);

	public Content[] contentStatic(Integer[] ids)
			throws TemplateNotFoundException, TemplateParseException,
			GeneratedZeroStaticPageException, StaticPageNotOpenException,
			ContentNotCheckedException;
	
	public Pagination getPageForCollection(Integer siteId, Integer memberId, int pageNo, int pageSize);
	
	public void updateFileByContent(Content bean,Boolean valid);
	
	public void updateBigPicture(Integer channel,Integer isBigPicture,String article,String classify);
	
	public List<Content> getListForAbout(String extTitle);
	
	public List<Content> getZhiShuPaiHangDaTu();
	
	public List<Content> getListForGunDong();
	
	public List<Content> getListForCurrentDay();
	/**
	 * 获取未推送给百度的文章
	 * @return
	 */
	public List<Content> getNoPushBaiduList();
	/**
	 * 更新已经推送给百度文章状态
	 */
	public void updatePushBaiduArticle(String ids);
	/**
	 * 加载更多文章
	 */
	public List<Content> getMoreArticles(int maxId,int minId,String category);
	/**
	 * 移动端
	 * 加载更新文章(上拉和下拉刷新)
	 * Param maxLengthId 页面最大文章ID
	 * Param articleLength 获取文章条数
	 * Param toward 刷新方向(true下拉,false上拉)
	 */
	public List<Content> getMoreArticles(int maxLengthId,int articleLength,boolean toward);
	/**
	 * 查询头条新闻
	 */
	public Content findTouTiao();
	
	/**
	 * 查询从昨天15点到今天15点最新10篇文章(用作更新网站右侧大家都在搜)
	 */
	public List<Content> findNewTenXinwen(String beforeDate,String afterDate);
	
	/**
	 * 查询人物专访文章
	 */
	public List<Content> findRenWuZhuanFangArticles();
	
	/**
	 * 查询学堂文章
	 */
	public List<Content> findXueTangArticles();
	
	/**
	 * 根据大数据学堂类型查询
	 */
	public List<Content> findXueTangByTypeArticls(String type);
	
	/**
	 * 普通活动列表
	 */
	public List<Content> findPuTongSports();
	
	/**
	 * 普通活动列表(手机)
	 */
	public List<Content> findPuTongSports(int maxId,int articleLength,String type);
	
	/**
	 * 普通精品活动列表(手机)
	 */
	public List<Content> findPuTongJPSports(int maxId,int articleLength,String type,String jpType);
	
	/**
	 * 我的活动列表
	 */
	public List<Content> findMySport(Integer num);
	
	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType,String startTime,String endTime);
	
	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType);
	
	/**
	 * 精品活动分类列表总览
	 */
	public List<Content> findMySportByCategoryDetail(String jpType,String startId,String stepLength);
	
	/**
	 * 查询当前活动列表
	 */
	public List<Content> findCurrentMonthSports(String startTime,String endTime);
	
	/**
	 * 查看最大文章数
	 */
	public int findMaxArticleNum();
	
	/**
	 * 更新文章中更多分类和ID
	 */
	public void updateMoreCategoryAndId(String oid,String releaseMoreCategory,String releaseMoreId);
}