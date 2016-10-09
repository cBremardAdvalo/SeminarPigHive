package metier;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	
	
	public static String md5(String s){
		String output = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(),0,s.length());
			output =  new BigInteger(1,m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return output;
	}

}
