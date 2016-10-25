package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;

/**
 * 热点导航
 * @author jingrun.zhang
 *
 */
public class BaseHotSpotNavigation implements Serializable{

	private Integer hotId;		//主键ID
	private String text;		//链接文字
	private String url;			//链接地址
	private String titleTag;	//title标签
	private Integer sort; 		//排列顺序
	
	public Integer getHotId() {
		return hotId;
	}
	public void setHotId(Integer hotId) {
		this.hotId = hotId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitleTag() {
		return titleTag;
	}
	public void setTitleTag(String titleTag) {
		this.titleTag = titleTag;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
