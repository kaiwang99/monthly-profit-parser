package com.nineforce.ecom.amzn.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.nineforce.ecom.csvparser.CSVMonthlyTxn;
import com.nineforce.ecom.csvparser.NFAccountEnum;
import com.nineforce.ecom.csvparser.NFAccountTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * Read in a csv file about returns then generate tab delimited files for each SKU
 * Set break into small returns, set ORDER_SIZE 
 * 
 * Order format  RXXX_YY_ZZ, varialb length
 * 		XXX -  1 to 3 digits, from 1 to 999. Order id
 * 		YY - order seq since we break a large QTY. MAX_ORDR_SEQ is 99 for 10-digit order number
 * 		ZZ - QTY in an order. Should be <= ORDER_SIZE
 * 
 * @author kaiwang
 *
 */

public class FbaReturnGenerator {
	
	public static Logger logger = (Logger) LoggerFactory.getLogger(FbaReturnGenerator.class);
	
	final int MAX_ORDR_SEQ = 99; 
	final int ORDER_SIZE = 1; 
	final String subDir= "fba-returns";
	
	// Those vars are account dependent
	String addressHeader = null; 
	String orderPrefix = null;
	
	NFAccountEnum amznAcctEnum = null; 
	String returnFile = null; 
	
	int totalOrder=0, totalQtyOutput=0, totalQtyInput=0;
	Hashtable<String, String> unfulfillSkuQty = new Hashtable<String, String>(); 
	Hashtable<String, String> goodItemSkuQty = new Hashtable<String, String>(); 
	
	public FbaReturnGenerator(String amznAcctName, String returnFile) {
		this.amznAcctEnum = NFAccountEnum.getEnumType(NFAccountTypeEnum.AMZN, amznAcctName);
		this.returnFile = returnFile;
		initByAccount();
	}
	
	/**
	 * Set variables by account
	 */
	void initByAccount()	{
		switch (this.amznAcctEnum) {
		case AMZN_TQS: addressHeader = TQS_HDR_UNFULFILL; orderPrefix = "DA"; break;
		case AMZN_SQB: addressHeader = SQB_HDR; orderPrefix = "S"; break;
		case AMZN_HG:  addressHeader = HG_HDR;  orderPrefix = "H"; break;
		case AMZN_WSD:  addressHeader = WSD_HDR;  orderPrefix = "W"; break;
		default: 
			addressHeader = null; 
			System.out.println("No output file address header found.");
			System.exit(1);
		}
	}
	
	/**
	 * pare input csv file
	 * @throws IOException 
	 */
	public void parse() throws IOException {
        try (	
                Reader reader = Files.newBufferedReader(Paths.get(this.returnFile));        		
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
            ) {
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();

                for (CSVRecord csvRecord : csvRecords) {
                    // Accessing values by Header names

                    String sku = csvRecord.get("SKU");
                    String unfulfillQty = csvRecord.get("Unfulfillable Return Quantity");  
                    addReturnInfo(unfulfillSkuQty, sku, unfulfillQty);
                     
                    String goodItemQty = csvRecord.get("Good Return Quantity");  
                    addReturnInfo(goodItemSkuQty, sku, goodItemQty);
                    
                }
                logger.debug("unfulfillSkuQty content\n{}", unfulfillSkuQty);
                logger.debug("goodItemSkuQty content\n{}", goodItemSkuQty);            

            }
	}
	
	/**
	 * If not empty or 0 qty, then add to the hashtable for later return file generation. 
	 * @param table
	 * @param sku
	 * @param qty
	 */
	void addReturnInfo(Hashtable<String, String> table, String sku, String qty) {
		if (qty!=null && qty.length()>0) {
			int i = Integer.parseInt(qty);
			if (i>0) {
				table.put(sku, qty);
			}
		}
	}
	
