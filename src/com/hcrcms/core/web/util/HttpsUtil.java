package com.hcrcms.core.web.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

/**
 * 用JAVA访问HTTPS网站，只有这个代码是好使的
 * @author jingrun.zhang
 *
 */
public class HttpsUtil {

	public static void main(String[] args) {

		String url = "https://www.baidu.com/s?wd=里约奥运会&tn=baidurs2top";
		System.out.println(HttpsUtil.getResult(url));
		
	}

	public static String relatedWords(String keyword){
		String result = "";
		try {
			if (!ChannelCacheUtils.baiduRelateMap.isEmpty()){
				for (String key : ChannelCacheUtils.baiduRelateMap.keySet()){
					if (keyword.equals(key)){
						String val = ChannelCacheUtils.baiduRelateMap.get(key);
						result = val.split("_")[0];
						break;
					}
				}
				if ("".equals(result)){
					result = getRelate(keyword);
				}
			}else{
				result = getRelate(keyword);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getRelate(String keyword){
		String result = "";
		try {
			String url = "https://www.baidu.com/s?wd="
					+ URLEncoder.encode(keyword, "utf-8") + "&tn=baidurs2top";
			result = getResult(url);
			StringBuilder sb = new StringBuilder();
			String[] tags = result.split(",");
			if (tags.length > 0) {
				for (int i = 0; i < 3; i++) {
					sb.append(tags[i]).append(",");
				}
				if (sb.length() > 0) {
					sb.setLength(sb.length() - 1);
				}
				result = sb.toString();
				//把从百度获取的结果放到缓存中
				synchronized (ChannelCacheUtils.baiduRelateString) {
					String val = result + "_" + new Date().getTime();
					ChannelCacheUtils.baiduRelateMap.put(keyword, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getResult(String url){
		HttpClient HttpClient = getSSLHttpClient();
		String x = getDoc(HttpClient, url);
		//System.out.println(x);
		return x;
	}
	
	public static String getDoc(HttpClient httpClient, String url) {
		HttpGet get = null;
		try {
			get = new HttpGet(url);
			get.setHeader(
			"User-Agent",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
			// 执行getMethod
			HttpResponse response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + response.getStatusLine().getStatusCode());
			}
			// 读取内容
			InputStream inputStream = response.getEntity().getContent();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(inputStream, baos);
			try {
			String result = new String(baos.toByteArray(), "utf-8");
			return result;
			} catch (Exception e) {
		
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			if (null != get)
				get.abort();
		}
		return "";
	}

	public static HttpClient getSSLHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
	
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,
			String authType) throws CertificateException {
			}
		
			public void checkServerTrusted(X509Certificate[] chain,
			String authType) throws CertificateException {
			}
		
			public X509Certificate[] getAcceptedIssuers() {
			return null;
			}
		};
	
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
		
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
		
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
		
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			} catch (KeyManagementException e1) {
			// TODO
			e1.printStackTrace();
	
		} catch (NoSuchAlgorithmException e1) {
			// TODO
			e1.printStackTrace();
		}
	
		httpClient.getParams().setParameter(
		CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
		20000);
		return httpClient;
	}
	
}
