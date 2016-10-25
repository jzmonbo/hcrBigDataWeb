package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;

/**
 * 顶部搜索下方内链
 * @author jingrun.zhang
 *
 */
public class BaseSiteRightLink implements Serializable{

	private Integer srId;		//主键ID
	private String text;		//链接文字
	private String textUs;		//链接文字英文
	private String url;			//链接地址
	private String titleTag;	//title标签
	private Integer sort; 		//排列顺序
	
	public Integer getSrId() {
		return srId;
	}
	public void setSrId(Integer srId) {
		this.srId = srId;
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
	public String getTextUs() {
		return textUs;
	}
	public void setTextUs(String textUs) {
		this.textUs = textUs;
	}
	
}