	public void genReturnFiles() throws IOException {
		System.out.println("generate below"); 
		
		// create out put csv summary file.   Write csv while creating return files. 
		BufferedWriter writer = Files.newBufferedWriter(Paths.get("./fba-return-summary.csv"));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Order Number", "SKU", "Return QTY", "Upload File"));
		 
        
		int counter = 1; 
		//Set<String> keys = retSkuQty.keySet();
		//Iterator<String> it = keys.iterator();
		
		Enumeration<String> it = unfulfillSkuQty.keys();
		
		while (it.hasMoreElements()) {

			String sku = (String)it.nextElement();
			String qty = (String) unfulfillSkuQty.get(sku); 
			String order_num_base = orderPrefix + counter++;
			
			//System.out.println(sku + "--" + qty + "--" + order_num_base);
			createFile(order_num_base, sku, qty, csvPrinter); 
			
			// System.out.println("sku:" + sku + "\t\tQty:" + retSkuQty.get(sku)); 
		}	
		
		csvPrinter.flush();
		System.out.println("total  input: " + totalQtyInput + "\ntotal output: " + totalQtyOutput +
				"\ntotal return orders:" + totalOrder  + "\ntotal SKU: " + unfulfillSkuQty.size());
	}
	
	
	public int getLoopCount(int qty) {
		int ret = (int) Math.ceil(1.0*qty/(ORDER_SIZE));
		System.out.println("getloop count:" + ret);
		
		return ret;
	}
	

	
	void createFile(String order_num_base, String sku, String strQty, CSVPrinter csvPrinter) {
		File fout = null; 
		int qty = 0;
		int loopQty = 0;
		FileOutputStream fos;
		
	try {
         qty = Integer.parseInt(strQty);
         totalQtyInput  += qty;
		
		int loopCount = getLoopCount(qty);

		
		for (int i=1; i<= loopCount; i++) {
			
			if (i<loopCount) 	loopQty = ORDER_SIZE;
			else				loopQty = qty - (i-1)*ORDER_SIZE;
			
			String order_num = order_num_base + "_" + i +"_" + loopQty;
			String output_file = i + "___" + order_num + "___" + sku + "___" + qty + ".txt";
			
			// Need to create the subDir 
			fout = new File(subDir + "/" + output_file);
			fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		 
			bw.write("MerchantRemovalOrderID\t");
			bw.write(order_num); 
			bw.write("\r\n"); 
			
			bw.write(addressHeader);

			//TODO, important about good or unfulfillable
			// for large qty, separate into small ones
			bw.write(sku + "\t" + 0 + "\t" + loopQty);  
			bw.write("\r\n");
			bw.close();
			
			totalQtyOutput += loopQty;
			totalOrder ++;
			csvPrinter.printRecord(order_num, sku, loopQty, output_file);
		} // end for loop 
		
		System.out.println("Done SKU:" + sku);
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}


	final String TQS_HDR_UNFULFILL = 
			"RemovalDisposition	Return 												\r" + 
			"AddressName	9Force Fulfillment Center 												\r" + 
			"AddressFieldOne	607 Ellis Road 												\r" + 
			"AddressFieldTwo	STE 51A											\r" + 
			"AddressFieldThree													\r" + 
			"AddressCity	Durham  												\r" + 
			"AddressCountryCode	US 												\r" + 
			"AddressStateOrRegion	NC 												\r" + 
			"AddressPostalCode	27703 												\r" + 
			"ContactPhoneNumber	919 367 7316												\r" + 
			"ShippingNotes	Bu4 Ke2 Shou4										\r" + 
			"													\r" + 
			"MerchantSKU	SellableQuantity	UnsellableQuantity\r";
	
	final String SQB_HDR = 
			"RemovalDisposition	Return 												\r" + 
			"AddressName	Susie SQB Su 												\r" + 
			"AddressFieldOne	130 D unit  												\r" + 
			"AddressFieldTwo	ExtraSpace Storage													\r" + 
			"AddressFieldThree													\r" + 
			"AddressCity	Cary  												\r" + 
			"AddressCountryCode	US 												\r" + 
			"AddressStateOrRegion	NC 												\r" + 
			"AddressPostalCode	27519 												\r" + 
			"ContactPhoneNumber	983 365 7336												\r" + 
			"ShippingNotes												\r" + 
			"													\r" + 
			"MerchantSKU	SellableQuantity	UnsellableQuantity\r";
	
	final String HG_HDR = 
			"RemovalDisposition	Return 												\r" + 
			"AddressName	H.G. Wang 												\r" + 
			"AddressFieldOne	101 CUVASION CT  												\r" + 
			"AddressFieldTwo														\r" + 
			"AddressFieldThree													\r" + 
			"AddressCity	Cary  												\r" + 
			"AddressCountryCode	US 												\r" + 
			"AddressStateOrRegion	NC 												\r" + 
			"AddressPostalCode	27519-6200 												\r" + 
			"ContactPhoneNumber	(515)441-1311											\r" + 
			"ShippingNotes												\r" + 
			"													\r" + 
			"MerchantSKU	SellableQuantity	UnsellableQuantity\r";

	final String WSD_HDR = 
			"RemovalDisposition	Return 												\r" + 
			"AddressName	Susie WSD Wong 												\r" + 
			"AddressFieldOne	unit 8  												\r" + 
			"AddressFieldTwo	ExtraSpace Storage													\r" + 
			"AddressFieldThree													\r" + 
			"AddressCity	Cary  												\r" + 
			"AddressCountryCode	US 												\r" + 
			"AddressStateOrRegion	NC 												\r" + 
			"AddressPostalCode	27519 												\r" + 
			"ContactPhoneNumber	919 351-9797												\r" + 
			"ShippingNotes												\r" + 
			"													\r" + 
			"MerchantSKU	SellableQuantity	UnsellableQuantity\r";	

	

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Usage: account-name[tqs|ha|sqb], file-name" +  args[0] + ", " + args[1]);
			System.exit(1);
		}
		
		
		FbaReturnGenerator gen = new FbaReturnGenerator(args[0], args[1]); 
		try {
			gen.parse();
			gen.genReturnFiles();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
