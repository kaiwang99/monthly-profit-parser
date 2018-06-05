package com.nineforce.ecom.csvparser;

public enum AmznCsvHeaderEnum {
	DateTime("date/time"), 
	SettlementID("settlement id"),
	Type ("type"),	 
	OrderID("order id"),
	SKU("sku"),
	DESCRIPTION("description"),
	Quantity("quantity"),
	Marketplace("marketplace"),
	Fulfillment("fulfillment"),
	OrderCity("order city"), 
	OrderState("order state"), 
	OrderPostal("order postal"),
	ProductSales("product sales"), 
	ShippingCredits("shipping credits"),
	GiftWrapCredits("gift wrap credits"),
	PromotionalRebates("promotional rebates"),
	SalesTaxCollected("sales tax collected"),
	MarketplaceFacilitatorTax("Marketplace Facilitator Tax"),
	SellingFees("selling fees"),
	FbaFees("fba fees"), 
	OtherTransactionFees("other transaction fees"),
	Other("other"), 
	Total("total");
	
	private final String headerName;
	AmznCsvHeaderEnum(String headerName) {
		this.headerName = headerName;
	}
	
	public String getHeaderName() {
		return this.headerName;
	}
}
