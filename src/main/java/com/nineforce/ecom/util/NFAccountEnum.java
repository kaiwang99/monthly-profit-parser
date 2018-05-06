package com.nineforce.ecom.util;

import static com.nineforce.ecom.util.NFAccountTypeEnum.*;

public enum NFAccountEnum {

	AMZN_TQS(NFAccountTypeEnum.AMZN, "tqs"), 
	AMZN_WSD(AMZN,"wsd"), 
	AMZN_SQB(AMZN,"sqb"), 
	AMZN_AD(NFAccountTypeEnum.AMZN,"ad"), 
	AMZN_HG(AMZN,"hg"), 
	
	EBAY_TQS(EBAY, "tqs"), 
	EBAY_SS(EBAY, "ss"),
	EBAY_WSD(EBAY, "wsd"),
	EBAY_VE(EBAY, "ve"),
	
	WMT_TQS(WMT, "tqs"),
	ETSY_TQS(ETSY, "tqs");   //this by SKU association even "WishDesigned" name.
	
	
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
	
	public static NFAccountEnum  getEnumType(NFAccountTypeEnum type, String acctName) {
		for(NFAccountEnum a: NFAccountEnum.values()) {
			if(a.getAccountType() == type) {
				if(a.getAccountName().equalsIgnoreCase(acctName))
						return a;
			}
		}
		return null; 
	}
	
	
	public String toString() {
		return accountType.toString() + "-" + accountName; 
	}
	
}
