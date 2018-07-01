package com.ruby.framework.function.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class FileOperation {
	  
	      /**
	       * 写文件
	       * 
	       * @param newStr
	       *            新内容
	       * @throws IOException
	       */
	      public static boolean writeTxtFile(String newStr, String path, String name) throws IOException {
	          // 先读取原有文件内容，然后进行写入操作
	          boolean flag = false;
	          
	          String filenameTemp = path + name;
	          File filename = new File(filenameTemp);
	          if (!filename.exists()) {
	              filename.createNewFile();
	          }
	          
	          String filein = newStr;
	          String temp = "";
	  
	          FileInputStream fis = null;
	          InputStreamReader isr = null;
	          BufferedReader br = null;
	  
	          FileOutputStream fos = null;
	          PrintWriter pw = null;
	          try {
	              // 文件路径
	              File file = new File(filenameTemp);
	              // 将文件读入输入流
	              fis = new FileInputStream(file);
	              isr = new InputStreamReader(fis);
	              br = new BufferedReader(isr);
	              StringBuffer buf = new StringBuffer();
	  
	              // 保存该文件原有的内容
	              for (int j = 1; (temp = br.readLine()) != null; j++) {
	                  buf = buf.append(temp);
	                 // System.getProperty("line.separator")
	                 // 行与行之间的分隔符 相当于“\n”
	                 buf = buf.append(System.getProperty("line.separator"));
	             }
	             buf.append(filein);
	 
	             fos = new FileOutputStream(file);
	             pw = new PrintWriter(fos);
	             pw.write(buf.toString().toCharArray());
	             pw.flush();
	             flag = true;
	         } catch (IOException e1) {
	             // TODO 自动生成 catch 块
	             throw e1;
	         } finally {
	             if (pw != null) {
	                 pw.close();
	             }
	             if (fos != null) {
	                 fos.close();
	             }
	             if (br != null) {
	                 br.close();
	             }
	             if (isr != null) {
	                 isr.close();
	             }
	             if (fis != null) {
	                 fis.close();
	             }
	         }
	         return flag;
     }
	
	public static String readFile(File file){
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
 
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block  
 
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
	}
	
	//递归删除文件夹  
	public static void deleteFile(File file) {  
	    if (file.exists()) {//判断文件是否存在  
	     if (file.isFile()) {//判断是否是文件  
	      file.delete();//删除文件   
	     } else if (file.isDirectory()) {//否则如果它是一个目录  
	      File[] files = file.listFiles();//声明目录下所有的文件 files[];  
	      for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件  
	       deleteFile(files[i]);//把每个文件用这个方法进行迭代  
	      }  
	      file.delete();//删除文件夹  
	     }  
	    }
	}
	
	  //下面这个函数用于将字节数组换成成16进制的字符串

	public static String byteArrayToHex(byte[] byteArray) {
	        String hs = "";   
	        String stmp = "";   
	        for (int n = 0; n < byteArray.length; n++) {   
	            stmp = (Integer.toHexString(byteArray[n] & 0XFF));   
	            if (stmp.length() == 1) {   
	                hs = hs + "0" + stmp;   
	            } else {   
	                hs = hs + stmp;   
	            }   
	            if (n < byteArray.length - 1) {   
	                hs = hs + "";   
	            }   
	        }   
	        // return hs.toUpperCase();   
	        return hs;

	      // 首先初始化一个字符数组，用来存放每个16进制字符

	      /*char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };

	 

	      // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

	      char[] resultCharArray =new char[byteArray.length * 2];

	      // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去

	      int index = 0;

	      for (byte b : byteArray) {

	         resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];

	         resultCharArray[index++] = hexDigits[b& 0xf];

	      }

	      // 字符数组组合成字符串返回

	      return new String(resultCharArray);*/

	}
	
	//计算文件md5值
	public static String fileMD5(String inputFile) throws IOException {
	      // 缓冲区大小（这个可以抽出一个参数）
	      int bufferSize = 256 * 1024;
	      FileInputStream fileInputStream = null;
	      DigestInputStream digestInputStream = null;
	      try {
	         // 拿到一个MD5转换器（同样，这里可以换成SHA1）
	         MessageDigest messageDigest =MessageDigest.getInstance("MD5");
	         // 使用DigestInputStream
	         fileInputStream = new FileInputStream(inputFile);
	         digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
	         // read的过程中进行MD5处理，直到读完文件
	         byte[] buffer =new byte[bufferSize];
	         while (digestInputStream.read(buffer) > 0);
	         // 获取最终的MessageDigest
	         messageDigest= digestInputStream.getMessageDigest();
	         // 拿到结果，也是字节数组，包含16个元素
	         byte[] resultByteArray = messageDigest.digest();
	         // 同样，把字节数组转换成字符串
	         return byteArrayToHex(resultByteArray);
	      } catch (NoSuchAlgorithmException e) {
	         return null;
	      } finally {
	         try {
	            digestInputStream.close();
	         } catch (Exception e) {
	         }
	         try {
	            fileInputStream.close();
	         } catch (Exception e) {
	         }
	      }
	   }
	
    /** 
     * 复制单个文件 
     *  
     * @param srcFileName 
     *            待复制的文件名 
     * @param descFileName 
     *            目标文件名 
     * @param overlay 
     *            如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyFile(String srcFileName, String destFileName,  
            boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
            return false;  
        } else if (!srcFile.isFile()) {  
            return false;  
        }  
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return false;  
                }  
            }  
        }  
  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
