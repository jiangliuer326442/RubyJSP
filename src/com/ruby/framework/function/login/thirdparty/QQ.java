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

public class QQ implements ThirdpartyInterface{
	private String app_ID;
	private String app_KEY;
	private String redirect_URI = null;
	private String scope;
	
	public QQ(ServletContext context){
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		app_ID = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "qq_app_ID");
		app_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "qq_app_KEY");
		try {
			redirect_URI = java.net.URLEncoder.encode(CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "qq_redirect_URI"),   "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		scope = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "qq_scope");
	}
	
	public void setRedirect_URI(String url){
		redirect_URI = url;
	}
	
	@Override
	public String dologin(HttpServletRequest request, HttpServletResponse response){	
		String openid = null;
		if(CommonFunction.getParameter(request, "code") != null && !CommonFunction.getParameter(request, "code").equals("")){
			String content = null;
			content = CommonFunction.getUrlData("https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="+app_ID+"&client_secret="+app_KEY+"&code="+CommonFunction.getParameter(request, "code")+"&redirect_uri="+redirect_URI);
			Pattern p = Pattern.compile("access_token=(.*?)&expires_in");
			Matcher m = p.matcher(content);
			String token = null;
			while (m.find()) {
				token = m.group(1);
			}
			content = CommonFunction.getUrlData("https://graph.qq.com/oauth2.0/me?access_token="+token);
			p = Pattern.compile(",\"openid\":\"(.*?)\"}");
			m = p.matcher(content);
			while (m.find()) {
				openid = m.group(1);
			}
			content = CommonFunction.getUrlData("https://graph.qq.com/user/get_user_info?access_token="+token+"&oauth_consumer_key="+app_ID+"&openid="+openid);
			JSONObject user = new JSONObject(content);
			if(user.getInt("ret") == 0){
				openid += "|"+user.getString("nickname")+"|"+user.getString("figureurl_qq_1")+"|"+user.getString("figureurl_qq_2")+"|";
			}
		}else{
	        try {
				response.sendRedirect("https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="+app_ID+"&redirect_uri="+redirect_URI+"&scope="+scope);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return openid;
	}
}
