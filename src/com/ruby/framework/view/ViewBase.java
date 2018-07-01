package com.ruby.framework.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.HtmlCompressor;

/**
 * 视图层操作类
 * 
 * @author fanghailiang
 *
 */
class ViewBase implements ViewInterface {
	private ServletContext context;
	private HashMap<String, String> replace_pairs;
	private String left_delimiter;
	private String right_delimiter;
	private String tmpl_prefix;
	private long cache_limits;
	
	public void init(){
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		//表单密钥
		String form_key = CommonFunction.readPropertiesFile(CONF_FOLDER + "security.txt", "form_key");
		this.assign("form_key", form_key);
	}

	public ViewBase(ServletContext context) {
		this.context = context;
		replace_pairs = new HashMap<String, String>();
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		left_delimiter = CommonFunction.readPropertiesFile(CONF_FOLDER + "tmpl.txt", "left_delimiter");
		right_delimiter = CommonFunction.readPropertiesFile(CONF_FOLDER + "tmpl.txt", "right_delimiter");
		tmpl_prefix = CommonFunction.readPropertiesFile(CONF_FOLDER + "tmpl.txt", "tmpl_prefix");
		cache_limits = Long.parseLong(CommonFunction.readPropertiesFile(CONF_FOLDER + "tmpl.txt", "cache_limits"))*1000;
	}

	public void assign(String key, String value) {
		// TODO Auto-generated method stub
		replace_pairs.put(key, value);
	}

