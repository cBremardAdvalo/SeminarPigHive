package metier;

public class Factory {

	public static int buildAge(String userId){
		int intValue = Util.stringToInt(userId);
		return 18 + (intValue % 40);
	}
	public static int buildSexe(String userId){
		byte[] bytes = userId.getBytes();
		if(bytes.length>0){
			int a = bytes[0];
			int b = bytes[bytes.length-1];
			if(a==b){
				return 3;
			}else if(a>b){
				return 2;
			}else{
				return 1;
			}
		}
		return 3;
	}
}
