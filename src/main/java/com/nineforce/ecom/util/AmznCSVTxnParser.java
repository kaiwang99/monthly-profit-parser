package com.nineforce.ecom.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// parse amazon csv file from reports. mostly use for the month for bonus calculation 

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
        try (
        		Reader reader = Files.newBufferedReader(Paths.get(this.csvFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
            ) {
            // create enumMap by its xxxTxnType
        		List<CSVRecord> csvRecords = csvParser.getRecords();        	
            for (CSVRecord csvRecord : csvRecords) {
                // Accessing values by Header names
            		String type = csvRecord.get("type");
            		AmznTxnTypeEnum curType = AmznTxnTypeEnum.getEnumType(type);
            		AmznTxnTypeSum curTypeSum = txnByTypes.get(curType);
            		
            		if (curTypeSum == null) 
            			System.out.println("Wrong: no AmznTxnTypeSum found");
            		
            		curTypeSum.increaseTxnCnt();
            		// TODO, sum the dollar amt

            } // end for
            
            System.out.println("total parse = " + getParsedRecordCnt() + " tlt csv record = " + csvRecords.size()); 
        
        } //end try-final 
	}
	
	/**
	 * Print our summary to stand out
	 */
	public void displaySummary() {
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
            System.out.println("key : " + curTxnType + " value : " + txnByTypes.get(curTxnType));
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