	// 获取视图字符串
	public String display(String tmpl) {
		String folder = context.getAttribute("__ROOT__") + "view" + File.separator;
		String path = folder + (tmpl + tmpl_prefix).replace("\\", File.separator).replace("/", File.separator);
		String s = CommonFunction.readTxtFile(path);
		boolean is_included = true;
		while (is_included) {
			// 初始状态下不需要进行下一次的检测包含文件的循环
			is_included = false;
			Pattern p = Pattern.compile(left_delimiter + "(.*?)" + right_delimiter);
			Matcher m = p.matcher(s);
			while (m.find()) {
				String codes = m.group(1);
				// 引入包含文件
				if (codes.length() > 8 && codes.substring(0, 8).equals(" include")) {
					s = includeFile(folder, codes, s);
					// 引入包含文件后需要重新进行字符的替换
					is_included = true;
				}
				// if语句判断
				if (codes.length() > 3 && codes.substring(0, 3).equals(" if")) {
					s = ifRemove(codes, s);
				}
				// 替换字符串
				else if (codes.substring(0, 1).equals("=")) {
					s = setVariable(codes, s);
				}
			}
		}
		// 清空数据
		replace_pairs = new HashMap<String, String>();
		try {
			s = HtmlCompressor.compress(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public String display(String tmpl, boolean is_cache) {
		//缓存文件夹
		final String CACHE_FOLDER = context.getAttribute("__ROOT__").toString()+"cache"+File.separator;
		final String FILE_NAME = CommonFunction.EncoderByMd5(tmpl);
		if(is_cache){
			//如果缓存文件存在并且在有效期内，则读取缓存文件
			File f = new File(CACHE_FOLDER+FILE_NAME);
			if(f.exists() && ((System.currentTimeMillis()-f.lastModified())<cache_limits)){
				String s = CommonFunction.readTxtFile(CACHE_FOLDER+FILE_NAME);
				return s;
			}
		}
		String s = this.display(tmpl);
		if(is_cache){
			CommonFunction.WriteStringToFile5(s, CACHE_FOLDER+FILE_NAME);
		}
		return s;
	}
	
	public String display(String tmpl, long cache_limits) {
		//缓存文件夹
		final String CACHE_FOLDER = context.getAttribute("__ROOT__").toString()+"cache"+File.separator;
		final String FILE_NAME = CommonFunction.EncoderByMd5(tmpl);
		//如果缓存文件存在并且在有效期内，则读取缓存文件
		File f = new File(CACHE_FOLDER+FILE_NAME);
		if(f.exists() && ((System.currentTimeMillis()-f.lastModified())<cache_limits*1000)){
			String s = CommonFunction.readTxtFile(CACHE_FOLDER+FILE_NAME);
			return s;
		}
		String s = this.display(tmpl);
		CommonFunction.WriteStringToFile5(s, CACHE_FOLDER+FILE_NAME);
		return s;
	}

	/**
	 * 文件包含
	 * 
	 * @param folder
	 *            根目录
	 * @param path
	 *            原始文件路径
	 * @param codes
	 *            指令码
	 * @param s
	 *            待处理字符串
	 * @return
	 */
	protected String includeFile(String folder, String codes, String s) {
		String key = codes.substring(9, codes.length() - 1);
		String new_path = (folder + key + tmpl_prefix).replace("\\", File.separator).replace("/", File.separator);
		String included_html = CommonFunction.readTxtFile(new_path);
		s = s.replace(left_delimiter + codes + right_delimiter, included_html);
		return s;
	}

	/**
	 * 替换变量
	 * 
	 * @param codes
	 *            指令字符串
	 * @param s
	 *            待处理字符串
	 * @return 处理结果
	 */
	protected String setVariable(String codes, String s) {
		String key = codes.substring(2, codes.length() - 1);
		s = s.replace(left_delimiter + codes + right_delimiter, replace_pairs.get(key));
		return s;
	}

	/**
	 * if语句对页面部分内容进行截取
	 * 
	 * @param codes 指令字符串
	 * @param s 待处理字符串
	 * @return 处理结果
	 */
	protected String ifRemove(String codes, String s) {
		// 从code中提取条件字符串
		int[] condition_arr = CommonFunction.matchString(codes, "(", ")");
		String condition_str = codes.substring(condition_arr[0] + 1, condition_arr[1]);

		// 对条件字符串进行判断的代码。。。
		String execute_body = s.substring(s.indexOf(codes));
		int[] execute_arr = CommonFunction.matchString(execute_body, "{", "}");
		String execute_body2 = execute_body.substring(execute_arr[1] + 1);
		// 根据条件判断的结果进行取舍
		StringBuffer s2 = new StringBuffer(s);
		int base = s.indexOf(codes) + execute_arr[1] + 1;
		if (judgeContion(condition_str)) {
			// 删除else板块
			if (execute_body2.substring(0, 4).equals("else")) {
				int[] execute_arr2 = CommonFunction.matchString(execute_body2, "{", "}");
				s2.delete(execute_arr2[0] + base - 6 - left_delimiter.length(),
						execute_arr2[1] + base + 2 + right_delimiter.length());
			} else {
				s2.delete(base - 2 - left_delimiter.length(), base + 1 + right_delimiter.length());
			}
			s2.delete(s.indexOf(codes) - left_delimiter.length(),
					s.indexOf(codes) - left_delimiter.length() + codes.length() + 2 + right_delimiter.length());
		} else {
			// 清除if块的内容
			if (execute_body2.substring(0, 4).equals("else")) {
				int[] execute_arr2 = CommonFunction.matchString(execute_body2, "{", "}");
				s2.delete(base + execute_arr2[1] - 1 - left_delimiter.length(),
						base + execute_arr2[1] + 2 + right_delimiter.length());
				s2.delete(s.indexOf(codes) - left_delimiter.length(),
						s.indexOf(codes) + execute_arr[1] + 7 + right_delimiter.length());
			} else {
				s2.delete(s.indexOf(codes) - left_delimiter.length(),
						s.indexOf(codes) + execute_arr[1] + 2 + right_delimiter.length());
			}

		}
		s = s2.toString();
		return s;
	}

	protected boolean judgeContion(String condition_str) {
		boolean result;
		ArrayList<String> kuohao = new ArrayList<String>();
		int i = 1;
		// 括号合并
		while (true) {
			// 先对括号里内容进行等价替换
			int[] kuohao_arr = CommonFunction.matchString(condition_str, "(", ")");
			if (kuohao_arr[1] != -1) {
				kuohao.add(condition_str.substring(kuohao_arr[0] + 1, kuohao_arr[1]));
				StringBuffer sb = new StringBuffer(condition_str);
				sb.replace(kuohao_arr[0], kuohao_arr[1] + 1, "kuohao_" + i);
				condition_str = sb.toString();
				i++;
			} else {
				break;
			}
		}
		// 或处理
		if (condition_str.indexOf(" || ") > 0) {
			result = false;
			boolean logic_huo = false;
			String[] huo = condition_str.split(" \\|\\| ");
			for (i = 0; i < huo.length; i++) {
				if (huo[i].length()>7 && huo[i].substring(0, 7).equals("kuohao_")) {
					int id = Integer.parseInt(huo[i].substring(7));
					huo[i] = kuohao.get(id - 1);
				}
				boolean logic_qie = true;
				String[] qie = huo[i].split(" \\&\\& ");
				for (int j = 0; j < qie.length; j++) {
					String[] dengshi = null;
					String yunsuan = null;
					// 表达式按照大雨、等于、小于分割
					if (qie[j].indexOf(">=") > 0) {
						dengshi = qie[j].split(">=");
						yunsuan = ">=";
					} else if (qie[j].indexOf("<=") > 0) {
						dengshi = qie[j].split("<=");
						yunsuan = "<=";
					} else if (qie[j].indexOf("==") > 0) {
						dengshi = qie[j].split("==");
						yunsuan = "==";
					}else if (qie[j].indexOf(">") > 0) {
						dengshi = qie[j].split(">");
						yunsuan = ">";
					} else if (qie[j].indexOf("<") > 0) {
						dengshi = qie[j].split("<");
						yunsuan = "<";
					}
					//判断右侧数据类型
					if(Float.parseFloat(dengshi[1])>0){
						//右侧数据为数值型
						Float left = Float.parseFloat(replace_pairs.get(dengshi[0]));
						Float right = Float.parseFloat(dengshi[1]);
						switch (yunsuan) {
						case ">":
							if(left <= right){
								logic_qie = false;
							}
							break;
						case "<":
							if(left >= right){
								logic_qie = false;
							}						
							break;
						case ">=":
							if(left < right){
								logic_qie = false;
							}
							break;
						case "<=":
							if(left > right){
								logic_qie = false;
							}						
							break;
						case "==":
							if(left - right != 0){
								logic_qie = false;
							}
							break;

						default:
							break;
						}
						if(logic_qie == false){
							break;
						}
					}
				}
				if(logic_qie){
					logic_huo = true;
				}
				if(logic_huo){
					result = true;
					break;
				}
			}
		}else if(condition_str.indexOf(" && ") > 0){
			result = true;
			boolean logic_qie = true;
			String[] qie = condition_str.split(" \\&\\& ");
			for (i = 0; i < qie.length; i++) {
				if (qie[i].length()>7 && qie[i].substring(0, 7).equals("kuohao_")) {
					int id = Integer.parseInt(qie[i].substring(7));
					qie[i] = kuohao.get(id - 1);
				}
				boolean logic_huo = false;
				String[] huo = qie[i].split(" \\|\\| ");
				for (int j = 0; j < huo.length; j++) {
					String[] dengshi = null;
					String yunsuan = null;
					// 表达式按照大雨、等于、小于分割
					if (huo[j].indexOf(">=") > 0) {
						dengshi = huo[j].split(">=");
						yunsuan = ">=";
					} else if (huo[j].indexOf("<=") > 0) {
						dengshi = huo[j].split("<=");
						yunsuan = "<=";
					} else if (huo[j].indexOf("==") > 0) {
						dengshi = huo[j].split("==");
						yunsuan = "==";
					}else if (huo[j].indexOf(">") > 0) {
						dengshi = huo[j].split(">");
						yunsuan = ">";
					} else if (huo[j].indexOf("<") > 0) {
						dengshi = huo[j].split("<");
						yunsuan = "<";
					}
					//判断右侧数据类型
					if(Float.parseFloat(dengshi[1])>=0){
						//右侧数据为数值型
						Float left = Float.parseFloat(replace_pairs.get(dengshi[0]));
						Float right = Float.parseFloat(dengshi[1]);
						switch (yunsuan) {
						case ">":
							if(left > right){
								logic_huo = true;
							}
							break;
						case "<":
							if(left < right){
								logic_huo = true;
							}						
							break;
						case ">=":
							if(left >= right){
								logic_huo = true;
							}
							break;
						case "<=":
							if(left <= right){
								logic_huo = true;
							}						
							break;
						case "==":
							if(left-right==0){
								logic_huo = true;
							}
							break;

						default:
							break;
						}
						if(logic_huo){
							break;
						}
					}
				}
				if(!logic_huo){
					logic_qie = false;
				}
				if(!logic_qie){
					result = false;
					break;
				}
			}
		}else{
			result = false;
			String[] dengshi = null;
			String yunsuan = null;
			// 表达式按照大雨、等于、小于分割
			if (condition_str.indexOf(">=") > 0) {
				dengshi = condition_str.split(">=");
				yunsuan = ">=";
			} else if (condition_str.indexOf("<=") > 0) {
				dengshi = condition_str.split("<=");
				yunsuan = "<=";
			} else if (condition_str.indexOf("==") > 0) {
				dengshi = condition_str.split("==");
				yunsuan = "==";
			} else if (condition_str.indexOf(">") > 0) {
				dengshi = condition_str.split(">");
				yunsuan = ">";
			} else if (condition_str.indexOf("<") > 0) {
				dengshi = condition_str.split("<");
				yunsuan = "<";
			}
			//判断右侧数据类型
			if(Float.parseFloat(dengshi[1])>=0){
				//右侧数据为数值型
				Float left = Float.parseFloat(replace_pairs.get(dengshi[0]));
				Float right = Float.parseFloat(dengshi[1]);
				switch (yunsuan) {
				case ">":
					if(left > right){
						result = true;
					}
					break;
				case "<":
					if(left < right){
						result = true;
					}						
					break;
				case "==":
					if(left - right == 0){
						result = true;
					}
					break;
				case ">=":
					if(left >= right){
						result = true;
					}
					break;
				case "<=":
					if(left <= right){
						result = true;
					}
					break;
				default:
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 设置分隔符
	 * 
	 * @param left_delimiter
	 *            左侧分解符
	 * @param right_delimiter
	 *            右侧分解符
	 */
	protected void setDelimiter(String left_delimiter, String right_delimiter) {
		this.left_delimiter = left_delimiter;
		this.right_delimiter = right_delimiter;
	}

	/**
	 * 设置分隔符
	 * 
	 * @param left_delimiter
	 *            左侧分解符
	 * @param right_delimiter
	 *            右侧分解符
	 */
	protected void setPrefix(String tmpl_prefix) {
		this.tmpl_prefix = tmpl_prefix;
	}
}
