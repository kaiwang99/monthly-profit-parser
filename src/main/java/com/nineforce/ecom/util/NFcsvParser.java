package com.nineforce.ecom.util;

import java.io.IOException;

public interface NFcsvParser {
	
	
	/**
	 * Return the real records parsed. 
	 * @return
	 */
	public int parseFile() throws IOException ; 
	
	
	/**
	 * Set COGS and account for order/sku/ASIN level profit. 
	 * However, if it is not set or to null, parser should still generate summary of each type,
	 * even though less usefull
	 * 
	 * @param enumAccount
	 * @param cogs
	 * @return
	 */
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs);
	
	public void displaySummary();
}
