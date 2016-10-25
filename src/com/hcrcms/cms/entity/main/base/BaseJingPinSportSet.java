package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;

public abstract class BaseJingPinSportSet implements Serializable{

	private Integer jpId;							//主键ID
	private java.util.Date twentyfour;			//大数据24小时
	private java.util.Date zhouZhouKanStart;	//大数据周周看开始时间
	private java.util.Date zhouZhoukanEnd;		//大数据周周看结束时间
	private java.util.Date touRongZiStart;		//投融资周报-月报-季报开始时间
	private java.util.Date touRongZiEnd;		//投融资周报-月报-季报结束时间
	private java.util.Date daJiaZhouYuLuStart;	//大咖周语录开始时间
	private java.util.Date daJiaZhouYuLuEnd;	//大咖周语录结束时间
	private java.util.Date zhouPinHuiStart;		//大数据周聘汇开始时间
	private java.util.Date zhouPinHuiEnd;		//大数据周聘汇结束时间
	private java.util.Date oneBookWeekStart;	//每周一本书开始时间
	private java.util.Date oneBookWeekEnd;		//每周一本书结束时间
	private java.util.Date jiDuYuGaoStart;		//大数据活动季度预告开始时间
	private java.util.Date jiDuYuGaoEnd;		//大数据活动季度预告结束时间
	
	public Integer getJpId() {
		return jpId;
	}
	public void setJpId(Integer jpId) {
		this.jpId = jpId;
	}
	public java.util.Date getTwentyfour() {
		return twentyfour;
	}
	public void setTwentyfour(java.util.Date twentyfour) {
		this.twentyfour = twentyfour;
	}
	public java.util.Date getZhouZhouKanStart() {
		return zhouZhouKanStart;
	}
	public void setZhouZhouKanStart(java.util.Date zhouZhouKanStart) {
		this.zhouZhouKanStart = zhouZhouKanStart;
	}
	public java.util.Date getZhouZhoukanEnd() {
		return zhouZhoukanEnd;
	}
	public void setZhouZhoukanEnd(java.util.Date zhouZhoukanEnd) {
		this.zhouZhoukanEnd = zhouZhoukanEnd;
	}
	public java.util.Date getTouRongZiStart() {
		return touRongZiStart;
	}
	public void setTouRongZiStart(java.util.Date touRongZiStart) {
		this.touRongZiStart = touRongZiStart;
	}
	public java.util.Date getTouRongZiEnd() {
		return touRongZiEnd;
	}
	public void setTouRongZiEnd(java.util.Date touRongZiEnd) {
		this.touRongZiEnd = touRongZiEnd;
	}
	public java.util.Date getDaJiaZhouYuLuStart() {
		return daJiaZhouYuLuStart;
	}
	public void setDaJiaZhouYuLuStart(java.util.Date daJiaZhouYuLuStart) {
		this.daJiaZhouYuLuStart = daJiaZhouYuLuStart;
	}
	public java.util.Date getDaJiaZhouYuLuEnd() {
		return daJiaZhouYuLuEnd;
	}
	public void setDaJiaZhouYuLuEnd(java.util.Date daJiaZhouYuLuEnd) {
		this.daJiaZhouYuLuEnd = daJiaZhouYuLuEnd;
	}
	public java.util.Date getZhouPinHuiStart() {
		return zhouPinHuiStart;
	}
	public void setZhouPinHuiStart(java.util.Date zhouPinHuiStart) {
		this.zhouPinHuiStart = zhouPinHuiStart;
	}
	public java.util.Date getZhouPinHuiEnd() {
		return zhouPinHuiEnd;
	}
	public void setZhouPinHuiEnd(java.util.Date zhouPinHuiEnd) {
		this.zhouPinHuiEnd = zhouPinHuiEnd;
	}
	public java.util.Date getOneBookWeekStart() {
		return oneBookWeekStart;
	}
	public void setOneBookWeekStart(java.util.Date oneBookWeekStart) {
		this.oneBookWeekStart = oneBookWeekStart;
	}
	public java.util.Date getOneBookWeekEnd() {
		return oneBookWeekEnd;
	}
	public void setOneBookWeekEnd(java.util.Date oneBookWeekEnd) {
		this.oneBookWeekEnd = oneBookWeekEnd;
	}
	public java.util.Date getJiDuYuGaoStart() {
		return jiDuYuGaoStart;
	}
	public void setJiDuYuGaoStart(java.util.Date jiDuYuGaoStart) {
		this.jiDuYuGaoStart = jiDuYuGaoStart;
	}
	public java.util.Date getJiDuYuGaoEnd() {
		return jiDuYuGaoEnd;
	}
	public void setJiDuYuGaoEnd(java.util.Date jiDuYuGaoEnd) {
		this.jiDuYuGaoEnd = jiDuYuGaoEnd;
	}
}
