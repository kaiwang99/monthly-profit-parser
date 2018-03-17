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
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    		
    		System.out.println("=========running  ===========\n");
    		
    		read_COGS();
    	
    		HashMap<String, CSVRecord> cogs = new HashMap<String, CSVRecord>();
    		
        try (
            Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        ) {
            List<CSVRecord> csvRecords = csvParser.getRecords();
            
            // 9 types as in 12/31/2017    test 3rd commit
            int order_count=0, refund_count=0, shipping_services_count=0, fba_inventory_fee_count=0,
            		fab_customer_return_fee_count=0, adjustment_count=0, service_fee_count=0,transfer_count=0,
            		chargeback_refund_count=0,
            		error_count=0, total_txn_count=0;
            		
            
            for (CSVRecord csvRecord : csvRecords) {
                // Accessing values by Header names
            		String type = csvRecord.get("type");
            		switch (type) {
            		case "Order": order_count++; break;
            		case "Refund": refund_count++; break;
            		case "Shipping Services": shipping_services_count++; break;
            		case "FBA Inventory Fee": fba_inventory_fee_count++; break;
            		case "FBA Customer Return Fee": fab_customer_return_fee_count++; break;
            		case "Adjustment": adjustment_count++; break; 
            		case "Service Fee": service_fee_count++; break;
            		case "Transfer": transfer_count++; break;
            		case "Chargeback Refund": chargeback_refund_count++; break;
            		
            		default: error_count++; 
            		}  //end switch
            			
              } // for 
            
            total_txn_count = order_count + refund_count + shipping_services_count + fba_inventory_fee_count
            				+ fab_customer_return_fee_count + adjustment_count + service_fee_count
            				+ transfer_count + chargeback_refund_count + error_count;
            System.out.println("total_txn_count = " + total_txn_count + " tlt record = " + csvRecords.size()); 
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
                    String sku = csvRecord.get("TQS_SKU");
                    float price = Float.parseFloat(csvRecord.get("Price"));
                    //System.out.println(sku + " : " + price);            			
            		}
        	
        }
    		
    }
}