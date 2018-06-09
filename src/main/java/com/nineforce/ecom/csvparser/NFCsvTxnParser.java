package com.nineforce.ecom.csvparser;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
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
	String csvInputFile;  // For ebay, this is pp file, which has more type info for transactions. 
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
		// initOutputFile();
		// initEnumMap();   This uses enumAccount, 
	}
	
	// each subclass init its own type enum and create this map
	abstract void initEnumMap();
	
	
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs) {
		this.enumAccount = enumAccount;
		this.cogs = cogs;
		
		// initOutputFile();  should call separately. 
	}
	
	/**
	 * create xlsx file and leave enough for summary 
	 * 
	 * @return 
	 */
	public void initOutputFile() {
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

	
	/**
	 * Write out the header line after summary section by the csv record, 
	 * adding COGS and Net two columns. 
	 * 
	 * @param hdrMap
	 */
	void writeOutHeaderLine(Map<String, Integer> hdrMap) {
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
		for (Map.Entry<String, Integer> entry : hdrMap.entrySet()) {
		    cellid = entry.getValue();
			cell = row.createCell(cellid);
	        cell.setCellValue(entry.getKey());		
		}
		cellid = hdrMap.size();
		cell = row.createCell(cellid++); cell.setCellValue("CGOS");
		cell = row.createCell(cellid++); cell.setCellValue("Nets");
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
