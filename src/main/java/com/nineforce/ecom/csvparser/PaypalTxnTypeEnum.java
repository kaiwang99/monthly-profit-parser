package com.nineforce.ecom.csvparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public static Logger logger = (Logger) LoggerFactory.getLogger(PaypalTxnTypeEnum.class);
	
	int KNOWN_TYPE_NUM = 5;  //
	
	
	private final String typeName; 
	
	PaypalTxnTypeEnum(String typeName) {
		this.typeName = typeName; 
	}
			
	public String getTypeName() {
		return this.typeName;
	}

	
	static public PaypalTxnTypeEnum getEnumType(String strType) {
		
		for (PaypalTxnTypeEnum p : PaypalTxnTypeEnum.values()) {
			if(strType.equals(p.getTypeName()))
				return p; 
		}
		
		logger.error("ERROR type[{}]",  strType);
		return PaypalTxnTypeEnum.ERROR;
	}

}
