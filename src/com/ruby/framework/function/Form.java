package com.ruby.framework.function;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.ruby.framework.function.upload.FileupFactory;
import com.ruby.framework.function.upload.FileupInterface;

public class Form implements FormInterface{
	private ServletContext context;
	private String form_key;
	
	public Form(ServletContext context){
		this.context = context;
	}
	
	@Override
	public Map<String, String> createForm(HttpServletRequest request, String form_name) {
		form_key = CommonFunction.getParameter(request,"k",true);
		//文件初始位置
		String path = context.getRealPath("/META-INF/upload/" + form_name + "/");
		Map<String, String> result = new HashMap<String , String>();;
		//获得磁盘文件条目工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(new File(path));
		factory.setSizeThreshold(1024*1024) ;
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			//可以上传多个文件
			List<FileItem> list = (List<FileItem>)upload.parseRequest(request);
			for(FileItem item : list){
				//获取表单的属性名字
				String name = item.getFieldName();
				//如果获取的 表单信息是普通的 文本 信息
				if(item.isFormField()){					
					//获取用户具体输入的字符串 ，名字起得挺好，因为表单提交过来的是 字符串类型的
					String value = item.getString();
					
					if(value != null && !value.equals("")){
						value = AesUtil.decrypt(value, form_key);
						
						if(value == null){
							result.put(name, "");
						}else{
	                        value = java.net.URLDecoder.decode(java.net.URLDecoder.decode(value, "UTF-8"), "UTF-8");
							result.put(name, value);
						}
					}
				}
				//对传入的非 简单的字符串进行处理 ，比如说二进制的 图片，电影这些
				else{
					//截取 上传文件的 字符串名字，加1是 去掉反斜杠，
					String filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
					//文件名进行重命名
					filename = new StringBuffer().append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(new Random().nextInt(10000)).append(filename.substring( filename.lastIndexOf("."))).toString();
					//真正写到磁盘上
					//它抛出的异常 用exception 捕捉
					item.write( new File(path,form_name + "_" + filename) );//第三方提供的
					
					//使用七牛云存储进行文件上传
					FileupInterface uploader = FileupFactory.produce(context);
					result.put(name, uploader.upload(path, form_name + "_" + filename));
				}
			}	
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
