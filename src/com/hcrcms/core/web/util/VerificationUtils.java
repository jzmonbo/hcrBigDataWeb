package com.hcrcms.core.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * @author jingrun.zhang
 *
 */
public class VerificationUtils {

	/**
	 * 验证URL
	 */
	public static boolean verificationUrl(String url){
		boolean result = true;
		Matcher matcher;
		try {
			//String verifiChar = "^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$";
			String verifiChar = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&amp;.+=.*)?)?";
			//String verifiChar = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			Pattern patt = Pattern.compile(verifiChar);
			matcher = patt.matcher(url);
			result = matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		String url = "wwwdatayuancn中国http://www.datayuan.cn";
		System.out.println(verificationUrl(url));
		
	}
}
