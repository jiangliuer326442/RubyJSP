package com.ruby.framework.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

public class SqliteModel {
	private static SqliteModel instance = null;
	private Connection conn;
	
	private SqliteModel(ServletContext context) {
		String db_folder = context.getAttribute("__ROOT__")+"db"+File.separator;
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		if (CommonFunction.readPropertiesFile(CONF_FOLDER + "sqlite.txt", "enable").equals("0")) {
			conn = null;
		} else {
			String db_file = db_folder + CommonFunction.readPropertiesFile(CONF_FOLDER + "sqlite.txt", "file");
			String db_pwd = CommonFunction.readPropertiesFile(CONF_FOLDER + "sqlite.txt", "pwd");
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite://"+db_file);
				if (!conn.isClosed())
					System.out.println("Succeeded connecting to the Sqlite!");
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}
	
	public static SqliteModel connect(ServletContext context) {
        if (instance == null) {    
			instance = new SqliteModel(context);
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
