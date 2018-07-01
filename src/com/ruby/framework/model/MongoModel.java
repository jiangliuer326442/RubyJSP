package com.ruby.framework.model;

import javax.servlet.ServletContext;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.ruby.framework.function.CommonFunction;

public class MongoModel implements NoSqlDbInterface {
	private static MongoModel instance = null;
	private MongoDatabase conn;
	private MongoCollection<Document> collection;
	private MongoClient mongoClient;
	private FindIterable<Document> rs;
	
	private MongoModel(ServletContext context) {
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		if(CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","enable").equals("0")){
			conn = null;
		}else{
			String host = CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","host");
			String port = CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","port");
			String database = CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","database");
//			String username = CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","user");
//			String password = CommonFunction.readPropertiesFile(CONF_FOLDER + "mongo.txt","password");

//            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址  
//            //ServerAddress()两个参数分别为 服务器地址 和 端口  
//            ServerAddress serverAddress = new ServerAddress(host, Integer.parseInt(port));  
//            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
//            addrs.add(serverAddress);  
//              
//            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//            MongoCredential credential = MongoCredential.createScramSha1Credential(username, database, password.toCharArray());  
//            List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
//            credentials.add(credential);  
//              
//            //通过连接认证获取MongoDB连接  
//            mongoClient = new MongoClient(addrs,credentials);
			mongoClient = new MongoClient(host, Integer.parseInt(port));
			conn = mongoClient.getDatabase(database);
		}
	}
	
	public static MongoModel connect(ServletContext context) {
        if (instance == null) {    
			instance = new MongoModel(context);
         }
        return instance;
	}
	
	public MongoModel getCollection(String collection){
		if(instance != null){
			this.collection = conn.getCollection(collection);
		}
		return instance;
	}
	
	public void insert(Document document){
		collection.insertOne(document);
	}
	
	public MongoModel find(){
		if(instance != null){
			rs = collection.find();
		}
		return instance;
	}
	
	public MongoModel find(Bson bson){
		if(instance != null){
			rs = collection.find(bson);
		}
		return instance;
	}
	
	public long count(){
		return collection.count();
	}
	
	public long count(Bson bson){
		return collection.count(bson);
	}
	
	public JSONArray toJSON_array() {
		String data = "[";
		try {
			MongoCursor<Document> mongoCursor = rs.iterator();
			while (mongoCursor.hasNext()) {
				Document document = mongoCursor.next();
				data += document.toJson() + ",";		
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		data = data.substring(0, data.length()-1);
		data += "]";
		JSONArray json = new JSONArray(data);
		return json;
	}
	
	public void close(){
		mongoClient.close();
	}
	
}
