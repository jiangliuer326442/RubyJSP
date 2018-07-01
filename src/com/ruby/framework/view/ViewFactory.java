package com.ruby.framework.view;

import javax.servlet.ServletContext;

import com.ruby.framework.function.CommonFunction;

public class ViewFactory {
	public static ViewInterface produce(ServletContext context) {
		ViewInterface _view = new ViewBase(context);
		return _view;
	}
}
