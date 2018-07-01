package com.ruby.framework.function.login.thirdparty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

public class WX implements ThirdpartyInterface{
	private String app_ID;
	private String app_KEY;
	private String web_ID;
	private String web_KEY;
	private String redirect_URI = null;
	private String scope;
	
	public WX(ServletContext context){
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		web_ID = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_web_ID");
		web_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_web_KEY");
		app_ID = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_app_ID");
		app_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_app_KEY");
		try {
			redirect_URI = java.net.URLEncoder.encode(CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_redirect_URI"),   "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		scope = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "wx_scope");
	}
	
	public void setRedirect_URI(String url){
		redirect_URI = url;
	}
	
	@Override
	public String dologin(HttpServletRequest request, HttpServletResponse response){	
		String openid = null;
		if(CommonFunction.getParameter(request, "code") != null && !CommonFunction.getParameter(request, "code").equals("")){
			String my_app_ID = null;
			String my_app_KEY = null;
			if(CommonFunction.getParameter(request, "state").equals("web")){
				my_app_ID = web_ID;
				my_app_KEY = web_KEY;
			}else{
				my_app_ID = app_ID;
				my_app_KEY = app_KEY;
			}
			String content = null;
			content = CommonFunction.getUrlData("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+my_app_ID+"&secret="+my_app_KEY+"&code="+CommonFunction.getParameter(request, "code")+"&grant_type=authorization_code");
			JSONObject content_object = new JSONObject(content);
			String token = content_object.getString("access_token");
			openid = content_object.getString("openid");
			content = CommonFunction.getUrlData("https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openid);
			JSONObject user = new JSONObject(content);
			openid += "|"+user.getString("nickname")+"|"+user.getString("headimgurl")+"|"+user.getString("headimgurl")+"|"+user.getString("unionid");
		}else{
	        try {
				response.sendRedirect("https://open.weixin.qq.com/connect/qrconnect?appid="+web_ID+"&redirect_uri="+redirect_URI+"&response_type=code&scope="+scope+"&state=web");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return openid;
	}
}
