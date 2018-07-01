package com.ruby.framework.model;

import javax.servlet.ServletContext;

public class DbManager implements DbManagerInterface {
	public static MongoModel mongo_model;
	public static RedisModel redis_model;
	public static MysqlModel mysql_model;
	public static ElasticModel elastic_model;
	public static SqliteModel sqlite_model;
	
	private static DbManager instance = null; 
	
	   /* 私有构造方法，防止被实例化 */  
    private DbManager(ServletContext context) {  
    	connect(context);
    }  
  
    /* 静态工程方法，创建实例 */  
    public static DbManager getInstance(ServletContext context) {  
        if (instance == null) {  
            instance = new DbManager(context);  
        }  
        return instance;  
    }  
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		//mongo_model.close();
		mysql_model.close();
		redis_model.close();
		sqlite_model.close();
	}

	@Override
	public void connect(ServletContext context) {
		mysql_model = MysqlModel.connect(context);
		//加载模型组件
		//mongo_model = MongoModel.connect(context);
		redis_model = RedisModel.connect(context);
		//elastic_model = ElasticModel.connect(context);
		sqlite_model = SqliteModel.connect(context);
	}

}
