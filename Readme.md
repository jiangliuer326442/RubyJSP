# RubyJSP是什么？
  它是一个Java Web 基于MVC开发的一个轻量级框架，相对于SSH框架而言，更加简单实用，初学者更容易上手。
# 开始旅程
  WebContent\WEB-INF\config目录是项目所有配置文件的存放目录，其中路由配置文件为url.txt，配置内容格式为：default = Default:index，一行一个，default代表网站访问的路径为default.jsp（或者网站根目录/），对应的控制器为 Default.java 文件的 index 函数。
  
  src\webapp 是项目用户源代码存放的路径，Default.java文件就在该目录内。查看Default.java文件，代码如下：
```
package webapp;
import java.io.IOException;
import org.json.JSONArray;
import com.ruby.framework.controller.ControllerBase;

public class Default extends ControllerBase {
	public void index() throws IOException{
		String result = _model.sqlite_model.executeQuery("select * from students");
		JSONArray array = new JSONArray(result);
		String student_name = "";
		if (array.length() >= 1) {
			student_name = array.getJSONObject(0).getString("name");
		}
		assign("student_name", student_name);
		display("index",1);
	}
}
```
  所有用户自定义的控制器类必须继承ControllerBase基类，这样，该控制器就能拿到ServletContext对象了（它是tomcat容器的上下文对象，你可能会用到它）。另外ControllerBase连接着模型和视图，通过_model对象拿到数据库模型实例。RubyJSP支持常用的数据库模型，包括 Mysql、Sqlite、Redis、MongoDB、ElasticSearch。调用assign方法给即将加载的模型分配变量，调用display方法，加载对应的视图。
  
  在这里 _model.sqlite_model 拿到了Sqlite模型的一个连接实例，你会问Sqlite数据库的配置信息在哪里呢？上面说过，WebContent\WEB-INF\config目录是项目所有配置文件的存放目录，所以这里有一个sqlite.txt文件就是它的配置文件，内容如下：
```
enable = 1
file = test.db
```
  所有数据库配置文件都有一个enable = ？的配置项，代表是否启用该数据库，后面是数据库的基本信息，file代表数据库文件名称为 test.db，该文件在WebContent\WEB-INF\db目录中。
  
  sqlite_model 实例有两个常用的方法 executeQuery 执行查询语句，返回内容为可转化为json数据的字符串，executeUpdate 执行除了查询语句以外的任意sql语句。
  
  项目的视图文件存放目录为 WebContent\WEB-INF\view，默认情况下，视图文件的扩展名为html，可在tmpl.txt配置文件中修改。查看index.html文件，内容如下：
  
```
<h1>你好，<%= student_name %>！</h1>
```
  student_name 需要和控制器中assign的变量名一致，<% 和 %>是模版分隔符，模版分隔符也可以在tmpl.txt中进行配置，传给模版解析器表明里面的内容是需要解析的，=代表是赋值语句。
  
  模版解析器支持的模版操作除了= 代表赋值以外，还包括如下：
  
* ** include**代表包含另外一个模板文件 
* ** if**代表逻辑判断
# 原理剖析
  框架的源代码在src\com\ruby\framework包中，GlobalServlet.java是项目唯一的Servlet实例，Frame.java是项目唯一Sevlet监控实例。
  
  Servlet实例生成时，Frame.java执行contextInitialized方法，主要工作为创建模型和视图的实例，同时，这也意味着，数据库是在Servlet实例生成时保持长连接的，而并非每次请求时再去创建连接。Servlet实例销毁时，调用contextDestroyed方法关闭数据库连接。
  
  GlobalServlet.java是项目唯一的Servlet实例，也是所有请求的统一入口，主要工作是根据url和路由配置文件，将请求携带数据转发（通过java的反射机制）到相应控制器的class中。
  
  是的，原理就是这么简单~
# 功能插件
  除了上面介绍过，支持大量主流的数据库之外，在目录com\ruby\framework\function中，还支持如下网站开发常用的操作类库：
  
* email 邮件发送相关
* file 文件管理相关
* login 第三方登录相关（QQ，微信，微博，钉钉）
* message 短信发送相关（融联云通讯）
* push 推送相关（及光推送）
* upload 上传相关（七牛云存储）
* zip 文件压缩解压相关
其他等等