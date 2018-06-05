package com.nineforce.ecom.csvparser;

public enum NFAccountTypeEnum {
	AMZN("Amazon"), EBAY("ebay"), WMT("Walmart"), ETSY("etsy"), PP("pp");
	public String type;
		
	NFAccountTypeEnum(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type; 
	}
}

