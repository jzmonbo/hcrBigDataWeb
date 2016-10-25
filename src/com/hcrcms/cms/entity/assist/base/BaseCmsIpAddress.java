package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

public class BaseCmsIpAddress implements Serializable{

	private java.lang.Integer id;
	private java.lang.Double ip1;
	private java.lang.Double ip2;
	private java.lang.String province;
	private java.lang.String city;
	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public java.lang.Double getIp1() {
		return ip1;
	}
	public void setIp1(java.lang.Double ip1) {
		this.ip1 = ip1;
	}
	public java.lang.Double getIp2() {
		return ip2;
	}
	public void setIp2(java.lang.Double ip2) {
		this.ip2 = ip2;
	}
	public java.lang.String getProvince() {
		return province;
	}
	public void setProvince(java.lang.String province) {
		this.province = province;
	}
	public java.lang.String getCity() {
		return city;
	}
	public void setCity(java.lang.String city) {
		this.city = city;
	}
}
