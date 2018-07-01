package com.ruby.framework.function.login.thirdparty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ThirdpartyInterface {
	public void setRedirect_URI(String url);
	public String dologin(HttpServletRequest request, HttpServletResponse response);
}
