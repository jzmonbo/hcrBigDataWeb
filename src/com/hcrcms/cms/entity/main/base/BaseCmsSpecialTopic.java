package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;
import java.util.Date;


/**
 * 专题
 */

public abstract class BaseCmsSpecialTopic  implements Serializable {

	private Integer id;
	private String topicName;
	private String shortName;
	private String keywords;
	private String mainPlace;
	private String subPlace;
	private Date startTime;
	private Date endTime;
	private Integer isUse;
	private java.util.Set<com.hcrcms.cms.entity.main.CmsSpecialTopicContent> specialTopicContents;
	private Integer hasTemplate;				//企业专题里用到,是否有模板
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public java.util.Set<com.hcrcms.cms.entity.main.CmsSpecialTopicContent> getSpecialTopicContents() {
		return specialTopicContents;
	}
	public void setSpecialTopicContents(
			java.util.Set<com.hcrcms.cms.entity.main.CmsSpecialTopicContent> specialTopicContents) {
		this.specialTopicContents = specialTopicContents;
	}
	public Integer getIsUse() {
		return isUse;
	}
	public void setIsUse(Integer isUse) {
		this.isUse = isUse;
	}
	public Integer getHasTemplate() {
		return hasTemplate;
	}
	public void setHasTemplate(Integer hasTemplate) {
		this.hasTemplate = hasTemplate;
	}
	
}