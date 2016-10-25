package com.hcrcms.core.web.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 下载图片资源
 * @author jingrun.zhang
 *
 */
public class DownloadImage {

	public static void savePicture(String urlPath,String saveImagePath){
		try {
			//String fileName = urlPath.substring(urlPath.lastIndexOf("/")+1);
		   //根据String形式创建一个URL对象，
		   URL url = new URL(urlPath);
		   //实列一个URLconnection对象，用来读取和写入此 URL 引用的资源
		   URLConnection con = url.openConnection();
		   //获取一个输入流
		   InputStream is = con.getInputStream();
		   //实列一个输出对象
		   FileOutputStream fos = new FileOutputStream(saveImagePath);
		   //一个byte[]数组，一次读取多个字节
		   byte[] bt = new byte[200];
		   //用来接收每次读取的字节个数
		   int b = 0;

		   //循环判断，如果读取的个数b为空了，则is.read()方法返回-1，具体请参考InputStream的read();
		   while ((b = is.read(bt)) != -1) {
		    //将对象写入到对应的文件中
		    fos.write(bt, 0, b);   
		   }
		   //刷新流
		   fos.flush();
		   //关闭流
		   fos.close();
		   is.close();

		 } catch (Exception e) {
		   e.printStackTrace();
		 }
	}
	
	public static void main(String[] args) {
		
		savePicture("http://img4.cache.netease.com/sports/2016/9/13/20160913103319d2416.jpg","");
		System.out.println("图片下载完成!");
		
	}
	
}
