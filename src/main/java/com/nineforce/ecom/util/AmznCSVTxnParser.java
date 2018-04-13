package com.nineforce.ecom.util;

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

// parse amazon csv file from reports. mostly use for the month for bonus calculation
// Hold data in csvRecord and also by type in enummap 

public class AmznCSVTxnParser {
	String csvFile; 
	EnumMap<AmznTxnTypeEnum, AmznTxnTypeSum> txnByTypes; 
	
	public AmznCSVTxnParser(String csvFile) {
		this.csvFile = csvFile;
		txnByTypes = new EnumMap<> (AmznTxnTypeEnum.class);
		
		// initiate the map, so don't check null in parse CSVRecord loop
		initEnumMap();
	}

	void initEnumMap() {
		for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			txnByTypes.put(p, new AmznTxnTypeSum(p));
		}
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
	
	void parseFile() throws IOException {
		boolean foundHeader = false; 
		int recordCnt = 0; 
		
        try (
        		Reader reader = Files.newBufferedReader(Paths.get(this.csvFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
            				.withHeader(AmznCsvHeaderEnum.class)
                        .withIgnoreHeaderCase()
                        .withTrim());
            ) {
            // create enumMap by its xxxTxnType
        		List<CSVRecord> csvRecords = csvParser.getRecords();    
        		
            for (CSVRecord csvRecord : csvRecords) {
	        		// sanity check. recordCnt = sum(type) after parse
	        		if (! foundHeader) {
	        			foundHeader = isHeader(csvRecord); 
	        			continue;
	        		}
            		recordCnt ++; 
            		
                // Accessing values by Header names
            		String type = csvRecord.get("type");
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
            		
            		curTypeSum.increaseTxnCnt();
            		curTypeSum.addTxnAmt(totalAmt);
            } // end for
            
            if (recordCnt == getParsedRecordCnt()) 
            		System.out.println("Parsed " + recordCnt + " useful records successfully!");
            else 
            		System.out.println("sum types  = " + getParsedRecordCnt() + 
            					"\ntlt useful = "  + recordCnt + 
            		             "\ntlt record = " + csvRecords.size()); 
     
        } //end try-final 

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
	public void displaySummary() {
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
            System.out.printf("%-26s Txn = %6d\tAmt = %8.2f%n", curTxnType,  
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnAmt());
        }
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
