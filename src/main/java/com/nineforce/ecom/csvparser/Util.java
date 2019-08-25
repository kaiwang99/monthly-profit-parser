package com.nineforce.ecom.csvparser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class Util {
	public static final double USDRMB_PURCHASE = 6.25;  //For COGS calculation. Change Quarterly or longer
	public static final double EURMB_PURCHASE = 7.29;
	public static final double GBPRMB_PURCHASE = 8.22;
	
	public static final double USDRMB_CURRENT = 6.80;
	public static final double EURMB_CURRENT = 7.35;   //current rate change monthly 
	public static final double GBPRMB_CURRENT = 8.85;
	
	public static final double ADMIN_OVERHEAD = 0.3;
	public static final double BONUS_RATE = 0.03;
	
	public static final Locale US_LOCALE = new Locale("en", "US");
	public static final Locale UK_LOCALE = new Locale("en", "UK");
	public static final Locale DE_LOCALE = new Locale("de", "DE");
	public static final Locale FR_LOCALE = new Locale("fr", "FR");
	public static final Locale IT_LOCALE = new Locale("it", "IT");
	public static final Locale ES_LOCALE = new Locale("es", "ES");
	
	private final static double US_SHIP_COST = 3.9; 
	public final static double RUS_ROP_SHIP_COST = 9.6; 
	
	/**
	 * Pass in locale and create  localeTypeStr -> stdTypeStr Map
	 * @param l
	 * @return
	 */
	public static HashMap<String, String> getAmznLocTypeStdTypeMap(Locale l	) {
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
	
	
	/**
	 * Pass in locale and create  stdTypeStr -> localeTypeStr  Map
	 * @param l
	 * @return
	 */
	public static HashMap<String, String> getAmznStdTypeLocTypeMap(Locale l	) {
		HashMap<String, String> map = new HashMap<String, String>();
		ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle",l);
		 
		 for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			 String stdTypeStr = p.getTypeName();
			 String resBundleKey = stdTypeStr.replace(" ", "_");
			 String locTypeStr = messages.getString(resBundleKey);
	System.out.println("key=" + resBundleKey + " locStr=" + locTypeStr);		 
			 map.put(stdTypeStr, locTypeStr);
		 }
		return map;
	}
	
	/**
	 * return CGOS rate depends on locale. 
	 * TODO Can be by ocean, air, or combination. 
	 * 
	 * NOTE: This is hard to be accurate. So only use getCurrentRate() below. 
	 * @param l
	 * @return
	 */
	public static double getCOGSrate(Locale l) {
		double rate = EURMB_PURCHASE;
		
		switch (l.getCountry()) {
		case "US": rate = USDRMB_PURCHASE; break;
		case "UK": rate = GBPRMB_PURCHASE; break;
		default: //rest are Euro rate.
		}
		return rate;
	}
	
	
	public static double getCurrentRate(Locale l) {
		double rate = EURMB_CURRENT;
		
		switch (l.getCountry()) {
		case "US": rate = USDRMB_CURRENT; break;
		case "UK": rate = GBPRMB_CURRENT; break;
		default: //rest are Euro rate.
		}
		return rate;
	}
	
	public static String currencyByLocale(Locale l) {
		String currency = "EURO";
		
		switch (l.getCountry()) {
		case "US": currency = "USD"; break;
		case "UK": currency = "GBP"; break;
		default: //rest are Euro rate.
		}
		return currency;
	}
	
	// round to 2 digits after decimal 
	public static double round(double num) {
		return Math.round(num*100)/100.00;
	}
	
	public static double parseDouble(String s) {
		if (s== null) return 0;
		
		String str = s.trim();
		if (str.length() == 0 )  return 0;
		
		// use NumberFormat to handle different thousand separator and
		// decimal separator correctly. No p.replaceAll(",",".")
		
		//TODO  change Locale when needed. Probably another input in the variable. 
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		Number number = null;
		try {
			number = format.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double d = number.doubleValue();
		
		return d;
	}
	
	/**
	 * Before we get shipping cost from Teapplix for each order, this is an estimate by SKU
	 * 
	 */
	public static double getShippingCost(String aSKU) {
		double cost = US_SHIP_COST;
		
		// rope light cost more, like 0-RUS18-ROP19-6M4MD-013FLAT
		if (aSKU.contains("RUS") && aSKU.contains("ROP"))
			cost = RUS_ROP_SHIP_COST;		
		return cost;
	}
	
}
