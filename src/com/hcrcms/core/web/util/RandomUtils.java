package com.hcrcms.core.web.util;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomUtils {

	public static void main(String[] args){
		
		/*double max=3740925951L;
        double min=973996032L;
        
        try {
			double double1 = nextDouble(min - 1, max + 1);
			DecimalFormat decimalFormat = new DecimalFormat("##########");//格式化设置  
	        System.out.println(decimalFormat.format(double1));
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		System.out.println(getHeadRandom());
		
	}
	
	public static String getProvinceString(double ran){
		DecimalFormat decimalFormat = new DecimalFormat("##########");//格式化设置  
		return decimalFormat.format(ran);
	}
	
	public static double getProvinceRandom(){
		double max=3740925951L;
        double min=973996032L;
        try {
			return nextDouble(min,max);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static int getHeadRandom(){
		int min = 1;
		int max= 60;
		int result = 0;
		try {
			result = nextInteger(min,max);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static double nextDouble(final double min, final double max) throws Exception {
        if (max < min) {
            throw new Exception("min < max");
        }
        if (min == max) {
            return min;
        }
        return min + ((max - min) * new Random().nextDouble());
    }
	
	
	public static int nextInteger(final int min,final int max) throws Exception{
		if (max < min) {
            throw new Exception("min < max");
        }
        if (min == max) {
            return min;
        }
        return new Random().nextInt(max);
	}
	
	public static int getMingziRandom(){
		int min = 1;
		int max = 3880;
		int result = 0;
		try {
			result = nextInteger(min,max);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
