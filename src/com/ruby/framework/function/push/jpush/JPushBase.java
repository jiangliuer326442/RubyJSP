package com.ruby.framework.function.push.jpush;

import javax.servlet.ServletContext;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.push.PushInterface;

public class JPushBase implements PushInterface {
	private String jpush_appkey;
	private String jpush_secret;
	
	public JPushBase(ServletContext context){
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		jpush_appkey = CommonFunction.readPropertiesFile(CONF_FOLDER + "push.txt","jpush_appkey");
		jpush_secret = CommonFunction.readPropertiesFile(CONF_FOLDER + "push.txt","jpush_secret");
	}
	
    public static PushPayload buildPushObject_all_all_alert(String uid, String msg) {
        return PushPayload.newBuilder()
        .setPlatform(Platform.android())
        .setAudience(Audience.newBuilder()
        		.addAudienceTarget(AudienceTarget.registrationId(uid))
        		.build())
        .setNotification(Notification.alert(msg))
        .build();
    }

	@Override
	public void send(String uid, String msg) {
		JPushClient jpushClient = new JPushClient(jpush_secret, jpush_appkey, null, ClientConfig.getInstance());
		PushPayload payload = buildPushObject_all_all_alert(uid, msg);
	    try {
	        PushResult result = jpushClient.sendPush(payload);
	        System.out.println("Got result - " + result);

	    } catch (APIConnectionException e) {
	        // Connection error, should retry later
	    	System.out.println("Connection error, should retry later");

	    } catch (APIRequestException e) {
	        // Should review the error, and fix the request
	    	System.out.println("Should review the error, and fix the request");
	    	System.out.println("HTTP Status: " + e.getStatus());
	    	System.out.println("Error Code: " + e.getErrorCode());
	    	System.out.println("Error Message: " + e.getErrorMessage());
	    }
	}
}
