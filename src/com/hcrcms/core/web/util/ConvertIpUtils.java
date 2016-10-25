package com.hcrcms.core.web.util;

/**
 * IP地址转换工具
 * @author jingrun.zhang
 *
 */
public class ConvertIpUtils {

	public static long iptolong(String strip)
		//将127.0.0.1形式的ip地址转换成10进制整数，这里没有进行任何错误处理
		{
		int j=0;
		int i=0;
		long[] ip=new long[4];
		int position1=strip.indexOf(".");
		int position2=strip.indexOf(".",position1+1);
		int position3=strip.indexOf(".",position2+1);
		ip[0]=Long.parseLong(strip.substring(0,position1));
		ip[1]=Long.parseLong(strip.substring(position1+1,position2));
		ip[2]=Long.parseLong(strip.substring(position2+1,position3));
		ip[3]=Long.parseLong(strip.substring(position3+1));
		return(ip[0]<<24)+(ip[1]<<16)+(ip[2]<<8)+ip[3];//ip1*256*256*256+ip2*256*256+ip3*256+ip4
	}
	public static String longtoip(long longip)
		//将10进制整数形式转换成127.0.0.1形式的ip地址，在命令提示符下输入ping3396362403l
		{
		StringBuffer sb=new StringBuffer("");
		sb.append(String.valueOf(longip>>>24));//直接右移24位
		sb.append(".");
		sb.append(String.valueOf((longip&0x00ffffff)>>>16));//将高8位置0，然后右移16位
		sb.append(".");
		sb.append(String.valueOf((longip&0x0000ffff)>>>8));
		sb.append(".");
		sb.append(String.valueOf(longip&0x000000ff));
		sb.append(".");
		return sb.toString();
	}
	public static void main(String[]args)
	{
		/*System.out.print("十进制形式：");
		System.out.println(iptolong("202.112.96.163"));
		System.out.print("普通形式：");
		System.out.println(longtoip(3396362403l));*/
		System.out.println("屬于地方：" + iptolong("182.50.124.211"));
		System.out.println(longtoip(3056762067L));
	} 
	
}
