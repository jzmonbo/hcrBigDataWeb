package com.hcrcms.core.entity;

import java.util.Date;

/**
 * 微信access_token值
 * @author jingrun.zhang
 *
 */
public class WeixinAccessToken {

	private String access_token;
	private String expires_in;
	private String refresh_token;
	private String openid;
	private String scope;
	private String unionid;
	private Date accessDate;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public Date getAccessDate() {
		return accessDate;
	}
	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}
	/**
	 * 获取是否过期时间
	 * 返回 : 正数(未过期) , 负数(已过期)
	 */
	public long getIsOverDueAccessDate(){
		long result = 1;
		long accessTime = this.accessDate.getTime();
		long expire = Integer.parseInt(expires_in)*1000;
		long nowTime = new Date().getTime()-expire;
		if (accessTime > nowTime){
			result = 1;
		}else{
			result = -1;
		}
		return result;
	}
	
	/**
	 * 查看刷新过的access_token,如果超过30天,则从session中删掉
	 */
	public long getIsFinalOverDueAccessDate(){
		long result = 1;
		long accessTime = this.accessDate.getTime();
		int expire = 2592000;							//30天
		long nowTime = new Date().getTime()-expire;
		if (accessTime > nowTime){
			result = 1;
		}else{
			result = -1;
		}
		return result;
	}
}
