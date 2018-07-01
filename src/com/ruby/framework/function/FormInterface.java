package com.ruby.framework.function;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 表单处理接口
 * @author SC000749
 *
 */
public interface FormInterface {
	//创建表单
	public Map<String, String> createForm(HttpServletRequest request, String form_name);
}
