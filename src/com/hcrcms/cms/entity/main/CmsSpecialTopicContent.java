package com.hcrcms.cms.entity.main;

import static com.hcrcms.common.web.Constants.SPT;

import java.io.Serializable;

import com.hcrcms.cms.entity.main.base.BaseCmsSpecialTopicContent;
import com.hcrcms.core.entity.CmsSite;

public class CmsSpecialTopicContent extends BaseCmsSpecialTopicContent implements Serializable {
	
	public Integer getTopicId(){
		return getSpecialTopic().getId();
	}
	
	public String getUrl(){
		Content content = getContent();
		CmsSite site = content.getSite();
		StringBuilder url = site.getUrlBuffer(true, true, false);
		url.append(SPT).append(content.getChannel().getPath());
		url.append(SPT).append(content.getId()).append(site.getDynamicSuffix());
		return url.toString();
	}
	
	public String getMainSubPlace(){
		return getSpecialTopic().getMainPlace()+"|"+getSpecialTopic().getSubPlace();
	}
}