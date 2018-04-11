package com.nineforce.ecom.util;

// This record the types of in Amazon financial report txn

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
	
    public static void main(String[] args) {
        for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values())
           System.out.printf("Amazon Transaction Type:  %s is %s%n",
                             p, p.getTypeName()); 
    }
	
}
