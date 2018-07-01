package com.ruby.framework.model;

import javax.servlet.ServletContext;

interface DbManagerInterface extends DbInterface {
	public void connect(ServletContext context);
}
