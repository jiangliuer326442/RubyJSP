package com.ruby.framework.function;

import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;  

import org.apache.commons.codec.binary.Base64;
  
  
/******************************************************************************* 
 * aes加解密算法 
 *  
 * @author jueyue 
 *  
 
  加密用的key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定 
  此处使用aes-128-cbc加密模式，key需要为16位。 
   也是使用0102030405060708
 */  
  
public class AesUtil {  
  
    // 加密  
    public static String encrypt(String ssrc, String skey) throws Exception {  
        if (skey == null) {  
        	System.out.print("key为空null");  
            return null;  
        }  
        // 判断key是否为16位  
        if (skey.length() != 16) {  
        	System.out.print("key长度不是16位");  
            return null;  
        }  
        byte[] raw = skey.getBytes();
        SecretKeySpec skeyspec = new SecretKeySpec(raw, "aes");  
        Cipher cipher = Cipher.getInstance("aes/cbc/pkcs5padding");//"算法/模式/补码方式"  
        IvParameterSpec iv = new IvParameterSpec(skey.getBytes());//使用cbc模式，需要一个向量iv，可增加加密算法的强度  
        cipher.init(Cipher.ENCRYPT_MODE, skeyspec, iv);  
        byte[] encrypted = cipher.doFinal(ssrc.getBytes());  
  
        return Base64.encodeBase64String(encrypted);//此处使用baes64做转码功能，同时能起到2次加密的作用。  
    }  
  
    // 解密  
    public static String decrypt(String ssrc, String skey) throws Exception {  
        try {  
            // 判断key是否正确  
            if (skey == null) {  
                System.out.print("key为空null");  
                return null;  
            }  
            // 判断key是否为16位  
            if (skey.length() != 16) {  
                System.out.print("key长度不是16位");  
                return null;  
            }  
            byte[] raw = skey.getBytes("ascii");  
            SecretKeySpec skeyspec = new SecretKeySpec(raw, "aes");  
            Cipher cipher = Cipher.getInstance("aes/cbc/pkcs5padding");  
            IvParameterSpec iv = new IvParameterSpec(skey  
                    .getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, skeyspec, iv);  
            byte[] encrypted1 = Base64.decodeBase64(ssrc);//先用baes64解密  
            try {  
                byte[] original = cipher.doFinal(encrypted1);  
                String originalstring = new String(original);  
                return originalstring;  
            } catch (Exception e) {  
                System.out.println(e.toString());  
                return null;  
            }  
        } catch (Exception ex) {  
        	System.out.println(ex.toString());  
            return null;  
        }  
    }  
  

}