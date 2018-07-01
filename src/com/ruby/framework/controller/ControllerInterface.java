package com.ruby.framework.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 控制器类的基类
 * @author fanghailiang
 *
 */
public interface ControllerInterface {
	//设置request和response对象
	public void set(ServletContext context, HttpServletRequest request, HttpServletResponse response);
	//设置输出变量
	public void assign(String key, String value);
	//输出页面
	public void display(String tmpl) throws IOException, ServletException;
	//json输出
	public void return_json(int status, String info, Object data) throws IOException;
	//调用子类函数
	public void load(String method) throws IOException;
	public void return_json(int status, String info) throws IOException;
}
