package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * 搜索指数排行
 * @author jingrun.zhang
 *
 */
public class BaseCmsSearchEngineBase  implements Serializable{

	private Integer searchId;		//主键ID
	private Integer category;		//分类
	private String company;			//公司名称
	private String companyKeyword;	//公司名称关键词
	private String bdss;			//百度搜索
	private String bdssxw;			//百度新闻
	private String sgss;			//搜狗搜索
	private String sgssxw;			//搜狗新闻
	private String sgwxss;			//搜狗微信搜索
	private String hs;				//360搜索
	private String hsxw;			//360新闻
	
	public Integer getSearchId() {
		return searchId;
	}
	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
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
	public String getBdss() {
		return bdss;
	}
	public void setBdss(String bdss) {
		this.bdss = bdss;
	}
	public String getBdssxw() {
		return bdssxw;
	}
	public void setBdssxw(String bdssxw) {
		this.bdssxw = bdssxw;
	}
	public String getSgss() {
		return sgss;
	}
	public void setSgss(String sgss) {
		this.sgss = sgss;
	}
	public String getSgssxw() {
		return sgssxw;
	}
	public void setSgssxw(String sgssxw) {
		this.sgssxw = sgssxw;
	}
	public String getSgwxss() {
		return sgwxss;
	}
	public void setSgwxss(String sgwxss) {
		this.sgwxss = sgwxss;
	}
	public String getHs() {
		return hs;
	}
	public void setHs(String hs) {
		this.hs = hs;
	}
	public String getHsxw() {
		return hsxw;
	}
	public void setHsxw(String hsxw) {
		this.hsxw = hsxw;
	}
}
