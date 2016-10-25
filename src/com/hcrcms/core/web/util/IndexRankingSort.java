package com.hcrcms.core.web.util;

import java.util.Comparator;

import com.hcrcms.cms.entity.assist.CmsSearchEngine;

public class IndexRankingSort implements Comparator{  
    public int compare(Object arg0,Object arg1){  
    	CmsSearchEngine se0 = (CmsSearchEngine)arg0;  
    	CmsSearchEngine se1 = (CmsSearchEngine)arg1;  
        int flag = new Double(se0.getTotal()).compareTo(new Double(se1.getTotal()));  
        return flag;  
    }  
    
} 
