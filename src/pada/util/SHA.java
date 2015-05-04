package pada.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
public class SHA{
	public static byte[] digest(String content){
 		MessageDigest md = null;
		byte[] result = null;
		try{
			md = MessageDigest.getInstance("SHA-256");
			md.update(content.getBytes("utf-8"));
			result = md.digest();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return result;
	}
	public static String digestToHex(String content){
		StringBuffer result = new StringBuffer("");
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance("SHA-256");
			md.update(content.getBytes("utf-8"));
			byte[] code = md.digest();
			String hexStr;
			for(int i = 0; i < code.length; i++){
				hexStr = Integer.toHexString(code[i]);
				if(hexStr.length() < 2){
					hexStr = "0" + hexStr;
				}else{
					hexStr = hexStr.substring(hexStr.length() - 2);
				}
				result.append(hexStr);
			}
			return result.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
	}
	public static byte[] digest(byte[] content){
 		MessageDigest md = null;
		byte[] result = null;
		try{
			md = MessageDigest.getInstance("SHA-256");
			md.update(content);
			result = md.digest();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return result;
	}
	public static String digestToHex(byte[] content){
		StringBuffer result = new StringBuffer("");
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance("SHA-256");
			md.update(content);
			byte[] code = md.digest();
			String hexStr;
			for(int i = 0; i < code.length; i++){
				hexStr = Integer.toHexString(code[i]);
				if(hexStr.length() < 2){
					hexStr = "0" + hexStr;
				}else{
					hexStr = hexStr.substring(hexStr.length() - 2);
				}
				result.append(hexStr);
			}
			return result.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			return null;
		}
	}
}