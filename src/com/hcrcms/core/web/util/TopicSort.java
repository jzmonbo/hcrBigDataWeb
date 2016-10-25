package com.hcrcms.core.web.util;

import java.util.Comparator;

import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;

public class TopicSort implements Comparator{  
    public int compare(Object arg0,Object arg1){  
    	CmsSpecialTopicContent content0 = (CmsSpecialTopicContent)arg0;  
    	CmsSpecialTopicContent content1 = (CmsSpecialTopicContent)arg1;  
        int flag = content0.getReleaseDate().compareTo(content1.getReleaseDate());  
        return flag;  
    }  
    
} 
