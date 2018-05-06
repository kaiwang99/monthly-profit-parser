package com.nineforce.ecom.util;

public enum NFAccountTypeEnum {
	AMZN("Amazon"), EBAY("ebay"), WMT("Walmart"), ETSY("etsy");
	public String type;
		
	NFAccountTypeEnum(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type; 
	}
}

