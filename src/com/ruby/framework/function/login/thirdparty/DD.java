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

public class DD implements ThirdpartyInterface{
	private String app_ID;
	private String app_KEY;
	private String redirect_URI = null;
	private String scope;
	
	public DD(ServletContext context){
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		app_ID = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "dd_app_ID");
		app_KEY = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "dd_app_KEY");
		try {
			redirect_URI = java.net.URLEncoder.encode(CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "dd_redirect_URI"),   "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		scope = CommonFunction.readPropertiesFile(CONF_FOLDER + "login.txt", "dd_scope");
	}
	
	public void setRedirect_URI(String url){
		redirect_URI = url;
	}
	
	@Override
	public String dologin(HttpServletRequest request, HttpServletResponse response){	
		String openid = null;
		if(CommonFunction.getParameter(request, "code") != null && !CommonFunction.getParameter(request, "code").equals("")){
			String content = null;
			content = CommonFunction.getUrlData("https://oapi.dingtalk.com/sns/gettoken?appid="+app_ID+"&appsecret="+app_KEY);
			JSONObject content_object = new JSONObject(content);
			String access_token = content_object.getString("access_token");
			String tmp_auth_code = CommonFunction.getParameter(request, "code");
			JSONObject jsonobj = new JSONObject();
	        jsonobj.put("tmp_auth_code", tmp_auth_code);
			content = CommonFunction.sendPostJson("https://oapi.dingtalk.com/sns/get_persistent_code?access_token="+access_token, jsonobj);
			content_object = new JSONObject(content);
			openid = content_object.getString("openid");
			String unionid = content_object.getString("unionid");
			String persistent_code = content_object.getString("persistent_code");
			jsonobj = new JSONObject();
			jsonobj.put("openid", openid);
			jsonobj.put("persistent_code", persistent_code);
			content = CommonFunction.sendPostJson("https://oapi.dingtalk.com/sns/get_sns_token?access_token="+access_token, jsonobj);
			content_object = new JSONObject(content);
			String sns_token = content_object.getString("sns_token");
			content = CommonFunction.getUrlData("https://oapi.dingtalk.com/sns/getuserinfo?sns_token="+sns_token);
			JSONObject userinfo = new JSONObject(content).getJSONObject("user_info");
			openid += "|"+userinfo.getString("nick")+"|||"+userinfo.getString("unionid");
		}else{
	        try {
	        	response.sendRedirect("https://oapi.dingtalk.com/connect/qrconnect?appid="+app_ID+"&response_type=code&scope="+scope+"&state=&redirect_uri="+redirect_URI);
	        } catch (IOException e) {
				e.printStackTrace();
			}
		}
		return openid;
	}
}

