package com.nineforce.ecom.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// parse amazon csv file from reports. mostly use for the month for bonus calculation
// Hold data in csvRecord and also by type in enummap 

public class AmznCSVTxnParser implements NFcsvParser {
	final int SUMMARY_LEN = 16;   //summar section of the xlsx file
	final String HEADER = "header";   // just a flage to indicate header of csv. 
	
	String csvInputFile;
	String xlsxOutputFile;
	XSSFWorkbook workbook;
	XSSFSheet spreadsheet;
	int rowid;
	
	COGS cogs;
	NFAccountEnum enumAccount;
	XSSFRow frontRow[];
	
	float totalCOGS = (float) 0.0;
	float totalNet = (float) 0.0; 
	
	EnumMap<AmznTxnTypeEnum, AmznTxnTypeSum> txnByTypes; 
	
	public AmznCSVTxnParser(String csvFile) {
		this.csvInputFile = csvFile;
		txnByTypes = new EnumMap<> (AmznTxnTypeEnum.class);
		
		// initiate the map, so don't check null in parse CSVRecord loop
		initEnumMap();
	}

	void initEnumMap() {
		for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			txnByTypes.put(p, new AmznTxnTypeSum(p));
		}
	}
	
	/**
	 * create xlsx file and leave enough for summary 
	 * 
	 * @return 
	 */
	void initOutputFile() {
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
	 * Write content of xlsx, line by line
	 * 1) Header: add COGS and Net
	 * 2) Order: add those 2 values. (total - COGS = net)
	 * 3) Other type: just copy over
	 * 
	 * @param csvRecord
	 * @param type
	 */
	void writeOutLine(CSVRecord csvRecord, String type, double total) {
		
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
         for (cellid=0; cellid<csvRecord.size(); cellid++) {
            cell = row.createCell(cellid);
            cell.setCellValue(csvRecord.get(cellid));
         }
		
		if (type.equals(HEADER))  {  // header
	         cell = row.createCell(cellid++);
	         cell.setCellValue("COGS");
	         cell = row.createCell(cellid++);
	         cell.setCellValue("Profit");
	      }
		
		// type order handle. Too bad, due to HEADER, can't use AmznTxnTypeEnum
		if(type.equalsIgnoreCase("Order")) {
			String sku = csvRecord.get("sku");
			if(sku != null) {
				float skuCOGS = cogs.getCOGS	(enumAccount, sku);
				float net = (float)total - skuCOGS;
				
		         cell = row.createCell(cellid++);
		         cell.setCellValue(skuCOGS);
		         cell = row.createCell(cellid++);
		         cell.setCellValue(net);
		         
		         totalCOGS += skuCOGS;
		         totalNet += net;
			}
		}
	}
	
		//Write out summary section
	void writeOutSummary() {
		Cell cell = null;
		
		frontRow[0].createCell(0); 
		cell = frontRow[0].createCell(1); cell.setCellValue("Total COGS");
		cell = frontRow[0].createCell(2); cell.setCellValue(round(totalCOGS));
		
		frontRow[1].createCell(0); 
		cell = frontRow[1].createCell(1); cell.setCellValue("Total Net");
		cell = frontRow[1].createCell(2); cell.setCellValue(round(totalNet));
		
		int topRowid = 2;
		double monthlyGross = 0.0;
		
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
        		double amt = ((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnAmt(); 
        		frontRow[topRowid].createCell(0); 
        		cell = frontRow[topRowid].createCell(1); cell.setCellValue(curTxnType.toString());
        		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(amt));
        		topRowid++;
        		
        		if (curTxnType != AmznTxnTypeEnum.TRANSFER)   monthlyGross += amt; 
        }
        
        monthlyGross -= totalCOGS;
        topRowid++;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Monthly Gross");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(monthlyGross));
		cell = frontRow[topRowid].createCell(5); cell.setCellValue("Bonus");
		cell = frontRow[topRowid].createCell(6); 
		cell.setCellValue(round(monthlyGross * CSVMonthlyTxn.Bonus_RATE));
		
		cell = frontRow[topRowid].createCell(8); 
		cell.setCellValue(round(monthlyGross * CSVMonthlyTxn.Bonus_RATE * CSVMonthlyTxn.CUR_USDRMB));
		cell = frontRow[topRowid].createCell(9); cell.setCellValue("RMB");
	}
	
	void closeOutputFile() {
	    FileOutputStream out;
		try {
			out = new FileOutputStream(new File(xlsxOutputFile));
		      workbook.write(out);
		      out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	double round(double num) {
		return Math.round(num*100)/100.00;
	}
	
	int getParsedRecordCnt() {
		int sum = 0;
		Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curType = enumKeySet.next();
        		sum = sum + txnByTypes.get(curType).getTotalTxnCnt();
        }
		return sum;
	}
	
	/**
	 * Once COGS set, a new version of xlsx should be created and COGS for each item added to the side
	 * in this output file. 
	 */
	@Override	
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs) {
		this.enumAccount = enumAccount;
		this.cogs = cogs;
		
		initOutputFile();
	}
	
	/**
	 * Parse input file. Create txnByTypes that organize records by types. 
	 * Write out xlsx file. Add column of COGS and net.  Add top section of summary and total. 
	 * 
	 * @return  number of records that are really useful. 
	 * @throws IOException
	 */
	@Override
	public int parseFile() throws IOException {

		boolean foundHeader = false; 
		int recordCnt = 0; 
		
        try (
        		Reader reader = Files.newBufferedReader(Paths.get(this.csvInputFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
            				.withHeader(AmznCsvHeaderEnum.class)
                        .withIgnoreHeaderCase()
                        .withTrim());
            ) {
            // create enumMap by its xxxTxnType
        		List<CSVRecord> csvRecords = csvParser.getRecords();    
        		
            for (CSVRecord csvRecord : csvRecords) {
            		String type = HEADER;  // first is always "header", then normal type
            	
	        		// sanity check. recordCnt = sum(type) after parse
	        		if (!foundHeader) {
	        			foundHeader = isHeader(csvRecord); 
	        			if (foundHeader)
	        				writeOutLine(csvRecord, HEADER, 0); // null is header
	        			continue;
	        		}
            		recordCnt ++; 
            		
                // Accessing values by Header names
            		type = csvRecord.get("type");
            		AmznTxnTypeEnum curType = AmznTxnTypeEnum.getEnumType(type);
            		AmznTxnTypeSum curTypeSum = txnByTypes.get(curType);
            		
            		if (curTypeSum == null) 
            			System.out.println("Wrong: no AmznTxnTypeSum found");
            		
            		Number num = null;
				try {
					num = NumberFormat.getNumberInstance(Locale.US).
								parse(csvRecord.get(AmznCsvHeaderEnum.Total));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            		double totalAmt = num.doubleValue();
            		
            		//TODO 1. write out Excel.   2. Order type. sum COGS
            		
            		curTypeSum.increaseTxnCnt();
            		curTypeSum.addTxnAmt(totalAmt);
            		
            		writeOutLine(csvRecord, type, totalAmt);
            } // end for
            
            if (recordCnt == getParsedRecordCnt()) 
            		System.out.println("Parsed " + recordCnt + " useful records successfully!");
            else 
            		System.out.println("sum types  = " + getParsedRecordCnt() + 
            					"\ntlt useful = "  + recordCnt + 
            		             "\ntlt record = " + csvRecords.size()); 
            writeOutSummary();
            closeOutputFile();
        } //end try-final 
        return recordCnt; 
	}
	
	/**
	 * Comparing value of csvRecord for the string of Enum type. 
	 * @param rec
	 * @return
	 */
	boolean isHeader(CSVRecord rec) {
		// skip commented lines. Amazon comment has a few commas, so use 5.
		if (rec.size() < 5) return false; 
		
		for (AmznCsvHeaderEnum enumHdr: AmznCsvHeaderEnum.values()) {
			if (!rec.get(enumHdr).equals(enumHdr.getHeaderName()))  
				return false; 
		}
		return true; 
	}
	
	/**
	 * Print our summary to stand out
	 */
	@Override
	public void displaySummary() {
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
            System.out.printf("%-26s Txn = %6d\tAmt = %8.2f%n", curTxnType,  
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnAmt());
        }
        
        System.out.println("Total CGOS = " + totalCOGS + ". Total Net = "  + totalNet); 
	}
	
    /**
     * @param args
     * @throws IOException
     * 
     * Should do unit test
     */
    //private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2017DecMonthlyTransaction.csv";	
	private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2018FebMonthlyTransaction-AD.csv";
    public static void main(String[] args) throws IOException {
    		System.out.println("=========running  ===========\n");
    		
    		AmznCSVTxnParser parser = new AmznCSVTxnParser(SAMPLE_CSV_FILE_PATH);
    		parser.parseFile();
    		parser.displaySummary();
    		
    }
}
