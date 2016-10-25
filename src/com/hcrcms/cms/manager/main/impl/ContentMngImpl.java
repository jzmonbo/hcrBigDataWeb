package com.hcrcms.cms.manager.main.impl;

import static com.hcrcms.cms.entity.main.ContentCheck.DRAFT;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import magick.ImageInfo;
import magick.MagickImage;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.hcrcms.cms.action.CommonUpload;
import com.hcrcms.cms.dao.main.ContentDao;
import com.hcrcms.cms.entity.assist.CmsFile;
import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.Channel.AfterCheckEnum;
import com.hcrcms.cms.entity.main.CmsTopic;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.Content.ContentStatus;
import com.hcrcms.cms.entity.main.CmsSpecialTopic;
import com.hcrcms.cms.entity.main.CmsSpecialTopicContent;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.cms.entity.main.ContentCount;
import com.hcrcms.cms.entity.main.ContentExt;
import com.hcrcms.cms.entity.main.ContentTag;
import com.hcrcms.cms.entity.main.ContentTxt;
import com.hcrcms.cms.entity.main.ContentType;
import com.hcrcms.cms.manager.assist.CmsCommentMng;
import com.hcrcms.cms.manager.assist.CmsFileMng;
import com.hcrcms.cms.manager.main.ChannelMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicContentMng;
import com.hcrcms.cms.manager.main.CmsSpecialTopicMng;
import com.hcrcms.cms.manager.main.CmsTopicMng;
import com.hcrcms.cms.manager.main.ContentCheckMng;
import com.hcrcms.cms.manager.main.ContentCountMng;
import com.hcrcms.cms.manager.main.ContentExtMng;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.cms.manager.main.ContentTagMng;
import com.hcrcms.cms.manager.main.ContentTxtMng;
import com.hcrcms.cms.manager.main.ContentTypeMng;
import com.hcrcms.cms.manager.main.ExpertMng;
import com.hcrcms.cms.service.ChannelDeleteChecker;
import com.hcrcms.cms.service.ContentListener;
import com.hcrcms.cms.staticpage.StaticPageSvc;
import com.hcrcms.cms.staticpage.exception.ContentNotCheckedException;
import com.hcrcms.cms.staticpage.exception.GeneratedZeroStaticPageException;
import com.hcrcms.cms.staticpage.exception.StaticPageNotOpenException;
import com.hcrcms.cms.staticpage.exception.TemplateNotFoundException;
import com.hcrcms.cms.staticpage.exception.TemplateParseException;
import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.common.image.ImageUtils;
import com.hcrcms.common.image.ImageUtils.Position;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.springmvc.RealPathResolver;
import com.hcrcms.core.entity.CmsGroup;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUser;
import com.hcrcms.core.entity.CmsUserSite;
import com.hcrcms.core.entity.MarkConfig;
import com.hcrcms.core.manager.CmsGroupMng;
import com.hcrcms.core.manager.CmsUserMng;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import freemarker.template.TemplateException;

@Service
@Transactional
public class ContentMngImpl extends CommonUpload implements ContentMng, ChannelDeleteChecker {
	@Transactional(readOnly = true)
	public Pagination getPageByRight(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId,Integer userId, int orderBy, int pageNo,
			int pageSize) {
		CmsUser user = cmsUserMng.findById(userId);
		CmsUserSite us = user.getUserSite(siteId);
		Pagination p;
		boolean allChannel = us.getAllChannel();
		boolean selfData = user.getSelfAdmin();
		if (allChannel && selfData) {
			// 拥有所有栏目权限，只能管理自己的数据
			p = dao.getPageBySelf(title, typeId, inputUserId, topLevel,
					recommend, status, checkStep, siteId, channelId, userId,
					orderBy, pageNo, pageSize);
		} else if (allChannel && !selfData) {
			// 拥有所有栏目权限，能够管理不属于自己的数据
			p = dao.getPage(title, typeId,currUserId, inputUserId, topLevel, recommend,
					status, checkStep, siteId,null,channelId,orderBy, pageNo,
					pageSize);
		} else {
			p = dao.getPageByRight(title, typeId, currUserId,inputUserId, topLevel,
					recommend, status, checkStep, siteId, channelId, userId,
					selfData, orderBy, pageNo, pageSize);
		}
		return p;
	}
	
	public Pagination getPageBySite(String title, Integer typeId,Integer inputUserId,boolean topLevel,
			boolean recommend,ContentStatus status, Integer siteId,int orderBy, int pageNo,int pageSize){
		return dao.getPage(title, typeId, null, inputUserId, topLevel, recommend, status, null, siteId, null, null, orderBy, pageNo, pageSize);
	}

	public Pagination getPageForMember(String title, Integer channelId,Integer siteId,Integer modelId, Integer memberId, int pageNo, int pageSize) {
		return dao.getPage(title, null,memberId,memberId, false, false,ContentStatus.all, null, siteId,modelId,  channelId, 0, pageNo,pageSize);
	}
	
