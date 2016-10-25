package com.hcrcms.core.web.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


public class HttpUtil {
    //private static Logger log = Logger.getLogger(HttpUtil.class);
    public static String doHPPost(String url,String dataContent) throws
            Exception {
        long begin = System.currentTimeMillis();
        long usedTimes = 0;

        MyPostMethod httppost = new MyPostMethod(url);
        HttpClient client = new HttpClient();
        //httppost.setRequestHeader("content-type","text/xml; charset=UTF-8");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                10000);
        client.getHttpConnectionManager().getParams().setIntParameter(
                HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 10 * 1024 * 1024);
        client.getHttpConnectionManager().getParams().setReceiveBufferSize(10 *
                1024 * 1024);

        /*NameValuePair[] data = {
        		new NameValuePair("transType",transcode),
        		new NameValuePair("transMessage",msg)
        };
        httppost.setRequestBody(data);*/
        
        //String cathectic = "transType="+transcode+"&transMessage="+msg;
        //httppost.setRequestBody(cathectic);

        try {
            client.executeMethod(httppost);
            String response = httppost.getResponseBodyAsString();
            usedTimes = System.currentTimeMillis() - begin;
            // log.info("耗时[" + usedTimes + "] 指令[" + transcode + "] ");
            //log.info("发送信息成功！耗时[" + usedTimes + "] 指令[" + transcode + "] 返回[" + response + "] ");

            return response;
        } catch (Exception ex) {
        	//log.error("发送命令出错了:",ex);
        	System.out.println("send post request is error:" + ex);
            throw new Exception("发送信息异常[" + ex.getMessage() + "] " + ex.getMessage());
        }
    }

    public static String doHPGet(String httpUrl, String httpArg) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    //httpUrl = httpUrl + "?" + httpArg;
	    //System.out.println(httpUrl);
 
	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	        //connection.setRequestProperty("apikey",  "********************");
	        connection.connect();
	        
	        int zhuangtaima = connection.getResponseCode();
	        System.out.println("服务器状态:"+zhuangtaima);
	        
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}

    public static class MyPostMethod extends PostMethod {
        public MyPostMethod(String url) {
            super(url);
        }

        @Override
        public String getRequestCharSet() {
            //return super.getRequestCharSet();
            return "UTF-8";
        }
    }
}

