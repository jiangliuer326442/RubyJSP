package com.ruby.framework.model;

import java.sql.ResultSet;

import org.json.JSONArray;

/**
 * SQL数据库操作接口
 * @author SC000749
 *
 */
interface SqlDbInterface extends DbInterface {
	//执行查询
	public SqlDbInterface executeQuery(String sqlString);
	//返回resultSet;
    public ResultSet get();
    //执行更新操作
    public int executeUpdate(String sqlString);
    //关闭连接
    public void close();
    //以json数组格式返回
    public JSONArray toJSON_array();
}
