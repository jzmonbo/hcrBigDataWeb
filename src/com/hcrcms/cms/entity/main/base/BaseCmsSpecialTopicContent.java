package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;
import java.util.Date;


/**
 * 专题内容
 */

public abstract class BaseCmsSpecialTopicContent  implements Serializable {

	private Integer id;
	private String mainPlace;
	private String subPlace;
	private String title;
	private String shortTitle;
	private String author;
	private String origin;
	private String originUrl;
	private String description;
	private Date releaseDate;
	private String mediaPath;
	private String mediaType;
	private String titleColor;
	private Integer isBold;
	private String titleImg;
	private String contentImg;
	private String watermarkerImg;					//水印图片
	private String typeImg;
	private String link;
	private String tplContent;
	private Integer needRegenerate;
	private String bigPictureImg;
	private Integer siteLocation;
	private Integer topicType;
	private Integer contentId;
	private com.hcrcms.cms.entity.main.CmsSpecialTopic specialTopic;
	private com.hcrcms.cms.entity.main.Content content;
	private Integer isSelect;								//企业专题中用到,标记模板中是否用到
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMainPlace() {
		return mainPlace;
	}
	public void setMainPlace(String mainPlace) {
		this.mainPlace = mainPlace;
	}
	public String getSubPlace() {
		return subPlace;
	}
	public void setSubPlace(String subPlace) {
		this.subPlace = subPlace;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getOriginUrl() {
		return originUrl;
	}
	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getMediaPath() {
		return mediaPath;
	}
	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getTitleColor() {
		return titleColor;
	}
	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}
	public Integer getIsBold() {
		return isBold;
	}
	public void setIsBold(Integer isBold) {
		this.isBold = isBold;
	}
	public String getTitleImg() {
		return titleImg;
	}
	public void setTitleImg(String titleImg) {
		this.titleImg = titleImg;
	}
	public String getContentImg() {
		return contentImg;
	}
	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
	}
	public String getTypeImg() {
		return typeImg;
	}
	public void setTypeImg(String typeImg) {
		this.typeImg = typeImg;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTplContent() {
		return tplContent;
	}
	public void setTplContent(String tplContent) {
		this.tplContent = tplContent;
	}
	public Integer getNeedRegenerate() {
		return needRegenerate;
	}
	public void setNeedRegenerate(Integer needRegenerate) {
		this.needRegenerate = needRegenerate;
	}
	public String getBigPictureImg() {
		return bigPictureImg;
	}
	public void setBigPictureImg(String bigPictureImg) {
		this.bigPictureImg = bigPictureImg;
	}
	public com.hcrcms.cms.entity.main.CmsSpecialTopic getSpecialTopic() {
		return specialTopic;
	}
	public void setSpecialTopic(
			com.hcrcms.cms.entity.main.CmsSpecialTopic specialTopic) {
		this.specialTopic = specialTopic;
	}
	public Integer getSiteLocation() {
		return siteLocation;
	}
	public void setSiteLocation(Integer siteLocation) {
		this.siteLocation = siteLocation;
	}
	public Integer getTopicType() {
		return topicType;
	}
	public void setTopicType(Integer topicType) {
		this.topicType = topicType;
	}
	public com.hcrcms.cms.entity.main.Content getContent() {
		return content;
	}
	public void setContent(com.hcrcms.cms.entity.main.Content content) {
		this.content = content;
	}
	public Integer getContentId() {
		return contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}
	public Integer getIsSelect() {
		return isSelect;
	}
	public void setIsSelect(Integer isSelect) {
		this.isSelect = isSelect;
	}
	public String getWatermarkerImg() {
		return watermarkerImg;
	}
	public void setWatermarkerImg(String watermarkerImg) {
		this.watermarkerImg = watermarkerImg;
	}
	
}