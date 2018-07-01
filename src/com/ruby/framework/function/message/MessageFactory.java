package com.ruby.framework.function.message;

import javax.servlet.ServletContext;

public class MessageFactory {
	public static MessageInterface produce(ServletContext context){
		MessageInterface instance = new RonglianMessage(context);
		return instance;
	}
}
