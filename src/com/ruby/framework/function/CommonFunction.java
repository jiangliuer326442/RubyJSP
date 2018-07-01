package com.ruby.framework.function;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;

/**
 * 框架公共函数库
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ruby.framework.controller.ControllerInterface;

/**
 * 公共函数文件
 * <p>Title:CommonFunction</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月5日 下午1:23:25
 */
public class CommonFunction {
	 public static String getIp2(HttpServletRequest request) {
		 String ip = request.getHeader("X-Forwarded-For");
		 if(ip != null && !ip.equals("") && !"unKnown".equalsIgnoreCase(ip)){
			 //多次反向代理后会有多个ip值，第一个ip才是真实ip
			 int index = ip.indexOf(",");
			 if(index != -1){
				 return ip.substring(0,index);
			 }else{
				 return ip;
			 }
		}
		ip = request.getHeader("X-Real-IP");
		if(ip != null && !ip.equals("") && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	//是否手机号
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	//获取参数
	public static String getParameter(HttpServletRequest request, String parameter){
		String result = "";
		result = request.getParameter(parameter);
		return result;
	}
	
	public static String getParameter(HttpServletRequest request, String parameter, boolean is_secure){
		String result = "";
		result = getParameter(request, parameter);
		if(is_secure && result!=null && !result.equals("null") && !request.equals("")){
			result = request.getParameter(parameter).replaceAll(".*([';]+|(--)+).*", " ");
		}else{
			result = "";
		}
		return result;
	}
	
	//md5加密
	public static String EncoderByMd5(String str){
	  try {  
		     
	        // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）  
	        MessageDigest messageDigest =MessageDigest.getInstance("MD5");  
	        // 输入的字符串转换成字节数组  
	        byte[] inputByteArray = str.getBytes();  
	        // inputByteArray是输入字符串转换得到的字节数组  
	        messageDigest.update(inputByteArray);  
	        // 转换并返回结果，也是字节数组，包含16个元素  
	        byte[] resultByteArray = messageDigest.digest();  
	        // 字符数组转换成字符串返回  
	        return byteArrayToHex(resultByteArray);  
	     } catch (NoSuchAlgorithmException e) {  
	        return null;  
	     }  
	}
	
    public static String byteArrayToHex(byte[] byteArray) {  
        
        // 首先初始化一个字符数组，用来存放每个16进制字符  
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };  
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））  
        char[] resultCharArray =new char[byteArray.length * 2];  
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去  
        int index = 0; 
        for (byte b : byteArray) {  
           resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];  
           resultCharArray[index++] = hexDigits[b& 0xf];  
        }
        // 字符数组组合成字符串返回  
        return new String(resultCharArray);  
    }
    
    //发送post请求
    public static String sendPostJson(String url, JSONObject obj) {
    	StringBuffer sb = new StringBuffer("");
    	try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type",
                    "application/json");
            conn.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(
                    conn.getOutputStream());
            out.writeBytes(obj.toString());
            // flush输出流的缓冲
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
            	 line = new String(line.getBytes(), "utf-8");
                 sb.append(line);
            }
            in.close();
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
    	return sb.toString();
    }    
	
	//读取网页内容
    public static String getUrlData(String urlString) {  
        String res = "";   
        try {   
            URL url = new URL(urlString);  
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();  
            conn.setDoOutput(true);  
            conn.setRequestMethod("GET");  
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));  
            String line;  
            while ((line = in.readLine()) != null) {  
                res += line;  
            }  
            in.close();  
        } catch (Exception e) {  
            System.out.println("error in wapaction,and e is " + e.getMessage());  
        }  
//      System.out.println(res);  
        return res;  
    }  

	// 匹配字符串
	public static int[] matchString(String str, String begin_str, String end_str) {
		int result = -1;
		// 查找匹配字符串的起始位置
		int begin_index = str.indexOf(begin_str);
		if (begin_index == -1) {
			return new int[] { begin_index, result };
		}
		char[] str_arr = str.toCharArray();
		int begin_str_len = begin_str.length();
		int end_str_len = end_str.length();
		int depth = 0;
		int i = begin_index;
		for (; i < str_arr.length; i++) {
			String test_end_str = str.substring(i, i + end_str_len);
			if (test_end_str.equals(end_str)) {
				depth--;
				if (depth == 0) {
					result = i;
					break;
				}
			}
			String test_begin_str = str.substring(i, i + begin_str_len);
			if (test_begin_str.equals(begin_str)) {
				depth++;
			}
		}
		return new int[] { begin_index, result };
	}

	// 读取配置文件
	public static String readPropertiesFile(String filename, String item) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(filename);
			properties.load(inputStream);
			inputStream.close(); // 关闭流
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = properties.getProperty(item);
		return result;
	}

	// 读取文本文件
	public static String readTxtFile(String filePath) {
		String result = "";
		try {
			String encoding = "UTF8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				read.close();
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	//写入文本文件
    public static void WriteStringToFile5(String content, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            String s = content;
            fos.write(s.getBytes());
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	// 解析路由
	public static void dispatchUri(ServletContext ctx, HttpServletRequest request, HttpServletResponse response) {
		// 解析路由
		String url = request.getServletPath().substring(0, request.getServletPath().lastIndexOf(".do")).substring(1);
		String url_file_path = ctx.getAttribute("CONF_FOLDER") + "url.txt";
		// 获取URL对应的控制器
		String Controller_path = CommonFunction.readPropertiesFile(url_file_path, url);
		// 空路径读取默认控制器
		if (null == Controller_path) {
			Controller_path = CommonFunction.readPropertiesFile(url_file_path, "default");
		}
		// 解析控制器路径，加载控制器
		String controller_arr[] = Controller_path.split(":");
		String controller_uri = controller_arr[0];
		String action_uri = controller_arr[1];
		Class<ControllerInterface> class_controller = null;
		try {
			class_controller = extracted("webapp", controller_uri);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			//尝试加载系统控制器
			try {
				class_controller = extracted("com.ruby.framework.controller", controller_uri);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 加载对应控制器的执行函数
		try {
			ControllerInterface _controller = class_controller.newInstance();
			// 将 上下文 请求 响应 视图 模型授权给控制器
			_controller.set(ctx, request, response);
			try {
				_controller.load(action_uri);
			} catch (IOException e) {
				ctx.log("加载控制器异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 返回请求调用的类 对请求处理的类必须在 webapp 包中
	 * 
	 * @param controller_uri
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Class<ControllerInterface> extracted(String base, String controller_uri) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<ControllerInterface> classs = (Class<ControllerInterface>) Class.forName(base+"." + controller_uri);
		return classs;
	}
}
