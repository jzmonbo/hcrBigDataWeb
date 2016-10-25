package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;
import java.util.Set;

public class BaseCmsTopicEnterprise implements Serializable{

	private Integer entId;
	private String shortVideo;		//短视频地址
	private String longVideo;		//长视频地址
	private String shareTopic;		//分享专题:文章ID间以逗号分开
	private String interview;		//人物专访:文章ID间以逗号分开
	private String description;			//企业专题说明
	private Integer meetId;			//标记专题ID(这里手动写入专题列表主键)
	private String topicName;		//专题名称
	private String imagePath;		//图片路径
	private Set<com.hcrcms.cms.entity.assist.CmsTopicReport> topicReport;
	
	public Integer getEntId() {
		return entId;
	}
	public void setEntId(Integer entId) {
		this.entId = entId;
	}
	public String getShortVideo() {
		return shortVideo;
	}
	public void setShortVideo(String shortVideo) {
		this.shortVideo = shortVideo;
	}
	public String getLongVideo() {
		return longVideo;
	}
	public void setLongVideo(String longVideo) {
		this.longVideo = longVideo;
	}
	public String getShareTopic() {
		return shareTopic;
	}
	public void setShareTopic(String shareTopic) {
		this.shareTopic = shareTopic;
	}
	public String getInterview() {
		return interview;
	}
	public void setInterview(String interview) {
		this.interview = interview;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<com.hcrcms.cms.entity.assist.CmsTopicReport> getTopicReport() {
		return topicReport;
	}
	public void setTopicReport(
			Set<com.hcrcms.cms.entity.assist.CmsTopicReport> topicReport) {
		this.topicReport = topicReport;
	}
	public Integer getMeetId() {
		return meetId;
	}
	public void setMeetId(Integer meetId) {
		this.meetId = meetId;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
}
