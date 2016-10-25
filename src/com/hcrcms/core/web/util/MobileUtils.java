package com.hcrcms.core.web.util;

/**
 * 手机工具类
 * @author jingrun.zhang
 *
 */
public class MobileUtils {

	/**
	 * 修改手机静态页面模型地址
	 */
	public static String getMobilePath(String modelPath){
		String mobileModelPath = "";
		//这里转换成移动端模板路径
		String base = "/WEB-INF/";
		mobileModelPath = modelPath.substring(0,modelPath.indexOf(base)+9)+"m/"+modelPath.substring(modelPath.indexOf(base)+9);
		return mobileModelPath;
	}
	
	/**
	 * 修改手机静态页面地址
	 */
	public static String getMobileStaticPath(String path){
		String mobileStaticPath = "";
		String base = "\\jx";
		String base1 = "/jx";
		if (path.indexOf(base) > -1){
			mobileStaticPath = path.substring(0,path.indexOf(base)) + "\\m" + path.substring(path.indexOf(base));
		}else if (path.indexOf(base1) > -1){
			mobileStaticPath = path.substring(0,path.indexOf(base1)) + "/m" + path.substring(path.indexOf(base1));
		}
		return mobileStaticPath;
	}
	
	/**
	 * 更新正文图片宽度和高度为100%
	 */
	public static String findPictureWidthHeight(String str){
		String result = "";
		if (str != null){
			int imgStart = str.indexOf("<img");
			while (imgStart > -1){
				result += str.substring(0,imgStart);
				str = str.substring(imgStart);
				int imgEnd = str.indexOf("/>");
				String target = str.substring(0,imgEnd+2);
				//System.out.println("原来串：" + target);
				String targetImg = insteadWidthHeightPercen(target);
				//System.out.println("新串：" + targetImg);
				str = str.substring(imgEnd+2);
				//result += "######";
				result += targetImg;
				imgStart = str.indexOf("<img");
			}
			result += str;
		}
		return result;
	}
	
	/**
	 * 更新腾讯视频尺寸为100%
	 */
	public static String findTenXunVideoSize(String str){
		String result = "";
		if (str != null){
			int iframeLocal = str.indexOf("<iframe");
			if (iframeLocal > -1){
				result += str.substring(0,iframeLocal);
				str = str.substring(iframeLocal);
				int heightLocal = str.indexOf("height=\"");
				result += str.substring(0,heightLocal+8);
				str = str.substring(heightLocal+8);
				result += "100%";
				int heightSY = str.indexOf("\"");
				str = str.substring(heightSY);
				int widthLocal = str.indexOf("width=\"");
				result += str.substring(0,widthLocal+7);
				str = str.substring(widthLocal+7);
				result += "100%";
				int widthSY = str.indexOf("\"");
				str = str.substring(widthSY);
				result += str;
			}else{
				result = str;
			}
		}
		return result;
	}
	
	/**
	 * 把图片中宽度和高度改为100%显示
	 */
	public static String insteadWidthHeightPercen(String srcImg){
		String result = "";
		//<img alt=\"\" src=\"/hcrBigDataWeb/u/cms/www/201607/04142350cj0w.jpg\" style=\"height:635px; width:240px\" />
		int styleIndex = srcImg.indexOf("style=\"");
		if (styleIndex > -1){
			result += srcImg.substring(0,styleIndex+7);
			srcImg = srcImg.substring(styleIndex+7);
			result += "width:100%;height:100%;\"";
			int shuangyinIndex = srcImg.indexOf("\"") + 1;
			result += " data-preview-src=\"\" data-preview-group=\"1\" " + srcImg.substring(shuangyinIndex);
		}else{
			//result = srcImg;
			int imgEnd = srcImg.indexOf("/>");
			result = srcImg.substring(0,imgEnd);
			srcImg = srcImg.substring(imgEnd);
			result += " style=\"width:100%;height:100%;\"" + " data-preview-src=\"\" data-preview-group=\"1\" ";
			result += srcImg;
		}
		return result;
	}
}
