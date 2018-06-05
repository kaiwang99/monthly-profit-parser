package com.nineforce.ecom.csvparser;

/**
 * Paypal type in the all transaction report. Since we are only in US selling, no i18n. 
 * 
 * If need i18n, please refer AmznTxnTypeEnum. 
 * 
 * @author kaiwang
 *
 */


public enum PaypalTxnTypeEnum {

	CANCEL_HOLD("Cancellation of Hold for Dispute Resolution"), 
	EBAY_PAYMNET("eBay Auction Payment"),
	HOLD_PAYMENT("Hold on Balance for Dispute Investigation"), 
	MOBILE_PAYMENT("Mobile Payment"),
	PAYMENT_REFUND("Payment Refund"),
	ERROR("Error");
	
	int KNOWN_TYPE_NUM = 5;  //
	
	
	private final String typeName; 
	
	PaypalTxnTypeEnum(String typeName) {
		this.typeName = typeName; 
	}
			
	public String getTypeName() {
		return this.typeName;
	}



}
