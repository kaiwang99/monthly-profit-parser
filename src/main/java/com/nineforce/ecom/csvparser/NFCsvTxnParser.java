package com.nineforce.ecom.csvparser;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * I don't know whether it is a good idea to have base class. 
 * 
 * It seems only a few common features can be in this file
 * 
 * 
 * @author kaiwang
 *
 */

public abstract class NFCsvTxnParser implements NFcsvParser {
	String csvInputFile;
	String xlsxOutputFile;
	XSSFWorkbook workbook;
	XSSFSheet spreadsheet;
	int rowid;
	
	XSSFRow frontRow[];
	final int SUMMARY_LEN = 16;   //summar section of the xlsx file
	
	
	NFAccountEnum enumAccount;
	COGS cogs;
	
	
	NFCsvTxnParser(String csvFile) {
		this.csvInputFile = csvFile;
		initOutputFile();
		initEnumMap();
	}
	
	// each subclass init its own type enum and create this map
	abstract void initEnumMap();
	
	
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs) {
		this.enumAccount = enumAccount;
		this.cogs = cogs;
		
		initOutputFile();
	}
	
	/**
	 * create xlsx file and leave enough for summary 
	 * 
	 * @return 
	 */
	private void initOutputFile() {
		int lastDotIndex = csvInputFile.lastIndexOf('.');
		xlsxOutputFile = csvInputFile.substring(0, lastDotIndex) + ".xlsx";
		
		workbook = new XSSFWorkbook();
		spreadsheet = workbook.createSheet(enumAccount.toString());
		
		//Create row object
		frontRow = new XSSFRow[SUMMARY_LEN];
		for (int i=0; i<frontRow.length; i++) 
			frontRow[i] = spreadsheet.createRow(i);
		
		rowid = SUMMARY_LEN;
	}

	@Override
	public int parseFile() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void displaySummary() {
		// TODO Auto-generated method stub
		
	}
	
}
