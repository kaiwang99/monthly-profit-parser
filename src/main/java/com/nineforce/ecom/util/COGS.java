package com.nineforce.ecom.util;

// now only hold SKU, Account, Price in RMB
public class COGS {
	String sku;
	String desc;
	NineforceAccount acct; 
	float priceRMB;
	
	public COGS(String sku, String desc, NineforceAccount acct, float priceRMB) {
		this.sku = sku;
		this.desc = desc;
		this.acct = acct;
		this.priceRMB = priceRMB;
	}
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public NineforceAccount getAcct() {
		return acct;
	}
	public void setAcct(NineforceAccount acct) {
		this.acct = acct;
	}
	public float getPriceRMB() {
		return priceRMB;
	}
	public void setPriceRMB(float priceRMB) {
		this.priceRMB = priceRMB;
	} 
		
}
