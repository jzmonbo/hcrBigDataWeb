package com.hcrcms.core.web.util;

import java.util.Comparator;  

import com.hcrcms.cms.entity.main.Content;

public class SortClass implements Comparator{  
    public int compare(Object arg0,Object arg1){  
        Content content0 = (Content)arg0;  
        Content content1 = (Content)arg1;  
        int flag = content0.getSortDate().compareTo(content1.getSortDate());  
        return flag;  
    }  
    
} 