	@Transactional(readOnly = true)
	public Content getSide(Integer id, Integer siteId, Integer channelId,
			boolean next) {
		return dao.getSide(id, siteId, channelId, next, true);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByIdsForTag(Integer[] ids, int orderBy) {
		if (ids.length == 1) {
			Content content = findById(ids[0]);
			List<Content> list;
			if (content != null) {
				list = new ArrayList<Content>(1);
				list.add(content);
			} else {
				list = new ArrayList<Content>(0);
			}
			return list;
		} else {
			return dao.getListByIdsForTag(ids, orderBy);
		}
	}

	@Transactional(readOnly = true)
	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int pageNo, int pageSize) {
		return dao.getPageBySiteIdsForTag(siteIds, typeIds, titleImg,
				recommend, title, attr,orderBy, pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, Integer first, Integer count) {
		return dao.getListBySiteIdsForTag(siteIds, typeIds, titleImg,
				recommend, title,attr, orderBy, first, count);
	}

	@Transactional(readOnly = true)
	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,Map<String,String[]>attr, int orderBy, int option, int pageNo, int pageSize,String isPhone) {
		return dao.getPageByChannelIdsForTag(channelIds, typeIds, titleImg,
				recommend, title,attr, orderBy, option, pageNo, pageSize,isPhone);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int option,Integer first, Integer count,String isPhone) {
		return dao.getListByChannelIdsForTag(channelIds, typeIds, titleImg,
				recommend, title,attr, orderBy, option,first, count,isPhone);
	}

