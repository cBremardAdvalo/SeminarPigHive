package com.ensai.dataGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ihm.Activity01;
import ihm.Activity02;
import ihm.Activity03;
import ihm.Activity04;
import ihm.Activity05;
import ihm.Activity06;
import metier.GlobalStat;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final String KEY_APP_NAME = "appName";
	public static final String KEY_TCHAT_PICTURE = "backgroundPath";
	public static final String KEY_MONTH_PRICE = "monthPrice";
	public static final String KEY_FLOWER_PRICE = "kissPrice";
	public static final String KEY_POPULARITY = "popularity";
	public static final String KEY_CHURN = "churn";
	public static final String KEY_STAGING_PATH = "staging";

	public static void main( String[] args )
    {
		  
    	System.out.println("START");
    	Map<String,String> globalMap = new HashMap<String,String>();
    	globalMap.put(KEY_MONTH_PRICE,"10.5");
    	globalMap.put(KEY_APP_NAME,"Ensai Tchat");
    	globalMap.put(KEY_FLOWER_PRICE,"0.50");
    	globalMap.put(KEY_POPULARITY,"100");
    	globalMap.put(KEY_CHURN,"3");
//    	globalMap.put(KEY_STAGING_PATH,"/home/corentin/Bureau/test");
    	globalMap.put(KEY_STAGING_PATH,"C:\\Users\\pORTABLE\\Downloads\\testSeminarPigHive");
//    	new Activity01().launch(globalMap);
//    	new Activity02().launch(globalMap);
//    	/*new Activity03().launch(globalMap);*/
//    	new Activity04().launch(globalMap);
//    	new Activity05().launch(globalMap);
    	GlobalStat gs = GlobalStat.getInstance(
    			globalMap.get(KEY_APP_NAME),
    			globalMap.get(KEY_TCHAT_PICTURE),
    			Double.parseDouble(globalMap.get(KEY_MONTH_PRICE)),
    			Double.parseDouble(globalMap.get(KEY_FLOWER_PRICE)),
    			Double.parseDouble(globalMap.get(KEY_POPULARITY)),
    			Double.parseDouble(globalMap.get(KEY_CHURN)),
    			new File(globalMap.get(KEY_STAGING_PATH)));
    	
    	new Activity06(gs).run();
    	
    	System.out.println("STOP");
	  }
}
