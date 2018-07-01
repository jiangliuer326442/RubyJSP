package com.ruby.framework.function.upload;

import javax.servlet.ServletContext;

public class FileupFactory {
	public static FileupInterface produce(ServletContext context){
		//使用七牛云存储
		FileupInterface uploader = new Qiniuupload(context);
		return uploader;
	}
}
