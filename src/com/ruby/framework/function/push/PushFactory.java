package com.ruby.framework.function.push;

import javax.servlet.ServletContext;

import com.ruby.framework.function.push.jpush.JPushBase;

public class PushFactory {
	public static PushInterface produce (ServletContext context){
		PushInterface instance = new JPushBase(context);
		return instance;
	}
}
