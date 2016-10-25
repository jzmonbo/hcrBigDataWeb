package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;

public class BaseExpert implements Serializable{

	private java.lang.Integer expertId;				//主键ID
	private java.lang.String author;				//作者
	private java.lang.String authorPy;				//作者拼音
	private java.util.Date releaseDate;			//创建时间
	private java.lang.String bigImg;				//专家大图片 
	private java.lang.String smallImg;				//专家小图片
	private java.lang.Integer sort;					//索引位置
	private java.lang.String company;				//公司名称
	private java.lang.String description;			//简介
	private java.util.Set<com.hcrcms.cms.entity.main.Content> experts;
	
	public java.lang.Integer getExpertId() {
		return expertId;
	}
	public void setExpertId(java.lang.Integer expertId) {
		this.expertId = expertId;
	}
	public java.lang.String getAuthor() {
		return author;
	}
	public void setAuthor(java.lang.String author) {
		this.author = author;
	}
	public java.util.Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(java.util.Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public java.lang.String getBigImg() {
		return bigImg;
	}
	public void setBigImg(java.lang.String bigImg) {
		this.bigImg = bigImg;
	}
	public java.lang.String getSmallImg() {
		return smallImg;
	}
	public void setSmallImg(java.lang.String smallImg) {
		this.smallImg = smallImg;
	}
	public java.lang.Integer getSort() {
		return sort;
	}
	public void setSort(java.lang.Integer sort) {
		this.sort = sort;
	}
	public java.util.Set<com.hcrcms.cms.entity.main.Content> getExperts() {
		return experts;
	}
	public void setExperts(java.util.Set<com.hcrcms.cms.entity.main.Content> experts) {
		this.experts = experts;
	}
	public java.lang.String getCompany() {
		return company;
	}
	public void setCompany(java.lang.String company) {
		this.company = company;
	}
	public java.lang.String getDescription() {
		return description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
	}
	public java.lang.String getAuthorPy() {
		return authorPy;
	}
	public void setAuthorPy(java.lang.String authorPy) {
		this.authorPy = authorPy;
	}
}
