package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * 搜索指数排行
 * @author jingrun.zhang
 *
 */
public class BaseCmsSearchEngineLog  implements Serializable{

	private Integer slogId;		//主键ID
	private Integer category;		//分类
	private String company;			//公司名称
	private String companyKeyword;	//公司关键词
	private String bdssAbnormal;	//百度搜索异常
	private String bdssYestoday;	//百度搜索昨天
	private String sgssAbnormal;	//搜狗搜索异常
	private String sgssYestoday;	//搜狗搜索昨天
	private String hsAbnormal;		//好搜搜索异常
	private String hsYestoday;		//好搜搜索昨天
	private java.util.Date createtime;		//创建时间
	
	public Integer getSlogId() {
		return slogId;
	}
	public void setSlogId(Integer slogId) {
		this.slogId = slogId;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCompanyKeyword() {
		return companyKeyword;
	}
	public void setCompanyKeyword(String companyKeyword) {
		this.companyKeyword = companyKeyword;
	}
	public String getBdssAbnormal() {
		return bdssAbnormal;
	}
	public void setBdssAbnormal(String bdssAbnormal) {
		this.bdssAbnormal = bdssAbnormal;
	}
	public String getBdssYestoday() {
		return bdssYestoday;
	}
	public void setBdssYestoday(String bdssYestoday) {
		this.bdssYestoday = bdssYestoday;
	}
	public String getSgssAbnormal() {
		return sgssAbnormal;
	}
	public void setSgssAbnormal(String sgssAbnormal) {
		this.sgssAbnormal = sgssAbnormal;
	}
	public String getSgssYestoday() {
		return sgssYestoday;
	}
	public void setSgssYestoday(String sgssYestoday) {
		this.sgssYestoday = sgssYestoday;
	}
	public String getHsAbnormal() {
		return hsAbnormal;
	}
	public void setHsAbnormal(String hsAbnormal) {
		this.hsAbnormal = hsAbnormal;
	}
	public String getHsYestoday() {
		return hsYestoday;
	}
	public void setHsYestoday(String hsYestoday) {
		this.hsYestoday = hsYestoday;
	}
	public java.util.Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}
}
