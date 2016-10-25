package com.hcrcms.core.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.hcrcms.cms.entity.main.Content;

/**
 * 用来克隆文章类Content
 * @author jingrun.zhang
 *
 */
public class CloneUtil {

	@SuppressWarnings("unchecked")  
    public static <T extends Serializable> T clone(T obj){  
        T cloneObj = null;  
        try {  
            //写入字节流  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            ObjectOutputStream obs = new ObjectOutputStream(out);  
            obs.writeObject(obj);  
            obs.close();  
              
            //分配内存，写入原始对象，生成新对象  
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());  
            ObjectInputStream ois = new ObjectInputStream(ios);  
            //返回生成的新对象  
            cloneObj = (T) ois.readObject();  
            ois.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return cloneObj;  
    }  
	
	public static Content cloneContent(Content content){
		Content bean1 = new Content();
		bean1.setId(content.getId());
		bean1.setChannel(content.getChannel());
		bean1.setType(content.getType());
		bean1.setModel(content.getModel());
		bean1.setSite(content.getSite());
		bean1.setSortDate(content.getSortDate());
		bean1.setTopLevel(content.getTopLevel());
		bean1.setHasTitleImg(content.getHasTitleImg());
		bean1.setRecommend(content.getRecommend());
		bean1.setStatus(content.getStatus());
		bean1.setViewsDay(content.getViewDay());
		bean1.setCommentsDay(content.getCommentsDay());
		bean1.setDownloadsDay(content.getDownloadsDay());
		bean1.setUpsDay(content.getUpsDay());
		bean1.setScore(content.getScore());
		bean1.setSportDate(content.getSportDate());
		bean1.setRecommandView(content.getRecommandView());
		bean1.setIsPushBaidu(content.getIsPushBaidu());
		bean1.setIsExpert(content.getIsExpert());
		bean1.setRecommendInterview(content.getRecommendInterview());
		bean1.setMySport(content.getMySport());
		bean1.setReleaseMoreCategory(content.getReleaseMoreCategory());
		bean1.setReleaseMoreId(content.getReleaseMoreId());
		bean1.setUser(content.getUser());
		bean1.setContentExt(content.getContentExt());
		bean1.setContentCount(content.getContentCount());
		bean1.setContentTxt(content.getContentTxt());
		return bean1;
	}
	
}
