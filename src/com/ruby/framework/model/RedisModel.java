package com.ruby.framework.model;

import javax.servlet.ServletContext;

import org.json.JSONArray;

import com.ruby.framework.function.CommonFunction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisModel implements NoSqlDbInterface {
	private JedisPool pool = null;
	private ServletContext context;
	private int database;
	
	private RedisModel(ServletContext context) {
		this.context = context;
	}
	
    /** 
     * 构建redis连接池 
     *  
     * @param ip 
     * @param port 
     * @return JedisPool 
     */  
    public JedisPool getPool() {  
        if (pool == null) {  
    		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
    		String host = CommonFunction.readPropertiesFile(CONF_FOLDER + "redis.txt", "host");
    		int port = Integer.parseInt(CommonFunction.readPropertiesFile(CONF_FOLDER + "redis.txt", "port"));
    		database = Integer.parseInt(CommonFunction.readPropertiesFile(CONF_FOLDER + "redis.txt", "database"));
        	
            JedisPoolConfig config = new JedisPoolConfig();  
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
            config.setMaxActive(100);  
            // 设置最大阻塞时间，记住是毫秒数milliseconds
            config.setMaxWait(1000);
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
            config.setMaxIdle(10);  
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
            config.setMaxWait(1000 * 100);  
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
            config.setTestOnBorrow(true);  
            pool = new JedisPool(config, host, port);  
        }  
        return pool;  
    }  
    
    /** 
     * 返还到连接池 
     *  
     * @param pool  
     * @param redis 
     */  
    public void returnResource(JedisPool pool, Jedis redis) {  
        if (redis != null) {  
            pool.returnResource(redis);  
        }  
    }  
    
	
	public static RedisModel connect(ServletContext context) {
       return new RedisModel(context);
	}
	
    /** 
     * 获取数据 
     *  
     * @param key 
     * @return 
     */  
    public String get(String key){  
        String value = null;  
          
        JedisPool pool = null;  
        Jedis jedis = null;  
        try {  
            pool = getPool();  
            jedis = pool.getResource();  
            jedis.select(database);
            value = jedis.get(key);  
        } catch (Exception e) {  
            //释放redis对象  
            pool.returnBrokenResource(jedis);  
            e.printStackTrace();  
        } finally {  
            //返还到连接池  
            returnResource(pool, jedis);  
        }  
          
        return value;  
    }  
	
	public void set(String key, String value, int seconds){
        JedisPool pool = null;  
        Jedis jedis = null;  
        try {  
            pool = getPool();  
            jedis = pool.getResource();  
            jedis.select(database);
            jedis.setex(key, seconds, value);
        } catch (Exception e) {  
            //释放redis对象  
            pool.returnBrokenResource(jedis);  
            e.printStackTrace();  
        } finally {  
            //返还到连接池  
            returnResource(pool, jedis);  
        }  
	}
	
	public void remove(String key){
        JedisPool pool = null;  
        Jedis jedis = null;  
        try {  
            pool = getPool();  
            jedis = pool.getResource();  
            jedis.select(database);
            jedis.del(key);
        } catch (Exception e) {  
            //释放redis对象  
            pool.returnBrokenResource(jedis);  
            e.printStackTrace();  
        } finally {  
            //返还到连接池  
            returnResource(pool, jedis);  
        }  
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		// jedis.disconnect();
	}
}
