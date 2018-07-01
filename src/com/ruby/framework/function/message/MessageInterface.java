package com.ruby.framework.function.message;

public interface MessageInterface {
	//发送文字短信
	public boolean send(String phone, String content);
	
	//发送模板短信
	public boolean send(String phone, int tmpl, String[] datas);
}
