package com.nineforce.ecom.util;

// now only hold SKU, Account, Price in RMB
public class COGS {
	
	
	String sku;
	String desc;
	NFAccountEnum acct; 
	float priceRMB;
	
	public COGS(String sku, String desc, NFAccountEnum acct, float priceRMB) {
		this.sku = sku;
		this.desc = desc;
		this.acct = acct;
		this.priceRMB = priceRMB;
	}
	
	/**
	 * Get Master SKU
	 * @return
	 */
	public String getSku(NFAccountEnum acct) {
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
	public NFAccountEnum getAcct() {
		return acct;
	}
	public void setAcct(NFAccountEnum acct) {
		this.acct = acct;
	}
	public float getPriceRMB() {
		return priceRMB;
	}
	public void setPriceRMB(float priceRMB) {
		this.priceRMB = priceRMB;
	} 
		
}
