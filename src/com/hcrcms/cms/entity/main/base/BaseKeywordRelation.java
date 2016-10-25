package com.hcrcms.cms.entity.main.base;

import java.io.Serializable;

public abstract class BaseKeywordRelation implements Serializable {

	private Integer id;				//主键ID
	private String name;			//关键词
	private Integer parentId;		//父结点
	private String note;			//说明
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
