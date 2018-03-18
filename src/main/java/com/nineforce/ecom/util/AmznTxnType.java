package com.nineforce.ecom.util;

// This record the types of in Amazon financial report txn

public enum AmznTxnType {

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
	
	AmznTxnType(String typeName) {
		this.typeName = typeName; 
	}
			
	public String getTypeName() {
		return this.typeName;
	}
	
    public static void main(String[] args) {
        for (AmznTxnType p : AmznTxnType.values())
           System.out.printf("Amazon Transaction Type:  %s is %s%n",
                             p, p.getTypeName());
    }
	
}
