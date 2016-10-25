package com.hcrcms.cms.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.hcrcms.cms.Constants;
import com.hcrcms.cms.entity.main.Content;
import com.hcrcms.cms.entity.main.ContentTag;
import com.hcrcms.cms.manager.main.ContentMng;
import com.hcrcms.common.page.Pagination;
import com.hcrcms.common.web.springmvc.RealPathResolver;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.SortClass;

@Service
@Transactional
public class LuceneContentSvcImpl implements LuceneContentSvc {
	@Transactional(readOnly = true)
	public Integer createIndex(Integer siteId, Integer channelId,
			Date startDate, Date endDate, Integer startId, Integer max)
			throws IOException, ParseException {
		String path = realPathResolver.get(Constants.LUCENE_PATH);
		Directory dir = new SimpleFSDirectory(new File(path));
		return createIndex(siteId, channelId, startDate, endDate, startId, max,
				dir);
	}

	@Transactional(readOnly = true)
	public Integer createIndex(Integer siteId, Integer channelId,
			Date startDate, Date endDate, Integer startId, Integer max,
			Directory dir) throws IOException, ParseException {
		boolean exist = IndexReader.indexExists(dir);
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(
				Version.LUCENE_30), !exist, IndexWriter.MaxFieldLength.LIMITED);
		try {
			if (exist) {
				LuceneContent.delete(siteId, channelId, startDate, endDate,
						writer);
			}
			Integer lastId = luceneContentDao.index(writer, siteId, channelId,
					startDate, endDate, startId, max);
			writer.optimize();
			return lastId;
		} finally {
			writer.close();
		}
	}

	@Transactional(readOnly = true)
	public void createIndex(Content content) throws IOException {
		String path = realPathResolver.get(Constants.LUCENE_PATH);
		Directory dir = new SimpleFSDirectory(new File(path));
		createIndex(content, dir);
	}

	@Transactional(readOnly = true)
	public void createIndex(Content content, Directory dir) throws IOException {
		boolean exist = IndexReader.indexExists(dir);
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(
				Version.LUCENE_30), !exist, IndexWriter.MaxFieldLength.LIMITED);
		try {
			writer.addDocument(LuceneContent.createDocument(content));
		} finally {
			writer.close();
		}
	}

	@Transactional(readOnly = true)
	public void deleteIndex(Integer contentId) throws IOException,
			ParseException {
		String path = realPathResolver.get(Constants.LUCENE_PATH);
		Directory dir = new SimpleFSDirectory(new File(path));
		deleteIndex(contentId, dir);
	}

	@Transactional(readOnly = true)
	public void deleteIndex(Integer contentId, Directory dir)
			throws IOException, ParseException {
		boolean exist = IndexReader.indexExists(dir);
		if (exist) {
			IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(
					Version.LUCENE_30), false,
					IndexWriter.MaxFieldLength.LIMITED);
			try {
				LuceneContent.delete(contentId, writer);
			} finally {
				writer.close();
			}
		}
	}

	public void updateIndex(Content content) throws IOException, ParseException {
		String path = realPathResolver.get(Constants.LUCENE_PATH);
		Directory dir = new SimpleFSDirectory(new File(path));
		updateIndex(content, dir);
	}

	public void updateIndex(Content content, Directory dir) throws IOException,
			ParseException {
		boolean exist = IndexReader.indexExists(dir);
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(
				Version.LUCENE_30), !exist, IndexWriter.MaxFieldLength.LIMITED);
		try {
			if (exist) {
				LuceneContent.delete(content.getId(), writer);
			}
			writer.addDocument(LuceneContent.createDocument(content));
		} finally {
			writer.close();
		}
	}

	@Transactional(readOnly = true)
	public Pagination searchPage(String path, String queryString,String category,String workplace,
			Integer siteId, Integer channelId, Date startDate, Date endDate,
			int pageNo, int pageSize) throws CorruptIndexException,
			IOException, ParseException {
		Directory dir = new SimpleFSDirectory(new File(path));
		return searchPage(dir, queryString,category,workplace, siteId, channelId, startDate,
				endDate, pageNo, pageSize);
	}

	@Transactional(readOnly = true)
	public Pagination searchPage(Directory dir, String queryString,String category,String workplace,
			Integer siteId, Integer channelId, Date startDate, Date endDate,
			int pageNo, int pageSize) throws CorruptIndexException,
			IOException, ParseException {
		Searcher searcher = new IndexSearcher(dir);
		try {
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			Query query = LuceneContent.createQuery(queryString,category,workplace, siteId,
					channelId, startDate, endDate, analyzer);
			TopDocs docs = searcher.search(query, pageNo * pageSize);
			Pagination p = LuceneContent.getResultPage(searcher, docs, pageNo,
					pageSize);
			List<?> ids = p.getList();
			List<Content> contents = new ArrayList<Content>();
			for (Object id : ids) {
				Content content = contentMng.findById((Integer) id);
				int channelid = content.getChannel().getId();
				if (content != null && !"71,78,79,82".contains(""+channelid)){
					contents.add(content);
				}
			}
			List<Integer> idss = (List<Integer>) ids;
			
			//***************************************
			if (!ChannelCacheUtils.keywordRelationMap.isEmpty()){
				List<String> krLists = null;
				for (String key : ChannelCacheUtils.keywordRelationMap.keySet()){
					if (queryString.equals(key)){
						krLists = ChannelCacheUtils.keywordRelationMap.get(key);
						break;
					}
				}
				if (krLists != null && krLists.size() > 0){
					BooleanQuery bq = new BooleanQuery();
					Query q;
					for (String operStr : krLists){
						q = MultiFieldQueryParser.parse(Version.LUCENE_30, operStr,
								LuceneContent.QUERY_FIELD, LuceneContent.QUERY_FLAGS, analyzer);
						bq.add(q, BooleanClause.Occur.MUST);
						docs = searcher.search(query, pageNo * pageSize);
						p = LuceneContent.getResultPage(searcher, docs, pageNo,pageSize);
						List<?> idsbak = p.getList();
						List<Integer> nids = new ArrayList<Integer>();
						
						//判断搜索文章ID是否在ids里，只添加没有出现在ids里的文章ID
						for (Object idbk : idsbak){
							boolean idFlag = false;
							for (Integer ido : idss){
								int idni = (int) idbk;
								if (ido == idni){
									idFlag = true;
									break;
								}
							}
							if (!idFlag){
								idss.add((Integer) idbk);
								nids.add((Integer) idbk);
							}
						}
						
						//添加搜索文章
						for (Integer idn : nids) {
							Content content = contentMng.findById(idn);
							int channelid = content.getChannel().getId();
							if (content != null && !"71,78,79,82".contains(""+channelid)){
								contents.add(content);
							}
						}
					}
				}
			}
			//***************************************
			
			//这里需要处理两点内容：1.找到按关键词查询的文章，按标题、标签、详情三处把关键词标红，按时间降序
			//					   2.余下在正文里搜索的文章，按时间降序排序
			//按这两块拼成一个搜索LIST，返回给页面
			List<Content> allContents = new ArrayList<Content>();			//全部文章LIST
			List<Content> keyContents = new ArrayList<Content>();			//按关键词搜索LiST
			List<Content> conContents = new ArrayList<Content>();			//从内容搜索LIST
			List<String> keywords = getKeyWord(queryString);
			
			keyContents = findKeyContents(contents,keywords);
			SortContent(keyContents);
			contents.removeAll(keyContents);
			conContents.addAll(contents);
			SortContent(conContents);
			allContents.addAll(keyContents);
			allContents.addAll(conContents);
			
			//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
			if (allContents != null && allContents.size() > 0){
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : allContents){
					content.setKeywords(keywords);
					if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
						m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()){
							content.setIncludeLetter(1);
						}else{
							content.setIncludeLetter(0);
						}
					}else{
						content.setIncludeLetter(0);
					}
				}
			}
			
			//这里添加按时间降序排序
			/*SortClass sortClass = new SortClass();
			Collections.sort(contents,sortClass);
			Collections.reverse(contents);
			//判断标题中是否有英文，首页文章列表中显示字数，并置字段includeLetter值为1，为0表示全为汉字
			if (contents != null && contents.size() > 0){
				String regex = ".*[a-zA-Z]+.*";
				Matcher m = null;
				for (Content content : contents){
					if (content.getContentExt().getShortTitle() != null && !"".equals(content.getContentExt().getShortTitle())){
						m=Pattern.compile(regex).matcher(content.getContentExt().getShortTitle());
						if (m.matches()){
							content.setIncludeLetter(1);
						}else{
							content.setIncludeLetter(0);
						}
					}else{
						content.setIncludeLetter(0);
					}
				}
			}*/
			p.setList(allContents);
			return p;
		} finally {
			searcher.close();
		}
	}

	/**
	 * 给LIST时间倒序排序
	 */
	public void SortContent(List<Content> contents){
		try {
			SortClass sortClass = new SortClass();
			Collections.sort(contents, sortClass);
			Collections.reverse(contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 找出标题、标签、描述里有关键词的LIST
	 */
	/*public List<Content> findKeyContents(List<Content> contents,List<String> keywords){
		List<Content> kContents = new ArrayList<Content>();
		if (contents != null && contents.size() > 0){
			for (Content content : contents){
				boolean titleFlag = false;
				boolean tagFlag = false;
				boolean descFlag = false;
				for (String kw : keywords){
					//第一步、找短标题
					String shortTitle = content.getShortTitle();
					StringBuilder titleSB = new StringBuilder();
					while (shortTitle.contains(kw)){
						titleSB.append(shortTitle.substring(0,shortTitle.indexOf(kw)));
						titleSB.append("<span style=\"color:red;\">").append(kw).append("</span>");
						shortTitle = shortTitle.substring(shortTitle.indexOf(kw)+kw.length());
					}
					if (titleSB.length() > 0){
						titleSB.append(shortTitle);
						content.getContentExt().setShortTitle(titleSB.toString());
						titleFlag = true;
					}
					
					//第二步、找TAG标签
					List<ContentTag> cTags = content.getTags();
					for (ContentTag tag : cTags){
						StringBuilder tagSB = new StringBuilder();
						if (tag.getName().contains(kw)){
							while (tag.getName().contains(kw)){
								tagFlag = true;
								String tagName = tag.getName();
								tagSB.append(tagName.substring(0,tagName.indexOf(kw)));
								tagSB.append("<span&nbsp;style=\"color:red;\">" + kw + "</span>");
								tag.setName(tag.getName().substring(tag.getName().indexOf(kw)+kw.length()));
							}
							if (tagSB.length() > 0){
								tagSB.append(tag.getName());
							}
						}else{
							tagSB.append(tag.getName());
						}
						tag.setName(tagSB.toString());
					}
					content.setTags(cTags);
					
					//第三步、找描述
					String description = content.getContentExt().getDescription();
					if (description != null){
						StringBuilder descSB = new StringBuilder();
						while (description.contains(kw)){
							descSB.append(description.substring(0,description.indexOf(kw)));
							descSB.append("<span style=\"color:red;\">").append(kw).append("</span>");
							description = description.substring(description.indexOf(kw)+kw.length());
						}
						if (descSB.length() > 0){
							descSB.append(description);
							content.getContentExt().setDescription(descSB.toString());
							descFlag = true;
						}
					}
				}
				if (titleFlag || tagFlag || descFlag){
					kContents.add(content);
				}
			}
		}
		return kContents;
	}*/
	
	public List<Content> findKeyContents(List<Content> contents,List<String> keywords){
		List<Content> kContents = new ArrayList<Content>();
		if (contents != null && contents.size() > 0){
			for (Content content : contents){
				boolean titleFlag = false;
				boolean tagFlag = false;
				boolean descFlag = false;
				for (String kw : keywords){
					//第一步、找短标题
					String shortTitle = content.getShortTitle();
					if (shortTitle.contains(kw)){
						titleFlag = true;
					}
					
					//第二步、找TAG标签
					List<ContentTag> cTags = content.getTags();
					for (ContentTag tag : cTags){
						if (tag.getName().contains(kw)){
							tagFlag = true;
						}
					}
					
					//第三步、找描述
					String description = content.getContentExt().getDescription();
					if (description != null){
						if (description.contains(kw)){
							descFlag = true;
						}
					}
				}
				if (titleFlag || tagFlag || descFlag){
					kContents.add(content);
				}
			}
		}
		return kContents;
	}
	
	/**
	 * 关键词分词
	 * @return
	 */
	public List<String> getKeyWord(String queryString){
		List<String> results = new ArrayList<String>();
		try {
			boolean keywordFlag = false;
			String operKey = "";
			String operKeyValue = "";
			for (String key : KeywordMap.keywordMap.keySet()){
				if (queryString.contains(key)){
					//如果查询关键字里包含处理不了的词，需要手工处理
					keywordFlag = true;
					operKey = key;
					operKeyValue = KeywordMap.keywordMap.get(key);
					break;
				}
			}
			
			if (keywordFlag){
				queryString = queryString.replaceAll(operKey,"");
			}
			
			if (queryString != null && queryString.length() > 0){
				StringReader sr = new StringReader(queryString);
				IKSegmenter ik = new IKSegmenter(sr, true);
				Lexeme lex = null;
				while ((lex = ik.next()) != null) {
					results.add(lex.getLexemeText());
				}
			}
			
			if (keywordFlag){
				String[] operStrs = operKeyValue.split(",");
				for (String operStr : operStrs){
					results.add(operStr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	@Transactional(readOnly = true)
	public List<Content> searchList(String path, String queryString,String category,String workplace,
			Integer siteId, Integer channelId, Date startDate, Date endDate,
			int first, int max) throws CorruptIndexException, IOException,
			ParseException {
		Directory dir = new SimpleFSDirectory(new File(path));
		return searchList(dir, queryString, category,workplace,siteId, channelId, startDate,
				endDate, first, max);
	}

	@Transactional(readOnly = true)
	public List<Content> searchList(Directory dir, String queryString,String category,String workplace,
			Integer siteId, Integer channelId, Date startDate, Date endDate,
			int first, int max) throws CorruptIndexException, IOException,
			ParseException {
		Searcher searcher = new IndexSearcher(dir);
		try {
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			Query query = LuceneContent.createQuery(queryString,category,workplace, siteId,
					channelId, startDate, endDate, analyzer);
			if (first < 0) {
				first = 0;
			}
			if (max < 0) {
				max = 0;
			}
			TopDocs docs = searcher.search(query, first + max);
			List<Integer> ids = LuceneContent.getResultList(searcher, docs,
					first, max);
			List<Content> contents = new ArrayList<Content>(ids.size());
			for (Object id : ids) {
				contents.add(contentMng.findById((Integer) id));
			}
			return contents;
		} finally {
			searcher.close();
		}
	}

	private RealPathResolver realPathResolver;
	private ContentMng contentMng;
	private LuceneContentDao luceneContentDao;

	@Autowired
	public void setRealPathResolver(RealPathResolver realPathResolver) {
		this.realPathResolver = realPathResolver;
	}

	@Autowired
	public void setLuceneContentDao(LuceneContentDao luceneContentDao) {
		this.luceneContentDao = luceneContentDao;
	}

	@Autowired
	public void setContentMng(ContentMng contentMng) {
		this.contentMng = contentMng;
	}

}
