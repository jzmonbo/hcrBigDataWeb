package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

/**
 * 搜索指数排行
 * @author jingrun.zhang
 *
 */
public class BaseCmsSearchEngine  implements Serializable{

	private Integer searchId;		//主键ID
	private Integer category;		//分类
	private String company;			//公司名称
	private String url;				//公司网址
	private String bdss;			//百度搜索
	private String bdtb;			//百度贴吧
	private String bdzsWhole;		//百度指数整体
	private String bdzsMobile;		//百度指数移动端
	private String sgss;			//搜狗搜索
	private String sgwxss;			//搜狗微信搜索
	private String hs;				//360搜索
	private String alexa;			//alexa全球排名
	private String wbss;			//微博搜索
	private String bdssxw;			//百度新闻
	private String sgssxw;			//搜狗新闻
	private String hsxw;			//360新闻
	private String total;			//总积分
	private Integer nowrank;		//现在排名
	private Integer previousRank;	//之前排名
	private String companyLogo;		//公司LOGO
	private String companyDesc;		//公司描述
	private String companyKeyword;	//公司名称关键词
	
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBdss() {
		return bdss;
	}
	public void setBdss(String bdss) {
		this.bdss = bdss;
	}
	public String getBdtb() {
		return bdtb;
	}
	public void setBdtb(String bdtb) {
		this.bdtb = bdtb;
	}
	public String getBdzsWhole() {
		return bdzsWhole;
	}
	public void setBdzsWhole(String bdzsWhole) {
		this.bdzsWhole = bdzsWhole;
	}
	public String getBdzsMobile() {
		return bdzsMobile;
	}
	public void setBdzsMobile(String bdzsMobile) {
		this.bdzsMobile = bdzsMobile;
	}
	public String getSgss() {
		return sgss;
	}
	public void setSgss(String sgss) {
		this.sgss = sgss;
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
	public String getAlexa() {
		return alexa;
	}
	public void setAlexa(String alexa) {
		this.alexa = alexa;
	}
	public String getWbss() {
		return wbss;
	}
	public void setWbss(String wbss) {
		this.wbss = wbss;
	}
	public String getBdssxw() {
		return bdssxw;
	}
	public void setBdssxw(String bdssxw) {
		this.bdssxw = bdssxw;
	}
	public String getSgssxw() {
		return sgssxw;
	}
	public void setSgssxw(String sgssxw) {
		this.sgssxw = sgssxw;
	}
	public String getHsxw() {
		return hsxw;
	}
	public void setHsxw(String hsxw) {
		this.hsxw = hsxw;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public Integer getNowrank() {
		return nowrank;
	}
	public void setNowrank(Integer nowrank) {
		this.nowrank = nowrank;
	}
	public Integer getPreviousRank() {
		return previousRank;
	}
	public void setPreviousRank(Integer previousRank) {
		this.previousRank = previousRank;
	}
	public String getCompanyLogo() {
		return companyLogo;
	}
	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}
	public String getCompanyDesc() {
		return companyDesc;
	}
	public void setCompanyDesc(String companyDesc) {
		this.companyDesc = companyDesc;
	}
	public String getCompanyKeyword() {
		return companyKeyword;
	}
	public void setCompanyKeyword(String companyKeyword) {
		this.companyKeyword = companyKeyword;
	}
	
}
