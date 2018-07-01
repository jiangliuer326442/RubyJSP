package com.ruby.framework.view;

public interface ViewInterface {
	//初始化
	public void init();
	//分配变量
	public void assign(String key, String value);
	//调用视图
	public String display(String tmpl);
	//调用视图
	public String display(String tmpl, boolean is_cache);
	//调用视图
	public String display(String tmpl, long cache_limits);
}
