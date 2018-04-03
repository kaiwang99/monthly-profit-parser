package com.nineforce.ecom.util;

public enum AmznCsvHeaderEnum {
	DateTime("date/time"), 
	SettlementID("settlement id"); 
		//	+ "
		//	+ "	type	 order id	sku	description	quantity	marketplace	fulfillment	order city	order state	order postal	product sales	shipping credits	gift wrap credits	promotional rebates	sales tax collected	Marketplace Facilitator Tax	selling fees	fba fees	other transaction fees	other	total
	
	private final String headerName;
	AmznCsvHeaderEnum(String headerName) {
		this.headerName = headerName;
	}
}