	@Transactional(readOnly = true)
	public Pagination getPageByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,Map<String,String[]>attr, int orderBy, int pageNo,
			int pageSize) {
		return dao.getPageByChannelPathsForTag(paths, siteIds, typeIds,
				titleImg, recommend, title,attr, orderBy, pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,Map<String,String[]>attr, int orderBy, Integer first,
			Integer count) {
		return dao.getListByChannelPathsForTag(paths, siteIds, typeIds,
				titleImg, recommend, title,attr, orderBy, first, count);
	}

	@Transactional(readOnly = true)
	public Pagination getPageByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			int pageNo, int pageSize) {
		return dao.getPageByTopicIdForTag(topicId, siteIds, channelIds,
				typeIds, titleImg, recommend, title,attr, orderBy, pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, Map<String,String[]>attr,int orderBy,
			Integer first, Integer count) {
		return dao.getListByTopicIdForTag(topicId, siteIds, channelIds,
				typeIds, titleImg, recommend, title,attr, orderBy, first, count);
	}

	@Transactional(readOnly = true)
	public Pagination getPageByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize) {
		return dao.getPageByTagIdsForTag(tagIds, siteIds, channelIds, typeIds,
				excludeId, titleImg, recommend, title, attr,orderBy, pageNo,
				pageSize);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, Integer first, Integer count) {
		return dao.getListByTagIdsForTag(tagIds, siteIds, channelIds, typeIds,
				excludeId, titleImg, recommend, title,attr, orderBy, first, count);
	}

	@Transactional(readOnly = true)
	public List<Content> getListByTagIdsForTag(String[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, Map<String,String[]>attr,int orderBy, Integer first, Integer count) {
		return dao.getListByTagIdsForTag(tagIds, siteIds, channelIds, typeIds,
				excludeId, titleImg, recommend, title,attr, orderBy, first, count);
	}
	
	@Transactional(readOnly = true)
	public Content findById(Integer id) {
		Content entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public Content save(Content bean, ContentExt ext, ContentTxt txt,
			Integer[] channelIds, Integer[] topicIds, Integer[] viewGroupIds,
			String[] tagArr, String[] attachmentPaths,
			String[] attachmentNames, String[] attachmentFilenames,
			String[] picPaths, String[] picDescs, Integer channelId,
			Integer typeId, Boolean draft,Boolean contribute, CmsUser user, boolean forMember,Integer meetingId,Integer siteLocation,Integer topicType) {
		saveContent(bean, ext, txt, channelId, typeId, draft,contribute,user, forMember);
		// 保存副栏目
		if (channelIds != null && channelIds.length > 0) {
			for (Integer cid : channelIds) {
				bean.addToChannels(channelMng.findById(cid));
			}
		}
		// 主栏目也作为副栏目一并保存，方便查询，提高效率。
		bean.addToChannels(channelMng.findById(channelId));
		// 保存专题
		if (topicIds != null && topicIds.length > 0) {
			for (Integer tid : topicIds) {
				bean.addToTopics(cmsTopicMng.findById(tid));
			}
		}
		// 保存浏览会员组
		if (viewGroupIds != null && viewGroupIds.length > 0) {
			for (Integer gid : viewGroupIds) {
				bean.addToGroups(cmsGroupMng.findById(gid));
			}
		}
		// 保存标签
		List<ContentTag> tags = contentTagMng.saveTags(tagArr);
		bean.setTags(tags);
		// 保存附件
		if (attachmentPaths != null && attachmentPaths.length > 0) {
			for (int i = 0, len = attachmentPaths.length; i < len; i++) {
				if (!StringUtils.isBlank(attachmentPaths[i])) {
					bean.addToAttachmemts(attachmentPaths[i],
							attachmentNames[i], attachmentFilenames[i]);
				}
			}
		}
		// 保存图片集
		if (picPaths != null && picPaths.length > 0) {
			for (int i = 0, len = picPaths.length; i < len; i++) {
				if (!StringUtils.isBlank(picPaths[i])) {
					bean.addToPictures(picPaths[i], picDescs[i]);
				}
			}
		}
		// 执行监听器
		afterSave(bean);
		//查看专题值
		if (meetingId != -1 && siteLocation != -1){
			CmsSpecialTopic specialTopic = cmsSpecialTopicMng.findById(meetingId);
			if (specialTopic != null){
				CmsSpecialTopicContent content = new CmsSpecialTopicContent();
				String meetingLocaltion = "";
				if (siteLocation == 1){
					meetingLocaltion = specialTopic.getMainPlace();
					content.setMainPlace(meetingLocaltion);
				}else{
					String[] subplaces = specialTopic.getSubPlace().split(",");
					meetingLocaltion = subplaces[siteLocation-2];
					content.setSubPlace(meetingLocaltion);
				}
				if (ext.getReleaseDate() == null){
					content.setReleaseDate(new Timestamp(System.currentTimeMillis()));
				}else{
					content.setReleaseDate(ext.getReleaseDate());
				}
				
				//生成水印图片
				String contentImg = ext.getContentImg();
				String watermarkerImg = generateWatermarker(contentImg);
				content.setWatermarkerImg(watermarkerImg);
				
				content.setTitle(ext.getTitle());
				content.setShortTitle(ext.getShortTitle());
				content.setAuthor(ext.getAuthor());
				content.setOrigin(ext.getOrigin());
				content.setOriginUrl(ext.getOriginUrl());
				content.setDescription(ext.getDescription());
				content.setMediaPath(ext.getMediaPath());
				content.setMediaType(ext.getMediaType());
				content.setTitleColor(ext.getTitleColor());
				content.setTitleImg(ext.getTitleImg());
				content.setContentImg(ext.getContentImg());
				content.setTypeImg(ext.getTypeImg());
				content.setLink(ext.getLink());
				content.setTplContent(ext.getTplContent());
				if (ext.getNeedRegenerate() == null){
					content.setNeedRegenerate(0);
				}else{
					content.setNeedRegenerate(ext.getNeedRegenerate()?1:0);
				}
				content.setBigPictureImg(ext.getBigPictureImg());
				content.setSiteLocation(siteLocation);
				content.setIsBold(0);
				content.setTopicType(topicType);
				content.setSpecialTopic(specialTopic);
				content.setContent(bean);
				cmsSpecialTopicContentMng.save(content, null);
			}
		}
		return bean;
	}
	
	/**
	 * 生成水印图片
	 */
	public String generateWatermarker(String contentImg){
		String waterPath = "";
		try {   
			/**  
			* 下面代码的"D:\\1.jpg"，是我把商品图片(需要被加水印的图片)  
			* 保存在D盘，并起名为1.jpg，这里您根据您的图片的实际位置来输  
			* 入正确的文件路径。  
			*/   
			String realpath = realPathResolver.get(contentImg);
			String markpath = realPathResolver.get("/r/cms/www/watermarkContent.png");
			String hechengpath = "";
			if (realpath.indexOf("\\") > -1){
				hechengpath = realpath.substring(0,realpath.lastIndexOf("\\")+1) + "w" + realpath.substring(realpath.lastIndexOf("\\")+1);
				waterPath = contentImg.substring(0,contentImg.lastIndexOf("/")+1) + "w" + contentImg.substring(contentImg.lastIndexOf("/")+1);
			}else{
				hechengpath = realpath.substring(0,realpath.lastIndexOf("/")+1) + "w" + realpath.substring(realpath.lastIndexOf("/")+1);
				waterPath = contentImg.substring(0,contentImg.lastIndexOf("/")+1) + "w" + contentImg.substring(contentImg.lastIndexOf("/")+1);
			}
			//System.out.println("realpath [" + realpath + "]");
			//System.out.println("hechengpath [" + hechengpath + "]");
			//System.out.println("waterPath [" + waterPath + "]");
	        File formerFile = new File(realpath);
	        Image formerImage = ImageIO.read(formerFile);   
	        //以下2行代码分别获得图片的宽(width)和高(height)   
	        int width = formerImage.getWidth(null);   
	        int height = formerImage.getHeight(null);   
	        System.out.println("原始图片的宽为："+width+"\n原始图片的高为："+height);   
            BufferedImage image = new BufferedImage(width, height,   
                    BufferedImage.TYPE_INT_RGB);   
            Graphics g = image.createGraphics();   
            g.drawImage(formerImage, 0, 0, width, height, null);   
	  
	        /**  
			* 下面代码的"D:\\sy.gif"，是我把水印图片保存在D盘，  
			* 并起名为sy.gif，这里您根据您的图片的实际位置来输  
			* 入正确的文件路径。  
			*/    
	        File waterMarkFile = new File(markpath);   
	        Image waterMarkImage = ImageIO.read(waterMarkFile);   
	        int widthWMI = waterMarkImage.getWidth(null);   
	        int heightWMI = waterMarkImage.getHeight(null);   
	        /**  
	         * 以下2行代码的x，y分别表示水印图片在原始图片的位置。  
	         * x,y为坐标。width，height为商品图片的宽和高。  
	         * width * 0.5 表示水印图片的水平位置覆盖在商品图片  
	         * 水平位置的正中间。height * 0.5 表示垂直位置。  
	         * 最终无论商品图片的宽高是多少，水印图片都会显示在  
	         * 商品图片的正中间。  
	         * 您可以根据您的需求，更改0.5这个数值，达到您想要的效果。  
	         * 这里我说的商品图片就是要被水印覆盖的图片。  
	         */   
	        int x = (int)(width * 0.4); //"0.5"小数越大，水印越向左移动。   
	        int y = (int)(height * 0.3); //"0.5"小数越大，水印越向上移动。   
	        g.drawImage(waterMarkImage, width - widthWMI - x, height - heightWMI - y, widthWMI,   
	        heightWMI, null);   
	          
	        /**  
	         * 输出被加上水印的图片，也就是最终的效果。  
	         * 注意！下面代码的"D:\\1.jpg"是最后输出  
	         * 的文件，如果跟你原始文件的路径和名字相同  
	         * 的话，那么会覆盖掉原始文件。  
	         * 如：我的原始文件位于"D:\\1.jpg"，而下  
	         * 面的代码运行之后，我的原始文件就会丢失被  
	         * 覆盖掉。  
	         * 您可以根据您的需要把加上水印后的图片放到  
	         * 您指定的文件路径。  
	         */   
	        g.dispose();   
	        FileOutputStream out = new FileOutputStream(hechengpath);   
	        //下面代码将被加上水印的图片转换为JPEG、JPG文件   
	        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);   
	        encoder.encode(image);   
	        out.close();   
	        System.out.println("水印已经添加成功！");   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	    }
		return waterPath;
	}
	
	//导入word执行
	public Content save(Content bean, ContentExt ext, ContentTxt txt,
			Integer channelId,Integer typeId, Boolean draft, CmsUser user, boolean forMember){
		saveContent(bean, ext, txt, channelId, typeId, draft,false, user, forMember);
		// 执行监听器
		afterSave(bean);
		return bean;
	}
	
	private Content saveContent(Content bean, ContentExt ext, ContentTxt txt,
			Integer channelId,Integer typeId, Boolean draft,Boolean contribute,CmsUser user, boolean forMember){
		Channel channel = channelMng.findById(channelId);
		bean.setChannel(channel);
		//重新设置文章MODEL
		if (channelId != 84){
			bean.setModel(bean.getChannel().getModel());
		}
		
		//bean.setType(contentTypeMng.findById(typeId));
		//************************  屏蔽jc_content_type  START  *********************************
		ContentType ct = new ContentType();
		ct.setId(2);
		bean.setType(ct);
		//************************** 屏蔽jc_content_type END    *******************************
		bean.setUser(user);
		/*Byte userStep;
		if (forMember) {
			// 会员的审核级别按0处理
			userStep = 0;
		} else {
			CmsSite site = bean.getSite();
			userStep = user.getCheckStep(site.getId());
		}
		// 流程处理
		if(contribute!=null&&contribute){
			bean.setStatus(ContentCheck.CONTRIBUTE);
		}else if (draft != null && draft) {
			// 草稿
			bean.setStatus(ContentCheck.DRAFT);
		} else {
			if (userStep >= bean.getChannel().getFinalStepExtends()) {
				bean.setStatus(ContentCheck.CHECKED);
			} else {
				bean.setStatus(ContentCheck.CHECKING);
			}
		}*/
		//这里发布文章不需要审核
		Byte userStep = 3;
		//bean.setStatus(ContentCheck.CHECKED);
		// 是否有标题图
		bean.setHasTitleImg(!StringUtils.isBlank(ext.getTitleImg()));
		bean.init();
		// 执行监听器
		preSave(bean);
		//如果是首页大图,先把相关数据库里大图修改为不是大图
		if (bean.getIsBigPicture() != null){
			if (bean.getIsBigPicture() == 1){
				updateBigPicture(channelId,bean.getId(),bean.getIsBigPicture(),bean.getArticle(),bean.getClassify());
			}
			//如果该文章需要置顶,先把其它置顶文章设置不置顶
			if (bean.getIsPictureTop() == 2){
				updatePictureTop(0,null);
			}
			//如果是文章推荐为指数大图,先把相关数据库里指数大图改为不是大图
			if (bean.getIsBigPicture() == 2){
				updateBigPictureIndexRank(channelId,bean.getId(),bean.getIsBigPicture(),bean.getArticle(),bean.getClassify());
			}
		}
		
		contentTxtMng.save(txt, bean);
		dao.save(bean);
		contentExtMng.save(ext, bean);
		ContentCheck check = new ContentCheck();
		check.setCheckStep(userStep);
		contentCheckMng.save(check, bean);
		contentCountMng.save(new ContentCount(), bean);
		return bean;
	}

	@Transactional(readOnly = false,propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public Content update(Content bean, ContentExt ext, ContentTxt txt,
			String[] tagArr, Integer[] channelIds, Integer[] topicIds,
			Integer[] viewGroupIds, String[] attachmentPaths,
			String[] attachmentNames, String[] attachmentFilenames,
			String[] picPaths, String[] picDescs, Map<String, String> attr,
			Integer channelId, Integer typeId, Boolean draft, CmsUser user,
			boolean forMember,String queryStatus,Integer queryTypeId,CmsSpecialTopicContent topicContent,Integer meetingId,Integer siteLocation,
			Integer specialTopicContentId,String subPlaceStr,Boolean queryTopLevel,Boolean queryRecommend,Integer queryOrderBy) {
		try{
			//如果文章需要置顶 ,需要把其它文章不置顶
			if (bean.getIsPictureTop() != null && bean.getIsPictureTop() == 2){
				updatePictureTop(0,bean.getId());
			}
			//如果是首页大图,先把相关数据库里大图修改为不是大图
			if (bean.getIsBigPicture() != null && bean.getIsBigPicture() == 1){
				updateBigPicture(channelId,bean.getId(),bean.getIsBigPicture(),bean.getArticle(),bean.getClassify());
			}
			//如果是指数排行大图,先把相关数据库里大图修改为不日大图
			if (bean.getIsBigPicture() != null && bean.getIsBigPicture() == 2){
				updateBigPictureIndexRank(channelId,bean.getId(),bean.getIsBigPicture(),bean.getArticle(),bean.getClassify());
			}
			//如果专题里专访要推荐到首页,需要把专题里其它专访的首页推荐置为0
			if (bean.getRecommandView() != null){
				if (bean.getRecommandView() == 1){
					ext.setBigPictureImg(bean.getSpecBigPicture());
					updateSpecialRecommandView(0);
				}
			}
			
			Content entity = findById(bean.getId());
			//判断是否修改为首页大图(只限于文章)
			if (channelId == 70){
				if (!"0".equals(bean.getBigPictureLocal()) && bean.getBigPictureLocal().length() > 0){
					entity.setIsBigPicture(1);
					entity.setArticle("首页大图");
					entity.setClassify(bean.getBigPictureLocal());
				}else{
					entity.setIsBigPicture(0);
					entity.setArticle(null);
					entity.setClassify(null);
					entity.getContentExt().setBigPictureImg(null);
				}
				if (ext.getSendUrl() != null && ext.getSendUrl().length() > 0){
					entity.getContentExt().setSendUrl(ext.getSendUrl());
				}else{
					entity.getContentExt().setSendUrl("");
				}
			}
			// 执行监听器
			List<Map<String, Object>> mapList = preChange(entity);
			// 更新主表
			Updater<Content> updater = new Updater<Content>(bean);
			bean = dao.updateByUpdater(updater);
			// 审核更新处理，如果站点设置为审核退回，且当前文章审核级别大于管理员审核级别，则将文章审核级别修改成管理员的审核级别。
			Byte userStep;
			if (forMember) {
				// 会员的审核级别按0处理
				userStep = 0;
			} else {
				CmsSite site = bean.getSite();
				userStep = user.getCheckStep(site.getId());
			}
			AfterCheckEnum after = bean.getChannel().getAfterCheckEnum();
			if (after == AfterCheckEnum.BACK_UPDATE
					&& bean.getCheckStep() > userStep) {
				bean.getContentCheck().setCheckStep(userStep);
				if (bean.getCheckStep() >= bean.getChannel().getFinalStepExtends()) {
					bean.setStatus(ContentCheck.CHECKED);
				} else {
					bean.setStatus(ContentCheck.CHECKING);
				}
			}
			//修改后退回
			if (after == AfterCheckEnum.BACK_UPDATE) {
				--userStep;
				if(userStep<0){
					userStep=0;
				}
				reject(bean.getId(), user, userStep,"");
			}
			// 草稿
			if (draft != null) {
				if (draft) {
					bean.setStatus(DRAFT);
				} else {
					if (bean.getStatus() == DRAFT) {
						if (bean.getCheckStep() >= bean.getChannel()
								.getFinalStepExtends()) {
							bean.setStatus(ContentCheck.CHECKED);
						} else {
							bean.setStatus(ContentCheck.CHECKING);
						}
					}
				}
			}
			// 是否有标题图
			bean.setHasTitleImg(!StringUtils.isBlank(ext.getTitleImg()));
			// 更新栏目
			if (channelId != null) {
				bean.setChannel(channelMng.findById(channelId));
			}
			// 更新类型
			if (typeId != null) {
				bean.setType(contentTypeMng.findById(typeId));
			}
			// 更新扩展表
			contentExtMng.update(ext);
			// 更新文本表
			contentTxtMng.update(txt, bean);
			// 更新属性表
			if (attr != null) {
				Map<String, String> attrOrig = bean.getAttr();
				attrOrig.clear();
				attrOrig.putAll(attr);
			}
			// 更新副栏目表
			Set<Channel> channels = bean.getChannels();
			channels.clear();
			if (channelIds != null && channelIds.length > 0) {
				for (Integer cid : channelIds) {
					channels.add(channelMng.findById(cid));
				}
			}
			channels.add(bean.getChannel());
			// 更新专题表
			Set<CmsTopic> topics = bean.getTopics();
			topics.clear();
			if (topicIds != null && topicIds.length > 0) {
				for (Integer tid : topicIds) {
					topics.add(cmsTopicMng.findById(tid));
				}
			}
			// 更新浏览会员组
			Set<CmsGroup> groups = bean.getViewGroups();
			groups.clear();
			if (viewGroupIds != null && viewGroupIds.length > 0) {
				for (Integer gid : viewGroupIds) {
					groups.add(cmsGroupMng.findById(gid));
				}
			}
			// 更新标签
			contentTagMng.updateTags(bean.getTags(), tagArr);
			// 更新附件
			bean.getAttachments().clear();
			if (attachmentPaths != null && attachmentPaths.length > 0) {
				for (int i = 0, len = attachmentPaths.length; i < len; i++) {
					if (!StringUtils.isBlank(attachmentPaths[i])) {
						bean.addToAttachmemts(attachmentPaths[i],
								attachmentNames[i], attachmentFilenames[i]);
					}
				}
			}
			// 更新图片集
			bean.getPictures().clear();
			if (picPaths != null && picPaths.length > 0) {
				for (int i = 0, len = picPaths.length; i < len; i++) {
					if (!StringUtils.isBlank(picPaths[i])) {
						bean.addToPictures(picPaths[i], picDescs[i]);
					}
				}
			}
			// 执行监听器
			afterChange(bean, mapList);
			//把99999置为空，这里产生莫名的99999问题
			if ("99999".equals(bean.getClassify())){
				bean.setClassify(null);
			}
			//更新专题
			if (bean.getChannel().getId() == 84){
				if (meetingId != null && siteLocation != null && meetingId != -1 && siteLocation != -1){
					CmsSpecialTopic specialTopic = cmsSpecialTopicMng.findById(meetingId);
					String[] places = subPlaceStr.split("\\|");
					if (siteLocation == 1){
						topicContent.setMainPlace(places[0]);
						topicContent.setSubPlace(null);
					}else{
						String[] subs = places[1].split(",");
						topicContent.setMainPlace(null);
						topicContent.setSubPlace(subs[siteLocation-2]);
					}
					topicContent.setSpecialTopic(specialTopic);
					topicContent.setSiteLocation(siteLocation);
					topicContent.setIsBold(0);
					topicContent.setNeedRegenerate(0);
					if (specialTopicContentId != null){
						topicContent.setId(specialTopicContentId);
					}
					cmsSpecialTopicContentMng.update(topicContent,null);
				}else{
					cmsSpecialTopicContentMng.update(topicContent,null);
				}
			}
		}catch(Exception e){
			System.out.println("---------------   update article manage is error :    ---------------------");
			e.printStackTrace();
		}
		
		return bean;
	}
	
	public Content update(Content bean){
		Updater<Content> updater = new Updater<Content>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}
	
	public Content updateByChannelIds(Integer contentId,Integer[]channelIds){
		Content bean=findById(contentId);
		Set<Channel>channels=bean.getChannels();
		channels.clear();
		if (channelIds != null && channelIds.length > 0) {
			for (Integer cid : channelIds) {
				channels.add(channelMng.findById(cid));
			}
		}
		return bean;
	}
	
	public Content addContentToTopics(Integer contentId,Integer[]topicIds){
		Content bean=findById(contentId);
		Set<CmsTopic>topics=bean.getTopics();
		if (topicIds != null && topicIds.length > 0) {
			for (Integer tid : topicIds) {
				topics.add(cmsTopicMng.findById(tid));
			}
		}
		return bean;
	}

	/**
	 * 获取未推送给百度的文章
	 */
	public List<Content> getNoPushBaiduList() {
		return dao.getNoPushBaiduList();
	}

	/**
	 * 更新已经推送给百度文章状态
	 */
	public void updatePushBaiduArticle(String ids) {
		dao.updatePushBaiduArticle(ids);
	}

	/**
	 * 加载更多文章
	 */
	public List<Content> getMoreArticles(int maxId, int minId,String category) {
		return dao.getMoreArticles(maxId,minId,category);
	}

	/**
	 * 移动端
	 * 加载更新文章(上拉和下拉刷新)
	 * Param maxLengthId 页面最大文章ID
	 * Param articleLength 获取文章条数
	 * Param toward 刷新方向(true下拉,false上拉)
	 */
	public List<Content> getMoreArticles(int maxLengthId, int articleLength,
			boolean toward) {
		return dao.getMoreArticles(maxLengthId,articleLength,toward);
	}

	public Content check(Integer id, CmsUser user) {
		Content content = findById(id);
		// 执行监听器
		List<Map<String, Object>> mapList = preChange(content);
		ContentCheck check = content.getContentCheck();
		byte userStep = user.getCheckStep(content.getSite().getId());
		byte contentStep = check.getCheckStep();
		byte finalStep = content.getChannel().getFinalStepExtends();
		// 用户审核级别小于当前审核级别，则不能审核
		if (userStep < contentStep) {
			return content;
		}
		check.setRejected(false);
		// 上级审核，清除退回意见。自我审核不清除退回意见。
		if (userStep > contentStep) {
			check.setCheckOpinion(null);
		}
		check.setCheckStep(userStep);
		// 终审
		if (userStep >= finalStep) {
			content.setStatus(ContentCheck.CHECKED);
			// 终审，清除退回意见
			check.setCheckOpinion(null);
			//终审，设置审核者
			check.setReviewer(user);
			check.setCheckDate(Calendar.getInstance().getTime());
		}
		// 执行监听器
		afterChange(content, mapList);
		return content;
	}

	public Content[] check(Integer[] ids, CmsUser user) {
		Content[] beans = new Content[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = check(ids[i], user);
		}
		return beans;
	}

	public Content reject(Integer id, CmsUser user, Byte step, String opinion) {
		Content content = findById(id);
		// 执行监听器
		List<Map<String, Object>> mapList = preChange(content);
		Integer siteId = content.getSite().getId();
		byte userStep = user.getCheckStep(siteId);
		byte contentStep = content.getCheckStep();
		// 用户审核级别小于当前审核级别，则不能退回
		if (userStep < contentStep) {
			return content;
		}
		ContentCheck check = content.getContentCheck();
		if (!StringUtils.isBlank(opinion)) {
			check.setCheckOpinion(opinion);
		}
		check.setRejected(true);
		// 退回稿件一律为未终审
		content.setStatus(ContentCheck.CHECKING);

		if (step != null) {
			// 指定退回级别，不能大于自身级别
			if (step < userStep) {
				check.setCheckStep(step);
			} else {
				check.setCheckStep(userStep);
			}
		} else {
			// 未指定退回级别
			if (contentStep < userStep) {
				// 文档级别小于用户级别，为审核时退回，文档审核级别不修改。
			} else if (contentStep == userStep) {
				// 文档级别等于用户级别，为退回时退回，文档审核级别减一级。
				check.setCheckStep((byte) (check.getCheckStep() - 1));
			}
		}
		// 执行监听器
		afterChange(content, mapList);
		return content;
	}

	public Content[] reject(Integer[] ids, CmsUser user, Byte step,String opinion) {
		Content[] beans = new Content[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = reject(ids[i], user,step,opinion);
		}
		return beans;
	}

	public Content cycle(Integer id) {
		Content content = findById(id);
		// 执行监听器
		List<Map<String, Object>> mapList = preChange(content);
		content.setStatus(ContentCheck.RECYCLE);
		// 执行监听器
		afterChange(content, mapList);
		return content;
	}

	public Content[] cycle(Integer[] ids) {
		Content[] beans = new Content[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = cycle(ids[i]);
		}
		return beans;
	}

	public Content recycle(Integer id) {
		Content content = findById(id);
		// 执行监听器
		List<Map<String, Object>> mapList = preChange(content);
		byte contentStep = content.getCheckStep();
		byte finalStep = content.getChannel().getFinalStepExtends();
		if (contentStep >= finalStep && !content.getRejected()) {
			content.setStatus(ContentCheck.CHECKED);
		} else {
			content.setStatus(ContentCheck.CHECKING);
		}
		// 执行监听器
		afterChange(content, mapList);
		return content;
	}

	public Content[] recycle(Integer[] ids) {
		Content[] beans = new Content[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = recycle(ids[i]);
		}
		return beans;
	}

	public Content deleteById(Integer id) {
		Content bean = findById(id);
		// 执行监听器
		preDelete(bean);
		// 移除tag
		contentTagMng.removeTags(bean.getTags());
		bean.getTags().clear();
		// 删除评论
		cmsCommentMng.deleteByContentId(id);
		//删除附件记录
		fileMng.deleteByContentId(id);
		bean.clear();
		bean = dao.deleteById(id);
		// 执行监听器
		afterDelete(bean);
		return bean;
	}

	public Content[] deleteByIds(Integer[] ids) {
		Content[] beans = new Content[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}
	
	public Content[] contentStatic(Integer[] ids)
			throws TemplateNotFoundException, TemplateParseException,
			GeneratedZeroStaticPageException, StaticPageNotOpenException, ContentNotCheckedException {
		int count = 0;
		List<Content> list = new ArrayList<Content>();
		for (int i = 0, len = ids.length; i < len; i++) {
			Content content = findById(ids[i]);
			try {
				if (!content.getChannel().getStaticContent()) {
					throw new StaticPageNotOpenException(
							"content.staticNotOpen", count, content.getTitle());
				}
				if(!content.isChecked()){
					throw new ContentNotCheckedException("content.notChecked", count, content.getTitle());
				}
				if (staticPageSvc.content(content)) {
					list.add(content);
					count++;
				}
			} catch (IOException e) {
				throw new TemplateNotFoundException(
						"content.tplContentNotFound", count, content.getTitle());
			} catch (TemplateException e) {
				throw new TemplateParseException("content.tplContentException",
						count, content.getTitle());
			}
		}
		if (count == 0) {
			throw new GeneratedZeroStaticPageException(
					"content.staticGenerated");
		}
		Content[] beans = new Content[count];
		return list.toArray(beans);
	}
	
	public Pagination getPageForCollection(Integer siteId, Integer memberId, int pageNo, int pageSize){
		return dao.getPageForCollection(siteId,memberId,pageNo,pageSize);
	}
	
	public void updateFileByContent(Content bean,Boolean valid){
		Set<CmsFile>files;
		Iterator<CmsFile>it;
		CmsFile tempFile;
		//处理附件
		files=bean.getFiles();
		it=files.iterator();
		while(it.hasNext()){
			tempFile=it.next();
			tempFile.setFileIsvalid(valid);
			fileMng.update(tempFile);
		}
	}
	
	public void updatePictureTop(Integer pictureTop,Integer id){
		dao.updatePictureTop(pictureTop,id);
	}
	
	public void updateBigPicture(Integer channel,Integer contentId, Integer isBigPicture,
			String article,String classify) {
		dao.updateBigPicture(channel,contentId,isBigPicture,article,classify);
	}
	
	public void updateBigPictureIndexRank(Integer channel,Integer contentId, Integer isBigPicture,
			String article,String classify) {
		dao.updateBigPictureIndexRank(channel,contentId,isBigPicture,article,classify);
	}
	
	public String checkForChannelDelete(Integer channelId) {
		int count = dao.countByChannelId(channelId);
		if (count > 0) {
			return "content.error.cannotDeleteChannel";
		} else {
			return null;
		}
	}
	
	public void updateSpecialRecommandView(Integer val){
		dao.updateSpecialRecommandView(val);
	}
	
	public List<Content> getListForGunDong() {
		return dao.getListForGunDong();
	}

	public List<Content> getListForCurrentDay() {
		return dao.getListForCurrentDay();
	}

	@Override
	public void updateBigPicture(Integer channel, Integer isBigPicture,
			String article, String classify) {
		// TODO Auto-generated method stub
		
	}

	private void preSave(Content content) {
		if (listenerList != null) {
			for (ContentListener listener : listenerList) {
				listener.preSave(content);
			}
		}
	}

	private void afterSave(Content content) {
		if (listenerList != null) {
			for (ContentListener listener : listenerList) {
				listener.afterSave(content);
			}
		}
	}

	private List<Map<String, Object>> preChange(Content content) {
		if (listenerList != null) {
			int len = listenerList.size();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
					len);
			for (ContentListener listener : listenerList) {
				list.add(listener.preChange(content));
			}
			return list;
		} else {
			return null;
		}
	}

	private void afterChange(Content content, List<Map<String, Object>> mapList) {
		if (listenerList != null) {
			Assert.notNull(mapList);
			Assert.isTrue(mapList.size() == listenerList.size());
			int len = listenerList.size();
			ContentListener listener;
			for (int i = 0; i < len; i++) {
				listener = listenerList.get(i);
				listener.afterChange(content, mapList.get(i));
			}
		}
	}

	private void preDelete(Content content) {
		if (listenerList != null) {
			for (ContentListener listener : listenerList) {
				listener.preDelete(content);
			}
		}
	}

	private void afterDelete(Content content) {
		if (listenerList != null) {
			for (ContentListener listener : listenerList) {
				listener.afterDelete(content);
			}
		}
	}

	private List<ContentListener> listenerList;

	public void setListenerList(List<ContentListener> listenerList) {
		this.listenerList = listenerList;
	}

	public List<Content> getListForAbout(String extTitle) {
		return dao.getListForAbout(extTitle);
	}

	public List<Content> getZhiShuPaiHangDaTu() {
		return dao.getZhiShuPaiHangDaTu();
	}

	/**
	 * 查询头条新闻
	 */
	public Content findTouTiao() {
		return dao.findTouTiao();
	}

	/**
	 * 查询从昨天15点到今天15点最新10篇文章(用作更新网站右侧大家都在搜)
	 */
	public List<Content> findNewTenXinwen(String beforeDate, String afterDate) {
		return dao.findNewTenXinwen(beforeDate,afterDate);
	}

	/**
	 * 查询人物专访文章
	 */
	public List<Content> findRenWuZhuanFangArticles() {
		return dao.findRenWuZhuanFangArticles();
	}

	/**
	 * 查询学堂文章
	 */
	public List<Content> findXueTangArticles() {
		return dao.findXueTangArticles();
	}

	/**
	 * 根据大数据学堂类型查询
	 */
	public List<Content> findXueTangByTypeArticls(String type) {
		return dao.findXueTangByTypeArticls(type);
	}

	/**
	 * 普通活动列表
	 */
	public List<Content> findPuTongSports() {
		return dao.findPuTongSports();
	}

	/**
	 * 普通活动列表(手机)
	 */
	public List<Content> findPuTongSports(int maxId, int articleLength,String type) {
		return dao.findPuTongSports(maxId,articleLength,type);
	}

	/**
	 * 普通精品活动列表(手机)
	 */
	public List<Content> findPuTongJPSports(int maxId, int articleLength,String type,String jpType) {
		return dao.findPuTongJPSports(maxId,articleLength,type,jpType);
	}

	/**
	 * 我的活动列表
	 */
	public List<Content> findMySport(Integer num) {
		return dao.findMySport(num);
	}

	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType, String startTime,
			String endTime) {
		return dao.findMySportByCategory(jpType,startTime,endTime);
	}

	/**
	 * 精品活动分类列表
	 */
	public List<Content> findMySportByCategory(String jpType) {
		return dao.findMySportByCategory(jpType);
	}

	/**
	 * 精品活动分类列表总览
	 */
	public List<Content> findMySportByCategoryDetail(String jpType,
			String startId, String stepLength) {
		return dao.findMySportByCategoryDetail(jpType,startId,stepLength);
	}

	/**
	 * 查询当前活动列表
	 */
	public List<Content> findCurrentMonthSports(String startTime, String endTime) {
		return dao.findCurrentMonthSports(startTime,endTime);
	}

	/**
	 * 查看最大文章数
	 */
	public int findMaxArticleNum() {
		return dao.findMaxArticleNum();
	}

	/**
	 * 更新文章中更多分类和ID
	 */
	public void updateMoreCategoryAndId(String oid, String releaseMoreCategory,
			String releaseMoreId) {
		dao.updateMoreCategoryAndId(oid,releaseMoreCategory,releaseMoreId);
	}

	private ChannelMng channelMng;
	private ContentExtMng contentExtMng;
	private ContentTxtMng contentTxtMng;
	private ContentTypeMng contentTypeMng;
	private ContentCountMng contentCountMng;
	private ContentCheckMng contentCheckMng;
	private ContentTagMng contentTagMng;
	private CmsGroupMng cmsGroupMng;
	private CmsUserMng cmsUserMng;
	private CmsTopicMng cmsTopicMng;
	private CmsCommentMng cmsCommentMng;
	private ContentDao dao;
	private StaticPageSvc staticPageSvc;
	private CmsFileMng fileMng;
	private ExpertMng expertMsg;
	private CmsSpecialTopicMng cmsSpecialTopicMng;
	private CmsSpecialTopicContentMng cmsSpecialTopicContentMng;

	@Autowired
	public void setChannelMng(ChannelMng channelMng) {
		this.channelMng = channelMng;
	}

	@Autowired
	public void setContentTypeMng(ContentTypeMng contentTypeMng) {
		this.contentTypeMng = contentTypeMng;
	}

	@Autowired
	public void setContentCountMng(ContentCountMng contentCountMng) {
		this.contentCountMng = contentCountMng;
	}

	@Autowired
	public void setContentExtMng(ContentExtMng contentExtMng) {
		this.contentExtMng = contentExtMng;
	}

	@Autowired
	public void setContentTxtMng(ContentTxtMng contentTxtMng) {
		this.contentTxtMng = contentTxtMng;
	}

	@Autowired
	public void setContentCheckMng(ContentCheckMng contentCheckMng) {
		this.contentCheckMng = contentCheckMng;
	}

	@Autowired
	public void setCmsTopicMng(CmsTopicMng cmsTopicMng) {
		this.cmsTopicMng = cmsTopicMng;
	}

	@Autowired
	public void setContentTagMng(ContentTagMng contentTagMng) {
		this.contentTagMng = contentTagMng;
	}

	@Autowired
	public void setCmsGroupMng(CmsGroupMng cmsGroupMng) {
		this.cmsGroupMng = cmsGroupMng;
	}

	@Autowired
	public void setCmsUserMng(CmsUserMng cmsUserMng) {
		this.cmsUserMng = cmsUserMng;
	}

	@Autowired
	public void setCmsCommentMng(CmsCommentMng cmsCommentMng) {
		this.cmsCommentMng = cmsCommentMng;
	}
	
	@Autowired
	public void setFileMng(CmsFileMng fileMng) {
		this.fileMng = fileMng;
	}

	@Autowired
	public void setDao(ContentDao dao) {
		this.dao = dao;
	}

	@Autowired
	public void setStaticPageSvc(StaticPageSvc staticPageSvc) {
		this.staticPageSvc = staticPageSvc;
	}

	@Autowired
	public void setExpertMsg(ExpertMng expertMsg) {
		this.expertMsg = expertMsg;
	}

	@Autowired
	public void setCmsSpecialTopicMng(CmsSpecialTopicMng cmsSpecialTopicMng) {
		this.cmsSpecialTopicMng = cmsSpecialTopicMng;
	}

	@Autowired
	public void setCmsSpecialTopicContentMng(
			CmsSpecialTopicContentMng cmsSpecialTopicContentMng) {
		this.cmsSpecialTopicContentMng = cmsSpecialTopicContentMng;
	}
	@Autowired
	protected RealPathResolver realPathResolver;
}