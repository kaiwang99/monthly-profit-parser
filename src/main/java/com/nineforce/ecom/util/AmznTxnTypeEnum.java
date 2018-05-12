package com.nineforce.ecom.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This record the types of in Amazon financial report txn in en US. 
 * 
 * The string of type name, getTypeName() then replace " " with "_"  are the 
 * key for resource bundle for other countries. 
 * 
 * @author kaiwang
 *
 */

public enum AmznTxnTypeEnum {

	ADJUSTMENT("Adjustment"), 
	CHARGEBACK_REFUND("Chargeback Refund"),
	FBA_INVENTORY_FEE("FBA Inventory Fee"), 
	FBA_CUSTOMER_RETURN_FEE("FBA Customer Return Fee"),
	
	ORDER("Order"), 
	REFUND("Refund"),
	SHIPPING_SERVICES("Shipping Services"), 
	SERVICE_FEE("Service Fee"), 
	TRANSFER("Transfer"),
	
	ERROR("Error");

	
	int KNOWN_TYPE_NUM = 9;  //
	
	private final String typeName; 
	
	AmznTxnTypeEnum(String typeName) {
		this.typeName = typeName; 
	}
			
	public String getTypeName() {
		return this.typeName;
	}
	
	static public AmznTxnTypeEnum getEnumType(String strType) {
		for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			if(strType.equals(p.getTypeName()))
				return p; 
		}
		System.out.println("ERROR type[" + strType + "]");
		return AmznTxnTypeEnum.ERROR;
	}
	
	/**
	 * Get AmznTxnTypeEnum by Locale. Need to search by curLocale string, then map back
	 * to the type. 
	 * 
	 * @param strType - the string in the target language
	 * @param curLocale
	 * @return
	 */
	static public AmznTxnTypeEnum getEnumType(String strType, Locale curLocale) {
			String resourceKey = null, targetType = null;
	     ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle",curLocale);
	      
		for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			
			resourceKey = p.getTypeName().replace(" ", "_");
			targetType = messages.getString(resourceKey);
			
			if(strType.equals(targetType))
				return p; 
		}
		System.out.println("ERROR type[" + strType + "]  Reousce Key [" + resourceKey 
				+ "] targetType: " + targetType);
		return AmznTxnTypeEnum.ERROR;
	}
	
    public static void main(String[] args) {
        for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values())
           System.out.printf("Amazon Transaction Type:  %s is %s%n",
                             p, p.getTypeName()); 
    }
	
}
