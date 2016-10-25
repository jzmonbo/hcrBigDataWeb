package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

public class BaseCmsTopicReport implements Serializable{

	private Integer rId;
	private String title;			//标题
	private String url;				//链接地址
	private String description;			//企业专题相关报道说明
	private com.hcrcms.cms.entity.assist.CmsTopicEnterprise topicEnterprise;
	
	public Integer getrId() {
		return rId;
	}
	public void setrId(Integer rId) {
		this.rId = rId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public com.hcrcms.cms.entity.assist.CmsTopicEnterprise getTopicEnterprise() {
		return topicEnterprise;
	}
	public void setTopicEnterprise(
			com.hcrcms.cms.entity.assist.CmsTopicEnterprise topicEnterprise) {
		this.topicEnterprise = topicEnterprise;
	}
	
}
