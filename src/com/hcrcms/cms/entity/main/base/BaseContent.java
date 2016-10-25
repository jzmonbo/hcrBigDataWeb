package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This is an object that contains data related to the jc_content table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_content"
 */

public abstract class BaseContent  implements Serializable {

	public static String REF = "Content";
	public static String PROP_STATUS = "status";
	public static String PROP_TYPE = "type";
	public static String PROP_RECOMMEND = "recommend";
	public static String PROP_SITE = "site";
	public static String PROP_USER = "user";
	public static String PROP_HAS_TITLE_IMG = "hasTitleImg";
	public static String PROP_SORT_DATE = "sortDate";
	public static String PROP_TOP_LEVEL = "topLevel";
	public static String PROP_COMMENTS_DAY = "commentsDay";
	public static String PROP_CONTENT_EXT = "contentExt";
	public static String PROP_VIEWS_DAY = "viewsDay";
	public static String PROP_UPS_DAY = "upsDay";
	public static String PROP_CHANNEL = "channel";
	public static String PROP_CONTENT_COUNT = "contentCount";
	public static String PROP_ID = "id";
	public static String PROP_DOWNLOADS_DAY = "downloadsDay";


	// constructors
	public BaseContent () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseContent (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseContent (
		java.lang.Integer id,
		com.hcrcms.core.entity.CmsSite site,
		java.util.Date sortDate,
		java.lang.Byte topLevel,
		java.lang.Boolean hasTitleImg,
		java.lang.Boolean recommend,
		java.lang.Byte status,
		java.lang.Integer viewsDay,
		java.lang.Short commentsDay,
		java.lang.Short downloadsDay,
		java.lang.Short upsDay) {

		this.setId(id);
		this.setSite(site);
		this.setSortDate(sortDate);
		this.setTopLevel(topLevel);
		this.setHasTitleImg(hasTitleImg);
		this.setRecommend(recommend);
		this.setStatus(status);
		this.setViewsDay(viewsDay);
		this.setCommentsDay(commentsDay);
		this.setDownloadsDay(downloadsDay);
		this.setUpsDay(upsDay);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.util.Date sortDate;
	private java.lang.Byte topLevel;
	private java.lang.Boolean hasTitleImg;
	private java.lang.Boolean recommend;
	private java.lang.Byte status;
	private java.lang.Integer viewsDay;
	private java.lang.Short commentsDay;
	private java.lang.Short downloadsDay;
	private java.lang.Short upsDay;
	private java.lang.Integer score;
	private java.lang.Integer isBigPicture;
	private java.lang.String article;			//文章
	private java.lang.String classify;			//位置或分类
	private java.lang.String bigPictureLocal;	//首页大图位置
	private java.lang.String xueTangCatory;		//大数据学堂分类 
	private java.lang.String xueTangFL;
	private java.lang.Integer isHome;			//是否首页
	private java.lang.Integer isPictureTop;		//图片置顶
	private com.hcrcms.cms.entity.main.Expert expert;			//专家外键ID
	private java.lang.String attr_selDefault;					//用于区分主页和频道页
	private java.lang.String attr_selPictureLocal;				//用于区分图片在主页还是频道页
	private java.lang.String attr_status;						//是否显示
	private java.lang.String indexrankLocal;					//指数排行大图位置
	private java.lang.String indexrankImg;						//指数排行大图
	//专家信息
	private java.lang.String uploadImgPath1;						//专家小图
	private java.lang.String uploadImgPath2;						//专家大图
	private java.util.Date sportDate;								//活动时间
	private java.util.Date attr_sportDate;							//自定义控件值
	private java.lang.Integer includeLetter;						//是否包含字母(如果包含值为1，否则为0.页面判断为1显示字数35，为0显示31个字)
	private List<String> keywords = new ArrayList<String>();		//保存搜索关键词
	private Integer recommandView;									//标识专访文章推荐到首页:0不推荐1已推荐
	private String specBigPicture;
	private Integer isPushBaidu;									//标识文章是否主动推送到百度:0未推送 1已推送
	private Integer isExpert;										//作者是否是专家:0不是 1是
	private Integer recommendInterview;								//是否推荐到人物专访大图:0不推荐 1已推荐
	private Integer mySport;										//属于数据猿的活动:0不是 1是
	private String releaseMoreCategory;								//一篇文章在多个分类里出现的分类ID，如果只在一个分类里只写本文分类ID
	private String releaseMoreId;									//一篇文章在多个分类里出现的主键ID，如果只在一个分类里只写本文ID
	//*******************************  为了修改一篇文章实现同时修改多篇文章而加字段  START ***********************************************
	private String renwuBigPictureImg;								//内容修改页面用到 人物专访图片
	private String xuetangBigPictureImg;							//内容修改页面用到 大数据学堂图片
	private String xuetangClassify;									//内容修改页面用到 大数据学堂分类
	private String zhishuClassify;									//内容修改页面用到 指数大图分类
	//*******************************  为了修改一篇文章实现同时修改多篇文章而加字段  END   ***********************************************

	// one to one
	private com.hcrcms.cms.entity.main.ContentExt contentExt;
	private com.hcrcms.cms.entity.main.ContentCount contentCount;
	private com.hcrcms.cms.entity.main.CmsSpecialTopicContent specialTopicContent;

	// many to one
	private com.hcrcms.cms.entity.main.ContentType type;
	private com.hcrcms.core.entity.CmsSite site;
	private com.hcrcms.core.entity.CmsUser user;
	private com.hcrcms.cms.entity.main.Channel channel;
	private com.hcrcms.cms.entity.main.CmsModel model;

	// collections
	private java.util.Set<com.hcrcms.cms.entity.main.Channel> channels;
	private java.util.Set<com.hcrcms.cms.entity.main.CmsTopic> topics;
	private java.util.Set<com.hcrcms.core.entity.CmsGroup> viewGroups;
	private java.util.List<com.hcrcms.cms.entity.main.ContentTag> tags;
	private java.util.List<com.hcrcms.cms.entity.main.ContentPicture> pictures;
	private java.util.List<com.hcrcms.cms.entity.main.ContentAttachment> attachments;
	private java.util.Set<com.hcrcms.cms.entity.main.ContentTxt> contentTxtSet;
	private java.util.Set<com.hcrcms.cms.entity.main.ContentCheck> contentCheckSet;
	private java.util.Map<java.lang.String, java.lang.String> attr;
	private java.util.Set<com.hcrcms.core.entity.CmsUser> collectUsers;
	private java.util.Set<com.hcrcms.cms.entity.assist.CmsComment> comments;
	private java.util.Set<com.hcrcms.cms.entity.assist.CmsFile> files;
	private java.util.Set<com.hcrcms.cms.entity.assist.CmsJobApply> jobApplys;
	private java.util.Set<com.hcrcms.cms.entity.assist.CmsScoreRecord> scoreRecordSet;
	



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="content_id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: sort_date
	 */
	public java.util.Date getSortDate () {
		return sortDate;
	}

	/**
	 * Set the value related to the column: sort_date
	 * @param sortDate the sort_date value
	 */
	public void setSortDate (java.util.Date sortDate) {
		this.sortDate = sortDate;
	}


	/**
	 * Return the value associated with the column: top_level
	 */
	public java.lang.Byte getTopLevel () {
		return topLevel;
	}

	/**
	 * Set the value related to the column: top_level
	 * @param topLevel the top_level value
	 */
	public void setTopLevel (java.lang.Byte topLevel) {
		this.topLevel = topLevel;
	}


	/**
	 * Return the value associated with the column: has_title_img
	 */
	public java.lang.Boolean getHasTitleImg () {
		return hasTitleImg;
	}

	/**
	 * Set the value related to the column: has_title_img
	 * @param hasTitleImg the has_title_img value
	 */
	public void setHasTitleImg (java.lang.Boolean hasTitleImg) {
		this.hasTitleImg = hasTitleImg;
	}


	/**
	 * Return the value associated with the column: is_recommend
	 */
	public java.lang.Boolean getRecommend () {
		return recommend;
	}

	/**
	 * Set the value related to the column: is_recommend
	 * @param recommend the is_recommend value
	 */
	public void setRecommend (java.lang.Boolean recommend) {
		this.recommend = recommend;
	}


	/**
	 * Return the value associated with the column: status
	 */
	public java.lang.Byte getStatus () {
		return status;
	}

	/**
	 * Set the value related to the column: status
	 * @param status the status value
	 */
	public void setStatus (java.lang.Byte status) {
		this.status = status;
	}


	/**
	 * Return the value associated with the column: views_day
	 */
	public java.lang.Integer getViewsDay () {
		return viewsDay;
	}

	/**
	 * Set the value related to the column: views_day
	 * @param viewsDay the views_day value
	 */
	public void setViewsDay (java.lang.Integer viewsDay) {
		this.viewsDay = viewsDay;
	}


	/**
	 * Return the value associated with the column: comments_day
	 */
	public java.lang.Short getCommentsDay () {
		return commentsDay;
	}

	/**
	 * Set the value related to the column: comments_day
	 * @param commentsDay the comments_day value
	 */
	public void setCommentsDay (java.lang.Short commentsDay) {
		this.commentsDay = commentsDay;
	}
	


	public java.util.Set<com.hcrcms.cms.entity.assist.CmsFile> getFiles() {
		return files;
	}

	public void setFiles(java.util.Set<com.hcrcms.cms.entity.assist.CmsFile> files) {
		this.files = files;
	}
	
	public java.util.Set<com.hcrcms.cms.entity.assist.CmsJobApply> getJobApplys() {
			return jobApplys;
	}
	
	public void setJobApplys(
				java.util.Set<com.hcrcms.cms.entity.assist.CmsJobApply> jobApplys) {
			this.jobApplys = jobApplys;
	}
	

	

	public java.util.Set<com.hcrcms.cms.entity.assist.CmsScoreRecord> getScoreRecordSet() {
		return scoreRecordSet;
	}

	public void setScoreRecordSet(
			java.util.Set<com.hcrcms.cms.entity.assist.CmsScoreRecord> scoreRecordSet) {
		this.scoreRecordSet = scoreRecordSet;
	}

	/**
	 * Return the value associated with the column: downloads_day
	 */
	public java.lang.Short getDownloadsDay () {
		return downloadsDay;
	}

	/**
	 * Set the value related to the column: downloads_day
	 * @param downloadsDay the downloads_day value
	 */
	public void setDownloadsDay (java.lang.Short downloadsDay) {
		this.downloadsDay = downloadsDay;
	}


	/**
	 * Return the value associated with the column: ups_day
	 */
	public java.lang.Short getUpsDay () {
		return upsDay;
	}

	/**
	 * Set the value related to the column: ups_day
	 * @param upsDay the ups_day value
	 */
	public void setUpsDay (java.lang.Short upsDay) {
		this.upsDay = upsDay;
	}
	

	public java.lang.Integer getScore() {
		return score;
	}

	public void setScore(java.lang.Integer score) {
		this.score = score;
	}

	/**
	 * Return the value associated with the column: contentExt
	 */
	public com.hcrcms.cms.entity.main.ContentExt getContentExt () {
		return contentExt;
	}

	/**
	 * Set the value related to the column: contentExt
	 * @param contentExt the contentExt value
	 */
	public void setContentExt (com.hcrcms.cms.entity.main.ContentExt contentExt) {
		this.contentExt = contentExt;
	}


	/**
	 * Return the value associated with the column: contentCount
	 */
	public com.hcrcms.cms.entity.main.ContentCount getContentCount () {
		return contentCount;
	}

	/**
	 * Set the value related to the column: contentCount
	 * @param contentCount the contentCount value
	 */
	public void setContentCount (com.hcrcms.cms.entity.main.ContentCount contentCount) {
		this.contentCount = contentCount;
	}


	/**
	 * Return the value associated with the column: type_id
	 */
	public com.hcrcms.cms.entity.main.ContentType getType () {
		return type;
	}

	/**
	 * Set the value related to the column: type_id
	 * @param type the type_id value
	 */
	public void setType (com.hcrcms.cms.entity.main.ContentType type) {
		this.type = type;
	}


	/**
	 * Return the value associated with the column: site_id
	 */
	public com.hcrcms.core.entity.CmsSite getSite () {
		return site;
	}

	/**
	 * Set the value related to the column: site_id
	 * @param site the site_id value
	 */
	public void setSite (com.hcrcms.core.entity.CmsSite site) {
		this.site = site;
	}


	/**
	 * Return the value associated with the column: user_id
	 */
	public com.hcrcms.core.entity.CmsUser getUser () {
		return user;
	}

	/**
	 * Set the value related to the column: user_id
	 * @param user the user_id value
	 */
	public void setUser (com.hcrcms.core.entity.CmsUser user) {
		this.user = user;
	}


	/**
	 * Return the value associated with the column: channel_id
	 */
	public com.hcrcms.cms.entity.main.Channel getChannel () {
		return channel;
	}

	/**
	 * Set the value related to the column: channel_id
	 * @param channel the channel_id value
	 */
	public void setChannel (com.hcrcms.cms.entity.main.Channel channel) {
		this.channel = channel;
	}
	
	public com.hcrcms.cms.entity.main.CmsModel getModel() {
		return model;
	}

	public void setModel(com.hcrcms.cms.entity.main.CmsModel model) {
		this.model = model;
	}


	/**
	 * Return the value associated with the column: channels
	 */
	public java.util.Set<com.hcrcms.cms.entity.main.Channel> getChannels () {
		return channels;
	}

	/**
	 * Set the value related to the column: channels
	 * @param channels the channels value
	 */
	public void setChannels (java.util.Set<com.hcrcms.cms.entity.main.Channel> channels) {
		this.channels = channels;
	}

	/**
	 * Return the value associated with the column: topics
	 */
	public java.util.Set<com.hcrcms.cms.entity.main.CmsTopic> getTopics () {
		return topics;
	}

	/**
	 * Set the value related to the column: topics
	 * @param topics the topics value
	 */
	public void setTopics (java.util.Set<com.hcrcms.cms.entity.main.CmsTopic> topics) {
		this.topics = topics;
	}


	/**
	 * Return the value associated with the column: viewGroups
	 */
	public java.util.Set<com.hcrcms.core.entity.CmsGroup> getViewGroups () {
		return viewGroups;
	}

	/**
	 * Set the value related to the column: viewGroups
	 * @param viewGroups the viewGroups value
	 */
	public void setViewGroups (java.util.Set<com.hcrcms.core.entity.CmsGroup> viewGroups) {
		this.viewGroups = viewGroups;
	}


	/**
	 * Return the value associated with the column: tags
	 */
	public java.util.List<com.hcrcms.cms.entity.main.ContentTag> getTags () {
		return tags;
	}

	/**
	 * Set the value related to the column: tags
	 * @param tags the tags value
	 */
	public void setTags (java.util.List<com.hcrcms.cms.entity.main.ContentTag> tags) {
		this.tags = tags;
	}


	/**
	 * Return the value associated with the column: pictures
	 */
	public java.util.List<com.hcrcms.cms.entity.main.ContentPicture> getPictures () {
		return pictures;
	}

	/**
	 * Set the value related to the column: pictures
	 * @param pictures the pictures value
	 */
	public void setPictures (java.util.List<com.hcrcms.cms.entity.main.ContentPicture> pictures) {
		this.pictures = pictures;
	}


	/**
	 * Return the value associated with the column: attachments
	 */
	public java.util.List<com.hcrcms.cms.entity.main.ContentAttachment> getAttachments () {
		return attachments;
	}

	/**
	 * Set the value related to the column: attachments
	 * @param attachments the attachments value
	 */
	public void setAttachments (java.util.List<com.hcrcms.cms.entity.main.ContentAttachment> attachments) {
		this.attachments = attachments;
	}


	/**
	 * Return the value associated with the column: contentTxtSet
	 */
	public java.util.Set<com.hcrcms.cms.entity.main.ContentTxt> getContentTxtSet () {
		return contentTxtSet;
	}

	/**
	 * Set the value related to the column: contentTxtSet
	 * @param contentTxtSet the contentTxtSet value
	 */
	public void setContentTxtSet (java.util.Set<com.hcrcms.cms.entity.main.ContentTxt> contentTxtSet) {
		this.contentTxtSet = contentTxtSet;
	}


	/**
	 * Return the value associated with the column: contentCheckSet
	 */
	public java.util.Set<com.hcrcms.cms.entity.main.ContentCheck> getContentCheckSet () {
		return contentCheckSet;
	}

	/**
	 * Set the value related to the column: contentCheckSet
	 * @param contentCheckSet the contentCheckSet value
	 */
	public void setContentCheckSet (java.util.Set<com.hcrcms.cms.entity.main.ContentCheck> contentCheckSet) {
		this.contentCheckSet = contentCheckSet;
	}
	/**
	 * Return the value associated with the column: attr
	 */
	public java.util.Map<java.lang.String, java.lang.String> getAttr () {
		return attr;
	}
	

	public java.util.Set<com.hcrcms.core.entity.CmsUser> getCollectUsers() {
		return collectUsers;
	}

	public void setCollectUsers(
			java.util.Set<com.hcrcms.core.entity.CmsUser> collectUsers) {
		this.collectUsers = collectUsers;
	}
	

	public java.util.Set<com.hcrcms.cms.entity.assist.CmsComment> getComments() {
		return comments;
	}

	public void setComments(
			java.util.Set<com.hcrcms.cms.entity.assist.CmsComment> comments) {
		this.comments = comments;
	}

	/**
	 * Set the value related to the column: attr
	 * @param attr the attr value
	 */
	public void setAttr (java.util.Map<java.lang.String, java.lang.String> attr) {
		this.attr = attr;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.hcrcms.cms.entity.main.Content)) return false;
		else {
			com.hcrcms.cms.entity.main.Content content = (com.hcrcms.cms.entity.main.Content) obj;
			if (null == this.getId() || null == content.getId()) return false;
			else return (this.getId().equals(content.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}

	public java.lang.Integer getIsBigPicture() {
		return isBigPicture;
	}

	public void setIsBigPicture(java.lang.Integer isBigPicture) {
		this.isBigPicture = isBigPicture;
	}

	public java.lang.String getArticle() {
		return article;
	}

	public void setArticle(java.lang.String article) {
		this.article = article;
	}

	public java.lang.String getClassify() {
		return classify;
	}

	public void setClassify(java.lang.String classify) {
		this.classify = classify;
	}

	public java.lang.String getBigPictureLocal() {
		return bigPictureLocal;
	}

	public void setBigPictureLocal(java.lang.String bigPictureLocal) {
		this.bigPictureLocal = bigPictureLocal;
	}

	public java.lang.String getXueTangCatory() {
		return xueTangCatory;
	}

	public void setXueTangCatory(java.lang.String xueTangCatory) {
		this.xueTangCatory = xueTangCatory;
	}

	public java.lang.Integer getIsHome() {
		return isHome;
	}

	public void setIsHome(java.lang.Integer isHome) {
		this.isHome = isHome;
	}

	public java.lang.Integer getIsPictureTop() {
		return isPictureTop;
	}

	public void setIsPictureTop(java.lang.Integer isPictureTop) {
		this.isPictureTop = isPictureTop;
	}

	public com.hcrcms.cms.entity.main.Expert getExpert() {
		return expert;
	}

	public void setExpert(com.hcrcms.cms.entity.main.Expert expert) {
		this.expert = expert;
	}

	public java.lang.String getAttr_selDefault() {
		return attr_selDefault;
	}

	public void setAttr_selDefault(java.lang.String attr_selDefault) {
		this.attr_selDefault = attr_selDefault;
	}

	public java.lang.String getAttr_selPictureLocal() {
		return attr_selPictureLocal;
	}

	public void setAttr_selPictureLocal(java.lang.String attr_selPictureLocal) {
		this.attr_selPictureLocal = attr_selPictureLocal;
	}

	public java.lang.String getUploadImgPath1() {
		return uploadImgPath1;
	}

	public void setUploadImgPath1(java.lang.String uploadImgPath1) {
		this.uploadImgPath1 = uploadImgPath1;
	}

	public java.lang.String getUploadImgPath2() {
		return uploadImgPath2;
	}

	public void setUploadImgPath2(java.lang.String uploadImgPath2) {
		this.uploadImgPath2 = uploadImgPath2;
	}

	public java.lang.String getAttr_status() {
		return attr_status;
	}

	public void setAttr_status(java.lang.String attr_status) {
		this.attr_status = attr_status;
	}

	public java.util.Date getSportDate() {
		return sportDate;
	}

	public void setSportDate(java.util.Date sportDate) {
		this.sportDate = sportDate;
	}

	public java.util.Date getAttr_sportDate() {
		return attr_sportDate;
	}

	public void setAttr_sportDate(java.util.Date attr_sportDate) {
		this.attr_sportDate = attr_sportDate;
	}

	public java.lang.Integer getIncludeLetter() {
		return includeLetter;
	}

	public void setIncludeLetter(java.lang.Integer includeLetter) {
		this.includeLetter = includeLetter;
	}

	public java.lang.String getXueTangFL() {
		return xueTangFL;
	}

	public void setXueTangFL(java.lang.String xueTangFL) {
		this.xueTangFL = xueTangFL;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public com.hcrcms.cms.entity.main.CmsSpecialTopicContent getSpecialTopicContent() {
		return specialTopicContent;
	}

	public void setSpecialTopicContent(
			com.hcrcms.cms.entity.main.CmsSpecialTopicContent specialTopicContent) {
		this.specialTopicContent = specialTopicContent;
	}

	public java.lang.String getIndexrankLocal() {
		return indexrankLocal;
	}

	public void setIndexrankLocal(java.lang.String indexrankLocal) {
		this.indexrankLocal = indexrankLocal;
	}

	public java.lang.String getIndexrankImg() {
		return indexrankImg;
	}

	public void setIndexrankImg(java.lang.String indexrankImg) {
		this.indexrankImg = indexrankImg;
	}

	public Integer getRecommandView() {
		return recommandView;
	}

	public void setRecommandView(Integer recommandView) {
		this.recommandView = recommandView;
	}

	public String getSpecBigPicture() {
		return specBigPicture;
	}

	public void setSpecBigPicture(String specBigPicture) {
		this.specBigPicture = specBigPicture;
	}

	public Integer getIsPushBaidu() {
		return isPushBaidu;
	}

	public void setIsPushBaidu(Integer isPushBaidu) {
		this.isPushBaidu = isPushBaidu;
	}

	public Integer getIsExpert() {
		return isExpert;
	}

	public void setIsExpert(Integer isExpert) {
		this.isExpert = isExpert;
	}

	public Integer getRecommendInterview() {
		return recommendInterview;
	}

	public void setRecommendInterview(Integer recommendInterview) {
		this.recommendInterview = recommendInterview;
	}

	public Integer getMySport() {
		return mySport;
	}

	public void setMySport(Integer mySport) {
		this.mySport = mySport;
	}

	public String getReleaseMoreCategory() {
		return releaseMoreCategory;
	}

	public void setReleaseMoreCategory(String releaseMoreCategory) {
		this.releaseMoreCategory = releaseMoreCategory;
	}

	public String getReleaseMoreId() {
		return releaseMoreId;
	}

	public void setReleaseMoreId(String releaseMoreId) {
		this.releaseMoreId = releaseMoreId;
	}

	public String getRenwuBigPictureImg() {
		return renwuBigPictureImg;
	}

	public void setRenwuBigPictureImg(String renwuBigPictureImg) {
		this.renwuBigPictureImg = renwuBigPictureImg;
	}

	public String getXuetangBigPictureImg() {
		return xuetangBigPictureImg;
	}

	public void setXuetangBigPictureImg(String xuetangBigPictureImg) {
		this.xuetangBigPictureImg = xuetangBigPictureImg;
	}

	public String getXuetangClassify() {
		return xuetangClassify;
	}

	public void setXuetangClassify(String xuetangClassify) {
		this.xuetangClassify = xuetangClassify;
	}

	public String getZhishuClassify() {
		return zhishuClassify;
	}

	public void setZhishuClassify(String zhishuClassify) {
		this.zhishuClassify = zhishuClassify;
	}
}