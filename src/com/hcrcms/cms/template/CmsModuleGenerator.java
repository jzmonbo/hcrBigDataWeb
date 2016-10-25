package com.hcrcms.cms.template;


public class CmsModuleGenerator {
	private static String packName = "com.hcrcms.cms.template";
	private static String fileName = "hcrcms.properties";

	public static void main(String[] args) {
		new ModuleGenerator(packName, fileName).generate();
	}
}
