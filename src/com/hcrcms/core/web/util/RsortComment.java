package com.hcrcms.core.web.util;

import java.util.Comparator;

import com.hcrcms.cms.entity.assist.CmsCommentExt;

/**
 * 评论回复排序
 * @author jingrun.zhang
 *
 */
public class RsortComment implements Comparator{  
    public int compare(Object arg0,Object arg1){  
        CmsCommentExt ext0 = (CmsCommentExt)arg0;  
        CmsCommentExt ext1 = (CmsCommentExt)arg1;  
        int flag = ext0.getRsort().compareTo(ext1.getRsort());  
        return flag;  
    }  
    
} 
