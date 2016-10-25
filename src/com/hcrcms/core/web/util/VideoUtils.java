package com.hcrcms.core.web.util;

import java.io.ByteArrayOutputStream;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.URL; 

/**
 * 请求腾讯视频当天有效引用地址
 * @author jingrun.zhang
 *
 */
public class VideoUtils {

	public String getTencentMovieSource(String url) {  
        //String html = this.getHtml(url, true).replaceAll("  ", "");  
        //String vid = this.getValue(html, "vid:", 1, "\",", 0);
		String vid = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
        String urlXml = "http://vv.video.qq.com/geturl?platform=1&otype=xml&vid=" + vid;  
        if(urlXml.indexOf("|") == -1)  
            return this.parseXmlSource(urlXml);  
        else{  
            String urls = "";  
            String [] uls = urlXml.replace("|", "-").split("-");  
            for (int i = 0; i < uls.length; i++){  
                String htmls = "http://vv.video.qq.com/geturl?platform=1&otype=xml&vid=" + uls[i];  
                urls += this.parseXmlSource(htmls) + "$";  
            }  
            return urls.substring(0,urls.lastIndexOf("$"));  
        }  
    }  
      
    private String parseXmlSource(String urlXml){  
        String videoXml = getHtml(urlXml, false);  
        return getValue(videoXml, "<url>", "</url>");  
    }  
      
    private String getHtml(String url , boolean isformat){  
        System.out.println("Request URL:"+url);  
        try{  
            URL u = new URL(url);  
            HttpURLConnection httpConn = (HttpURLConnection) u.openConnection();  
            //设置user agent确保系统与浏览器版本兼容  
            HttpURLConnection.setFollowRedirects(true);  
            httpConn.setRequestMethod("GET");   
            httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");  
            InputStream is = u.openStream();  
            int length = 0;  
            ByteArrayOutputStream bos = new ByteArrayOutputStream();   
            while((length=is.read()) != -1) {   
                 bos.write(length);  
            }  
            if(isformat)  
                return new String(bos.toByteArray(),"UTF-8").replace("\r", "").replace("\n", "");  
            else  
                return new String(bos.toByteArray(),"UTF-8");  
        }catch(Exception e){  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    private String getValue(String html ,String s1,String s2){  
        try{  
            String subHtml = html.substring(html.indexOf(s1));  
            subHtml = subHtml.substring(s1.length());  
            int s2Len = subHtml.indexOf(s2);  
            return String.valueOf(subHtml.substring(0,s2Len));  
        }catch(Exception e){  
            e.printStackTrace();  
            return null;  
        }  
    }  
          
    private String getValue(String html , String s1 ,int s1length , String s2 , int s2length){  
        try{  
            StringBuffer subHtml = new StringBuffer(html.substring(html.indexOf(s1)));  
            return String.valueOf(subHtml.substring(s1.length() + s1length , subHtml.indexOf(s2) - s2length));  
        }catch(Exception e){  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    public static void main(String[] args) {  
        //String s = new TX().getTencentMovieSource("http://v.qq.com/cover/y/y1xpnck2llvs7wa.html");  
    	//1.http://v.qq.com/page/b/p/l/b01980uv2pl.html  2.http://v.qq.com/x/page/g0316egabxg.html
    	//String s = new VideoUtils().getTencentMovieSource("http://v.qq.com/x/page/v03203ktfmm.html");
    	String s = new VideoUtils().getTencentMovieSource("http://v.qq.com/x/page/d0326i3e2xr.html");
        System.out.println("视频源地址:"+s);  
    }  
	
}
