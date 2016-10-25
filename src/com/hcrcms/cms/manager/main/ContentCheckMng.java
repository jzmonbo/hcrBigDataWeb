package com.hcrcms.cms.manager.main;

import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentCheck;

/**
 * 内容审核Manager接口
 * 
 * '内容'数据存在，则'内容审核'数据必须存在。
 */
public interface ContentCheckMng {
	public ContentCheck save(ContentCheck check, Content content);

	public ContentCheck update(ContentCheck bean);
}