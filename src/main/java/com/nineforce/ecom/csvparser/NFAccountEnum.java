package com.nineforce.ecom.csvparser;

import static com.nineforce.ecom.csvparser.NFAccountTypeEnum.*;

public enum NFAccountEnum {

	AMZN_TQS(NFAccountTypeEnum.AMZN, "tqs"), 
	AMZN_WSD(AMZN,"wsd"), 
	AMZN_SQB(AMZN,"sqb"), 
	AMZN_AD(NFAccountTypeEnum.AMZN,"ad"), 
	AMZN_HG(AMZN,"hg"), 
	AMZN_EMC(AMZN,"emc"), 
	
	EBAY_TQS(EBAY, "tqs"), 
	EBAY_SS(EBAY, "ss"),
	EBAY_WSD(EBAY, "wsd"),
	EBAY_VE(EBAY, "ve"),
	
	PP_TQS(PP, "tqs"), 
	PP_SS(PP, "ss"),
	PP_WSD(PP, "wsd"),
	PP_VE(PP, "ve"),
	
	WMT_TQS(WMT, "tqs");
	
	//ETSY_TQS(ETSY, "tqs");   //this by SKU association even "WishDesigned" name.
	
	static String WMT_FEE = "FEE";   //skip Walmart-TQS-fees file name
	
	final String accountName;
	final NFAccountTypeEnum accountType;
	
	NFAccountEnum(NFAccountTypeEnum type, String name) {
		this.accountName = name; 
		this.accountType = type; 
	}
	
	public String getAccountName() {
		return this.accountName;
	}
	
	public NFAccountTypeEnum getAccountType() {
		return this.accountType;
	}
	
	/**
	 * File names should be in format Type-Name. For example 
	 * 		amazon-ad
	 * 		ebay-ve
	 * 		wmt-tqs
	 * 		etsy-tqs
	 * 		
	 * @param type
	 * @param acctName
	 * @return
	 */
	public static NFAccountEnum  getEnumType(NFAccountTypeEnum type, String acctName) {
		for(NFAccountEnum a: NFAccountEnum.values()) {
			if(a.getAccountType() == type) {
				if(a.getAccountName().equalsIgnoreCase(acctName))
						return a;
			}
		}
		return null; 
	}
	
	/**
	 * Pass in a string, and check if it may match any type. 
	 * Input String or File names should be in format Type-Name. For example 
	 * 		amazon-ad
	 * 		ebay-ve
	 * 		wmt-tqs
	 * 		etsy-tqs
	 * 
	 * @param aString
	 * @return
	 */
	public static NFAccountEnum  getEnumType(String aString) {
		String upperAString = aString.toUpperCase();
		
		for(NFAccountEnum a: NFAccountEnum.values()) {
			String upperAcctStr = a.toString().toUpperCase();		
			
			if(upperAString.contains(WMT_FEE))
				continue; 
			
			if(upperAString.contains(upperAcctStr)) 
				return a;
		}
		return null; 
	}
	
	
	public String toString() {
		return accountType.toString() + "-" + accountName; 
	}
	
}
