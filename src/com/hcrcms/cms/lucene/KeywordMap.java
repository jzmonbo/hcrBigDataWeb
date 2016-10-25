package com.hcrcms.cms.lucene;

import java.util.HashMap;
import java.util.Map;

/**
 * 不能自动分词，需要手工来分词
 * @author jingrun.zhang
 *
 */
public class KeywordMap {

	public static Map<String,String> keywordMap = new HashMap<String,String>();
	
	static{
		keywordMap.put("大数据分析","大数据,分析");
	}
	
}
