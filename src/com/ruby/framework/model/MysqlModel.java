package com.ruby.framework.model;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL数据库操作基类
 * <p>
 * Title:MysqlModel
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:上海夜伦网络信息技术有限公司
 * </p>
 *
 * @author 方海亮
 *
 * @date 2016年8月5日 上午9:58:36
 *
 */
public class MysqlModel {
	private static MysqlModel instance = null;
	private Connection conn;

	private MysqlModel(ServletContext context) {
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		if (CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "enable").equals("0")) {
			conn = null;
		} else {
			String host = CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "host");
			String port = CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "port");
			String database = CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "database");
			String user = CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "user");
			String password = CommonFunction.readPropertiesFile(CONF_FOLDER + "mysql.txt", "password");
			
			/**
			String server_host = "172.17.0.10";
			String server_port = "22";
			String server_username = "sunyu";
			String server_password = "sunyu";
			try {  
	            JSch jsch = new JSch();  
	            Session session = jsch.getSession(server_username, server_host, Integer.parseInt(server_port));  
	            session.setPassword(server_password);  
	            session.setConfig("StrictHostKeyChecking", "no");  
	            session.connect();  
	            System.out.println(session.getServerVersion());//这里打印SSH服务器版本信息  
	            int assinged_port = session.setPortForwardingL(Integer.parseInt(port), host, Integer.parseInt(port));  
	            System.out.println("localhost:" + assinged_port + " -> " + host + ":" + port);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
			**/
			
			// 驱动程序名
			String driver = "com.mysql.jdbc.Driver";

			// URL指向要访问的数据库名scutcs
			//String url = "jdbc:mysql://localhost:"+port+"/"+database+"?characterEncoding=UTF-8&autoReconnect=true";
			String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?characterEncoding=UTF-8&autoReconnect=true";

			try {
				// 加载驱动程序
				Class.forName(driver);

				// 连续数据库
				conn = DriverManager.getConnection(url, user, password);

				if (!conn.isClosed())
					System.out.println("Succeeded connecting to the Mysql!");
			} catch (SQLException e) {

				e.printStackTrace();

			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}

	public static MysqlModel connect(ServletContext context) {
        if (instance == null) {    
			instance = new MysqlModel(context);
         }
        return instance;
	}

	// 执行查询语句，返回数据集
	public String executeQuery(String sqlString) {
		String result = "";
		if(result == ""){
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sqlString);
				result = toJSON_array(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int executeUpdate(String sqlString) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			count = st.executeUpdate(sqlString);
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return count;
	}

	/**
	 * 关闭MYQL数据库
	 * 
	 * @return void
	 * @date 2016年8月5日 上午9:59:20
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
		}
	}

	private String toJSON_array(ResultSet rs) {
		JSONArray result = new JSONArray();
		ResultSetMetaData rsmd = null;// 获取数据库列名
		int numberOfColumns = 0;// 返回集的列数
		try {
			rsmd = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 取数据库的列名 我觉得名比1，2，3..更好用
		try {
			numberOfColumns = rsmd.getColumnCount();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 获得列数
		try {
			while (rs.next()) {
				JSONObject object = new JSONObject();
				for (int i = 1; i <= numberOfColumns; i++) {
					object.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				result.put(object);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return result.toString();
	}

}
