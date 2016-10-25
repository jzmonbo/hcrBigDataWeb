package com.hcrcms.cms.staticpage;

import static com.hcrcms.common.web.Constants.UTF8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentCheck;
import com.hcrcms.cms.manager.assist.CmsKeywordMng;
import com.hcrcms.common.hibernate3.Finder;
import com.hcrcms.common.hibernate3.HibernateSimpleDao;
import com.hcrcms.common.page.Paginable;
import com.hcrcms.common.page.SimplePage;
import com.hcrcms.common.web.springmvc.RealPathResolver;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.DateUtils;
import com.hcrcms.core.web.util.FrontUtils;
import com.hcrcms.core.web.util.MobileUtils;
import com.hcrcms.core.web.util.URLHelper;
import com.hcrcms.core.web.util.URLHelper.PageInfo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Repository
public class StaticPageDaoImpl extends HibernateSimpleDao implements
		StaticPageDao {
	public int channelStatic(Integer siteId, Integer channelId,
			boolean containChild, Configuration conf, Map<String, Object> data)
			throws IOException, TemplateException {
		Finder finder = Finder.create("select bean from Channel bean");
		if (channelId != null) {
			if (containChild) {
				finder.append(",Channel parent where").append(
						" bean.lft between parent.lft and parent.rgt").append(
						" and parent.site.id=bean.site.id").append(
						" and parent.id=:channelId");
				finder.setParam("channelId", channelId);
			} else {
				finder.append(" where bean.id=:channelId");
				finder.setParam("channelId", channelId);
			}
		} else if (siteId != null) {
			finder.append(" where bean.site.id=:siteId");
			finder.setParam("siteId", siteId);
		}
		Session session = getSession();
		ScrollableResults channels = finder.createQuery(session).setCacheMode(
				CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
		int count = 0;
		CmsSite site;
		Channel c;
		PageInfo info;
		Writer out = null;
		Template tpl;
		String real, filename;
		File f, parent;
		int quantity, totalPage;
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		while (channels.next()) {
			c = (Channel) channels.get(0);
			site = c.getSite();
			FrontUtils.frontData(data, site, null, null, null);
			// 如果是外部链接或者不需要生产静态页，则不生成
			if (!StringUtils.isBlank(c.getLink()) || !c.getStaticChannel()) {
				continue;
			}
			// 没有内容或者有子栏目，则只生成一页
			int childs = childsOfChannel(c.getId());
			if (!c.getModel().getHasContent()) {
				totalPage = 1;
			} else {
				if (c.getListChild()) {
					quantity = childs;
				} else {
					if(!c.getListChild() && childs > 0){
						quantity=contentsOfParentChannel(c.getId());
					}else{
						quantity = contentsOfChannel(c.getId());
					}
				}
				if (quantity <= 0) {
					totalPage = 1;
				} else {
					totalPage = (quantity - 1) / c.getPageSize() + 1;
				}
			}
			for (int i = 1; i <= totalPage; i++) {
				filename = c.getStaticFilename(i);
				real = realPathResolver.get(filename.toString());
				f = new File(real);
				parent = f.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				tpl = conf.getTemplate(c.getTplChannelOrDef());
				String urlStatic = c.getUrlStatic(i);
				info = URLHelper.getPageInfo(filename.substring(filename
						.lastIndexOf("/")), null);
				FrontUtils.frontPageData(i, info.getHref(), info
						.getHrefFormer(), info.getHrefLatter(), data);
				FrontUtils.putLocation(data, urlStatic);
				data.put("channel", c);
				try {
					// FileWriter不能指定编码确实是个问题，只能用这个代替了。
					out = new OutputStreamWriter(new FileOutputStream(f), UTF8);
					tpl.process(data, out);
					log.info("create static file: {}", f.getAbsolutePath());
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
			if (++count % 20 == 0) {
				session.clear();
			}
		}
		return count;
	}

	public void channelStatic(Channel c, boolean firstOnly, Configuration conf,
			Map<String, Object> data) throws IOException, TemplateException {
		// 如果是外部链接或者不需要生产静态页，则不生成
		if (!StringUtils.isBlank(c.getLink()) || !c.getStaticChannel()) {
			return;
		}
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		// 没有内容或者有子栏目，则只生成一页
		int childs = childsOfChannel(c.getId());
		int quantity, totalPage;
		if (firstOnly || !c.getModel().getHasContent()
				|| (!c.getListChild() && childs > 0)) {
			totalPage = 1;
		} else {
			if (c.getListChild()) {
				quantity = childs;
			} else {
				quantity = contentsOfChannel(c.getId());
			}
			if (quantity <= 0) {
				totalPage = 1;
			} else {
				totalPage = (quantity - 1) / c.getPageSize() + 1;
			}
		}
		String real, filename;
		File f, parent;
		PageInfo info;
		Writer out = null;
		Template tpl;
		CmsSite site = c.getSite();
		FrontUtils.frontData(data, site, null, null, null);
		for (int i = 1; i <= totalPage; i++) {
			filename = c.getStaticFilename(i);
			real = realPathResolver.get(filename.toString());
			f = new File(real);
			parent = f.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			tpl = conf.getTemplate(c.getTplChannelOrDef());
			String urlStatic = c.getUrlStatic(i);
			info = URLHelper.getPageInfo(filename.substring(filename
					.lastIndexOf("/")), null);
			FrontUtils.frontPageData(i, info.getHref(), info.getHrefFormer(),
					info.getHrefLatter(), data);
			FrontUtils.putLocation(data, urlStatic);
			data.put("channel", c);
			try {
				// FileWriter不能指定编码确实是个问题，只能用这个代替了。
				out = new OutputStreamWriter(new FileOutputStream(f), UTF8);
				tpl.process(data, out);
				log.info("create static file: {}", f.getAbsolutePath());
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	public int contentsOfChannel(Integer channelId) {
		String hql = "select count(*) from Content bean"
				+ " join bean.channels as channel"
				+ " where channel.id=:channelId and bean.status="
				+ ContentCheck.CHECKED;
		Query query = getSession().createQuery(hql);
		query.setParameter("channelId", channelId);
		return ((Number) query.iterate().next()).intValue();
	}
	
	public int contentsOfParentChannel(Integer channelId) {
		String hql = "select count(*) from Content bean"
				+ " join bean.channel channel,Channel parent"
				+ "  where channel.lft between parent.lft and parent.rgt and channel.site.id=parent.site.id and parent.id=:parentId and bean.status="
				+ ContentCheck.CHECKED;
		Query query = getSession().createQuery(hql);
		query.setParameter("parentId", channelId);
		return ((Number) query.iterate().next()).intValue();
	}

	public int childsOfChannel(Integer channelId) {
		String hql = "select count(*) from Channel bean"
				+ " where bean.parent.id=:channelId";
		Query query = getSession().createQuery(hql);
		query.setParameter("channelId", channelId);
		return ((Number) query.iterate().next()).intValue();
	}

	public int contentStatic(Integer siteId, Integer channelId, Date start,Date end,
			Configuration conf, Map<String, Object> data) throws IOException,
			TemplateException {
		Finder f = Finder.create("select bean from Content bean");
		if (channelId != null) {
			f.append(" join bean.channel node,Channel parent");
			f.append(" where node.lft between parent.lft and parent.rgt");
			f.append(" and parent.id=:channelId");
			f.append(" and node.site.id=parent.site.id");
			f.setParam("channelId", channelId);
		} else if (siteId != null) {
			f.append(" where bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		if (start != null) {
			f.append(" and bean.sortDate>=:start");
			f.setParam("start", start);
		}
		if (end != null){
			f.append(" and bean.sortDate<=:end");
			f.setParam("end",end);
		}
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		f.append(" and bean.channel.id <> 71");
		Session session = getSession();
		ScrollableResults contents = f.createQuery(session).setCacheMode(
				CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
		int count = 0;
		int totalPage;
		String url, real, realMobile="";
		File file, parent, fileMobile;
		Content c;
		Channel chnl;
		CmsSite site;
		PageInfo info;
		Template tpl;
		Template tplMobile = null;
		Writer out = null;
		Writer outMobile = null;
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		while (contents.next()) {
			c = (Content) contents.get(0);
			chnl = c.getChannel();
			// 如果是外部链接或者不生成静态页面，则不生成
			if (!StringUtils.isBlank(c.getLink()) || !chnl.getStaticContent()) {
				continue;
			}
			// 如果不需要生成静态页面，则不生成
			/*
			if(!c.getNeedRegenerate()){
				continue;
			}
			*/
			site = c.getSite();
			tpl = conf.getTemplate(c.getTplContentOrDef());
			if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
					|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
				tplMobile = conf.getTemplate(MobileUtils.getMobilePath(c.getTplContentOrDef()));
			}
			FrontUtils.frontData(data, site, null, null, null);
			data.put("content", c);
			data.put("channel", c.getChannel());
			data.put("articlePtime",DateUtils.getTimeZone(c.getSortDate()));
			totalPage = c.getPageCount();
			for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
				String txt = c.getTxtByNo(pageNo);
				// 内容加上关键字
				txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
				Paginable pagination = new SimplePage(pageNo, 1, c
						.getPageCount());
				data.put("pagination", pagination);
				url = c.getUrlStatic(pageNo);
				info = URLHelper.getPageInfo(url
						.substring(url.lastIndexOf("/")), null);
				FrontUtils.putLocation(data, url);
				FrontUtils.frontPageData(pageNo, info.getHref(), info
						.getHrefFormer(), info.getHrefLatter(), data);
				data.put("title", c.getTitleByNo(pageNo));
				data.put("txt", txt);
				data.put("pic", c.getPictureByNo(pageNo));
				if (pageNo == 1) {
					real = realPathResolver.get(c.getStaticFilename(pageNo));										//PC端地址
					if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
							|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
						realMobile = realPathResolver.get(MobileUtils.getMobileStaticPath(c.getStaticFilename(pageNo)));		//移动端地址
					}
					
					file = new File(real);																			
					parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
							|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
						fileMobile = new File(realMobile);
						parent = fileMobile.getParentFile();
						if (!parent.exists()) {
							parent.mkdirs();
						}
					}
				} else {
					real = realPathResolver.get(c.getStaticFilename(pageNo));										//PC端地址
					file = new File(real);
					if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
							|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
						realMobile = realPathResolver.get(MobileUtils.getMobileStaticPath(c.getStaticFilename(pageNo)));		//移动端地址
						fileMobile = new File(realMobile);
					}
				}
				try {
					// FileWriter不能指定编码确实是个问题，只能用这个代替了。
					out = new OutputStreamWriter(new FileOutputStream(file),UTF8);
					tpl.process(data, out);
					if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
							|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
						String contentTxt = c.getTxt();
						String contentTxt1 = c.getTxt1();
						if (c.getTxt() != null){
							//修改手机端图片尺寸
							c.getContentTxt().setTxt(MobileUtils.findPictureWidthHeight(c.getTxt()));
							//修改手机端腾讯视频尺寸
							c.getContentTxt().setTxt(MobileUtils.findTenXunVideoSize(c.getTxt()));
						}
						if (c.getTxt1() != null){
							//修改手机端图片尺寸
							c.getContentTxt().setTxt1(MobileUtils.findPictureWidthHeight(c.getTxt1()));
							//修改手机端腾讯视频尺寸
							c.getContentTxt().setTxt1(MobileUtils.findTenXunVideoSize(c.getTxt1()));
						}
						data.put("content", c);
						data.put("channel", c.getChannel());
						outMobile = new OutputStreamWriter(new FileOutputStream(realMobile),UTF8);
						tplMobile.process(data,outMobile);
						c.getContentTxt().setTxt(contentTxt);
						c.getContentTxt().setTxt1(contentTxt1);
					}
					log.info("create static file: {}", file.getAbsolutePath());
				} finally {
					if (out != null) {
						out.close();
					}
					if (outMobile != null){
						outMobile.close();
					}
				}
			}
			c.setNeedRegenerate(false);
			if (++count % 20 == 0) {
				session.flush();
				session.clear();
			}
		}
		return count;
	}
	
	public boolean contentStatic(Content c, Configuration conf,
			Map<String, Object> data) throws IOException, TemplateException {
		// 如果是外部链接或者不生成静态页面，则不生成
		Channel chnl = c.getChannel();
		if (!StringUtils.isBlank(c.getLink()) || !chnl.getStaticContent()) {
			return false;
		}
		// 如果不需要生成静态页面，则不生成
		/* 是否需要生成静态页这里判断过于简单话，模板变换 站点名称等参数变换均需重新生成
		if(!c.getNeedRegenerate()){
			return false;
		}
		*/
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		int totalPage;
		String url, real,realMobile;
		File file, parent,fileMobile;
		CmsSite site;
		PageInfo info;
		Template tpl;
		Template tplMobile = null;
		Writer out = null;
		Writer outMobile = null;
		site = c.getSite();
		//tpl = conf.getTemplate(c.getTplContentOrDef());
		
		if (ChannelCacheUtils.staticPageMap.get(c.getChannel().getId()) != null){
			tpl = conf.getTemplate(ChannelCacheUtils.staticPageMap.get(c.getChannel().getId()));
			if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
					|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
				tplMobile = conf.getTemplate(MobileUtils.getMobilePath(ChannelCacheUtils.staticPageMap.get(c.getChannel().getId())));
			}
		}else{
			tpl = conf.getTemplate(c.getTplContentOrDef());
			if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
					|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
				tplMobile = conf.getTemplate(MobileUtils.getMobilePath(ChannelCacheUtils.staticPageMap.get(c.getChannel().getId())));
			}
		}
		
		FrontUtils.frontData(data, site, null, null, null);
		data.put("content", c);
		data.put("channel", chnl);
		data.put("articlePtime",DateUtils.getTimeZone(c.getSortDate()));
		totalPage = c.getPageCount();
		for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
			String txt = c.getTxtByNo(pageNo);
			// 内容加上关键字
			txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
			Paginable pagination = new SimplePage(pageNo, 1, c.getPageCount());
			data.put("pagination", pagination);
			url = c.getUrlStatic(pageNo);
			info = URLHelper.getPageInfo(url.substring(url.lastIndexOf("/")),
					null);
			FrontUtils.putLocation(data, url);
			FrontUtils.frontPageData(pageNo, info.getHref(), info
					.getHrefFormer(), info.getHrefLatter(), data);
			data.put("title", c.getTitleByNo(pageNo));
			data.put("txt", txt);
			data.put("pic", c.getPictureByNo(pageNo));
			real = realPathResolver.get(c.getStaticFilename(pageNo));												//PC端地址
			realMobile = realPathResolver.get(MobileUtils.getMobileStaticPath(c.getStaticFilename(pageNo)));		//移动端地址
			file = new File(real);
			if (pageNo == 1) {
				parent = file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
			}
			fileMobile = new File(realMobile);
			parent = fileMobile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			try {
				// FileWriter不能指定编码确实是个问题，只能用这个代替了。
				out = new OutputStreamWriter(new FileOutputStream(file), UTF8);
				tpl.process(data, out);
				//outMobile = new OutputStreamWriter(new FileOutputStream(realMobile),UTF8);
				//tplMobile.process(data,outMobile);
				if (c.getChannel().getId() == 70 || c.getChannel().getId() == 74 || c.getChannel().getId() == 75 || c.getChannel().getId() == 76
						|| c.getChannel().getId() == 84 || c.getChannel().getId() == 79){
					String contentTxt = c.getTxt();
					String contentTxt1 = c.getTxt1();
					if (c.getTxt() != null){
						//修改手机端图片尺寸
						c.getContentTxt().setTxt(MobileUtils.findPictureWidthHeight(c.getTxt()));
						//修改手机端腾讯视频尺寸
						c.getContentTxt().setTxt(MobileUtils.findTenXunVideoSize(c.getTxt()));
					}
					if (c.getTxt1() != null){
						//修改手机端图片尺寸
						c.getContentTxt().setTxt1(MobileUtils.findPictureWidthHeight(c.getTxt1()));
						//修改手机端腾讯视频尺寸
						c.getContentTxt().setTxt1(MobileUtils.findTenXunVideoSize(c.getTxt1()));
					}
					data.put("content", c);
					data.put("channel", c.getChannel());
					outMobile = new OutputStreamWriter(new FileOutputStream(realMobile),UTF8);
					tplMobile.process(data,outMobile);
					c.getContentTxt().setTxt(contentTxt);
					c.getContentTxt().setTxt1(contentTxt1);
				}
				log.info("create static file: {}", file.getAbsolutePath());
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
		c.setNeedRegenerate(false);
		return true;
	}

	private CmsKeywordMng cmsKeywordMng;
	private RealPathResolver realPathResolver;

	@Autowired
	public void setCmsKeywordMng(CmsKeywordMng cmsKeywordMng) {
		this.cmsKeywordMng = cmsKeywordMng;
	}

	@Autowired
	public void setRealPathResolver(RealPathResolver realPathResolver) {
		this.realPathResolver = realPathResolver;
	}
}
