package com.nineforce.ecom.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

//import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;


// Parse csc file of monthly transactions, so the bonus will be faster.

public class CSVMonthlyTxn {
    private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2017DecMonthlyTransaction.csv";
    
    private static final String COGS_CSV_FILE_PATH = "./src/main/resources/COGS.csv";    
    HashMap cogsHash = new HashMap();   //hold SKU-price (RMB). SKU can be any account, 
    
    public static void main(String[] args) throws IOException {
    		
    		System.out.println("=========running  ===========\n");
    		
    		read_COGS();
    	
    		HashMap<String, CSVRecord> cogs = new HashMap<String, CSVRecord>();
    		
        try (
            Reader reader = Files.newBufferedReader(Paths.get(COGS_CSV_FILE_PATH));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {
            List<CSVRecord> csvRecords = csvParser.getRecords();
            
            // 9 types as in 12/31/2017
            int order_count=0, refund_count=0, shipping_services_count=0, fba_inventory_fee_count=0,
            		fab_customer_return_fee_count=0, adjustment_count=0, service_fee_count=0,transfer_count=0,
            		chargeback_refund=0,
            		error_count=0, total_txn_count=0;
            		
            
            for (CSVRecord csvRecord : csvRecords) {
                // Accessing values by Header names

                String sku = csvRecord.get("TQS_SKU");
                float price = Float.parseFloat(csvRecord.get("Price"));
                	System.out.println(sku + " : " + price);
              }
      
            }
                   
    }
    
    // setup cogsHash from the flat file
    private static void read_COGS() throws IOException {
        try (
                Reader reader = Files.newBufferedReader(Paths.get(COGS_CSV_FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
            ) {   
            		List<CSVRecord> csvRecords = csvParser.getRecords();          
            		for (CSVRecord csvRecord : csvRecords) {
            			
            		}
        	
        }
    		
    }
}