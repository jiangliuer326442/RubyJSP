package com.ruby.framework.function.email;

import javax.servlet.ServletContext;

abstract interface EmailInterface {
	
	/**
	 * 发送邮件
	 * @param context 上下文环境
	 * @param to 收件人
	 * @param to_name 收件人姓名
	 * @param title 邮件标题
	 * @param content 邮件内容
	 * @return 发送结果
	 */
	public boolean send(ServletContext context, String to, String to_name, String title, String content) throws Exception;
}
