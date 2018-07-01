package com.ruby.framework;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ruby.framework.model.DbManager;
import com.ruby.framework.model.MysqlModel;
import com.ruby.framework.view.ViewFactory;
import com.ruby.framework.view.ViewInterface;

import webapp.Initialize;

/**
 * servlet监听器
 * 在监听器中
 * 创建模型
 * 关闭模型
 * 加载视图解析器
 * <p>Title:Frame</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月5日 上午10:00:19
 */
@WebListener
public class Frame implements ServletContextListener{
	private DbManager dbmanager;
	
	@Override
	/**
	 * 关闭数据库连接
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		dbmanager.close();
	}
	
	/**
	 * 加载项目需要的全局数据
	 * 包括
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
    	//上下文环境
		ServletContext context = sce.getServletContext();
		String __ROOT__ = context.getRealPath("/WEB-INF/");
		//根目录
        context.setAttribute("__ROOT__", __ROOT__);
        //配置文件目录
        context.setAttribute("CONF_FOLDER", __ROOT__ + "config" + File.separator);
		//加载视图组件
		ViewInterface _view = ViewFactory.produce(context);
		context.setAttribute("_view", _view);
		dbmanager = DbManager.getInstance(context);
		context.setAttribute("_model", dbmanager);
		//以线程方式执行自定义过程
		new Thread(new Initialize()).start();
	}

}
