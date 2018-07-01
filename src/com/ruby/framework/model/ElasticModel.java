package com.ruby.framework.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import org.apache.http.Header;

import com.ruby.framework.function.BASE64;

public class ElasticModel implements NoSqlDbInterface {
	private RestClient restClient;
	private String baseurl;
	public String database;
	private static ElasticModel instance = null;
	
	private ElasticModel(ServletContext context) {
		BasicHeader header;
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		if(CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","enable").equals("0")){
			restClient = null;
		}else{
			String host = CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","host");
			String port = CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","port");
			database = CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","database");
			String username = CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","user");
			String password = CommonFunction.readPropertiesFile(CONF_FOLDER + "elastic.txt","password");
			header = new BasicHeader("Authorization", "Basic "+BASE64.encode((username+":"+password).getBytes()));
			restClient = RestClient.builder(
			        new HttpHost(host, Integer.parseInt(port), "http")).setDefaultHeaders(new Header[]{header}).build();
			baseurl = "http://"+host+":"+port+"/";
		}
	}
	
	public JSONObject get(String url,JSONObject jo) throws Exception{	
		HttpEntity entity = new StringEntity(jo.toString(), ContentType.APPLICATION_JSON);
		Response indexResponse = restClient.performRequest(
		        "GET",
		        baseurl+url,
		        Collections.<String, String>emptyMap(),
		        entity);
		JSONObject result = new JSONObject();
		result.put("status", indexResponse.getStatusLine().getStatusCode());
		result.put("info", "success");
		result.put("data", EntityUtils.toString(indexResponse.getEntity()));
		return result;
	}
	
	public JSONObject put(String url,JSONObject jo) throws Exception{	
		HttpEntity entity = new StringEntity(jo.toString(), ContentType.APPLICATION_JSON);
		Response indexResponse = restClient.performRequest(
		        "PUT",
		        baseurl+url,
		        Collections.<String, String>emptyMap(),
		        entity);
		JSONObject result = new JSONObject();
		result.put("status", indexResponse.getStatusLine().getStatusCode());
		result.put("info", "success");
		result.put("data", EntityUtils.toString(indexResponse.getEntity()));
		return result;
	}
	
	public static ElasticModel connect(ServletContext context) {
        if (instance == null) {    
			instance = new ElasticModel(context);
         }
        return instance;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
