package com.ruby.framework.function.upload;

import java.io.File;

import javax.servlet.ServletContext;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.ruby.framework.function.CommonFunction;

class Qiniuupload implements FileupInterface{
	private ServletContext context;
	
	public Qiniuupload(ServletContext context) {
		this.context = context;
	}

	@Override
	public String upload(String path, String filename) {
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		String ACCESS_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "upload.txt","qiniu_ACCESS_KEY");
		String SECRET_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "upload.txt","qiniu_SECRET_KEY");
		String bucketname = CommonFunction.readPropertiesFile(CONF_FOLDER + "upload.txt","qiniu_bucketname");
		String host = CommonFunction.readPropertiesFile(CONF_FOLDER + "upload.txt","qiniu_host");
		//上传到七牛后保存的文件名
		String FilePath = path + filename;
		String key = filename;
		//上传文件的路径
		//密钥配置
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		//创建上传对象
		UploadManager uploadManager = new UploadManager();
		  try {
			  uploadManager.put(FilePath, key, auth.uploadToken(bucketname));
			  //打印返回的信息
		      //System.out.println(res.bodyString()); 
		  } catch (QiniuException e) {
		  }
		//删除原为文件
		  File f = new File(FilePath); // 输入要删除的文件位置
		  return "http://" + host + "/" + key;
	}

}
