package com.hcrcms.cms.entity.main;

import com.hcrcms.cms.entity.main.base.BaseJingPinSportSet;

public class JingPinSportAssist{

	private String timeFrame;				//设置时间范围
	private String description;				//文字说明
	private String url;						//链接
	private String urlMore;					//MORE地址
	
	public String getTimeFrame() {
		return timeFrame;
	}
	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlMore() {
		return urlMore;
	}
	public void setUrlMore(String urlMore) {
		this.urlMore = urlMore;
	}
}
