package com.nineforce.ecom.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class Util {
	
	/**
	 * Pass in locale and create  localeTypeStr -> stdTypeStr Map
	 * @param l
	 * @return
	 */
	public static HashMap<String, String> getAmznTypeMapByLocale(Locale l	) {
		HashMap<String, String> map = new HashMap<String, String>();
		ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle",l);
		 
		 for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			 String stdTypeStr = p.getTypeName();
			 String resBundleKey = stdTypeStr.replace(" ", "_");
			 String locTypeStr = messages.getString(resBundleKey);
			 
			 map.put(locTypeStr, stdTypeStr);
		 }
		
		return map;
	}
	

}
