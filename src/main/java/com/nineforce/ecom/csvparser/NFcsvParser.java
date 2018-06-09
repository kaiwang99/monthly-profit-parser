package com.nineforce.ecom.csvparser;

import java.io.IOException;
import java.util.Locale;

public interface NFcsvParser {
	
	
	/**
	 * Return the real records parsed. 
	 * @return
	 */
	public int parseFile() throws IOException ; 
	
	
	/**
	 * Set COGS and account for order/sku/ASIN level profit. 
	 * However, if it is not set or to null, parser should still generate summary of each type,
	 * even though less useful
	 * 
	 * @param enumAccount
	 * @param cogs
	 * @return
	 */
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs);
	
	/**
	 * Set locale for EU countried. The file header, type string, and currency are localized. 
	 * @param locale
	 */
	//public void setLocale(Locale locale);
	
	public void displaySummary();


	public void initOutputFile();
}
