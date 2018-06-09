package com.nineforce.ecom.csvparser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PaypalCSVTxnParser extends NFCsvTxnParser implements NFcsvParser {
	//String csvPayPalFile;    //PP file
	//String csvEbayFile;		// ebay file corresponding. not class variable, only use in parser although 
	
	EnumMap<PaypalTxnTypeEnum, PaypalTxnTypeSum> txnByTypes;
	public static Logger logger = (Logger) LoggerFactory.getLogger(PaypalCSVTxnParser.class);
	
	//Hashtable<String, String> txnSKUmap; 
	
	public PaypalCSVTxnParser(String csvFile) {
		super(csvFile);    //super initOutputFile
		
	//	this.csvPayPalFile = csvFile;
	//	this.csvEbayFile = getPairingEbayCsv();
		
		initEnumMap();
		
		// initiate the map, so don't check null in parse CSVRecord loop
		
	}
	
	String getPairingEbayCsv() {
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(csvInputFile);
		logger.debug("In getPairingEbayCsv(). NFAcct is:{}", nfAcct);
		String acctName = nfAcct.getAccountName();
		NFAccountEnum ebayAcctEnum = NFAccountEnum.getEnumType(NFAccountTypeEnum.EBAY, acctName);
		
		logger.debug("Finding corresponding ebay file:{}", ebayAcctEnum);
		String ebayFile = null; 
		File  ppFile = new File(csvInputFile);
		File  folder = ppFile.getParentFile();
		logger.debug("directory of this paypal file:" + folder);
		
		String fileName;
		File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileName = files[i].getName();
                
				if (fileName.endsWith(".xlsx") 	|| 
    					fileName.startsWith(".")		||
    					fileName.startsWith("COGS")) 
    					continue;
				
				NFAccountEnum nfAcct_1 = NFAccountEnum.getEnumType(fileName);
				if(nfAcct_1 == ebayAcctEnum) {
					ebayFile = fileName;
					break;
				}
            }
        } 	// end for
        ebayFile = folder.toString() + File.separator + ebayFile;
		logger.debug("Find ebay file:{} by pp file:{}", ebayFile, csvInputFile);
		return ebayFile;
	}
	
	void initEnumMap() {
		txnByTypes = new EnumMap<> (PaypalTxnTypeEnum.class);
		
		for (PaypalTxnTypeEnum p : PaypalTxnTypeEnum.values()) {
			txnByTypes.put(p, new PaypalTxnTypeSum(p));
		}
	}
	
	/**
	 * extract transaction, SKU pair and put in a hashtable.
	 */
	Hashtable<String, String> parseEbayFile(String csvEbayFile) {
		

		Hashtable<String, String> txnSKUmap = new Hashtable<String, String> ();

        try {
        		File inputFile = new File(csvEbayFile);
        		CSVParser csvParser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), 
        				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        		/* not UTF-8, can't handle some chars. 
            Reader reader = Files.newBufferedReader(Paths.get(this.csvEbayFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
                    */
            
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                // Accessing values by Header names

                String txn = csvRecord.get("PayPal Transaction ID");
                String sku = csvRecord.get("Custom Label");
                txnSKUmap.put(txn, sku);
            }
        } catch(Exception e) {
        		e.printStackTrace();
        }
        logger.info("find {} pair of txn and custom label for this month.", txnSKUmap.size());
        return txnSKUmap;
	}
	

	/**
	 * Parse PP file, which is more complete than ebay file. 
	 * Create out .xlse file and summary.  Add Cogs etc. 
	 * 
	 * 1. get pairing ebay file
	 * 2. create hash txn-sku from ebay file
	 * 3. typical parse and create output file
	 * 
	 * @return  number of records that are really useful. 
	 */
	
	public int parseFile() throws IOException {
	
		String csvEbayFile = getPairingEbayCsv();
		if (csvEbayFile == null) {
			System.out.println("Can't find corresponding ebay file for:" + csvInputFile);
			logger.error("Can't find corresponding ebay file for:{}", csvInputFile);
			//Shall program quit, or just this parser? 
			return -1;
		}
		
		Hashtable<String, String> txnSKUmap = parseEbayFile(csvEbayFile);
		
		
      try {
  		File inputFile = new File(csvInputFile);
  		CSVParser csvParser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), 
  				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
  		writeOutHeaderLine(csvParser.getHeaderMap());
  		
		List<CSVRecord> csvRecords = csvParser.getRecords();    
		System.out.println("In parseFile()"  + csvInputFile + ". Total records:" + csvRecords.size());
		logger.debug("In parseFile() {}. Total records:{}", csvInputFile, csvRecords.size());
		
        for (CSVRecord csvRecord : csvRecords) {
        		String type = csvRecord.get("Type");
        		String txnID = csvRecord.get("Transaction ID");
        		String strGross = csvRecord.get("Gross");
        		String strFee = csvRecord.get("Fee");
        		String strNet = csvRecord.get("Net");
        		double gross = Double.parseDouble(strGross);
        		double fee = Double.parseDouble(strFee);
        		double net = Double.parseDouble(strNet);
        		
        		PaypalTxnTypeEnum curTypeEnum = PaypalTxnTypeEnum.getEnumType(type);
        		PaypalTxnTypeSum curTypeSum = txnByTypes.get(curTypeEnum);
        		
        		curTypeSum.increaseTxnCnt();
        		curTypeSum.addTxnGross(gross);
        		curTypeSum.addTxnFee(fee);
        		curTypeSum.addTxnNet(net);
        		
        	
        }
      } catch(Exception e) {
    	  	e.printStackTrace();
      }
  		
		return 0;
	}

	@Override
	public void displaySummary() {
        Iterator<PaypalTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		PaypalTxnTypeEnum curTxnType = enumKeySet.next();
            System.out.printf("%-26s count = %6d\t Gross = %8.2f, Fee = %8.2f, Net = %8.2f%n", curTxnType,  
            			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
            			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGross(), 
            			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnFee(),
            			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnNet());
            
	        Object[] paramArray = {curTxnType,  
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGross(), 
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnFee(),
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnNet()};
	        
	        logger.debug("typ={}, count={}, gross={}, fee={}, net={}", paramArray);
        }
        
  // todo      System.out.println("Total CGOS = " + totalCOGS + ". Total Net = "  + totalNet); 
	}
	

	private static final String COGS_PATH = "./src/test/resources/COGS.csv";  //for testing in main
	private static final String ppTestFile = "./AprTxn/pp-tqs-2018-04-alltxn.CSV";
	
	public static void main(String[] args) throws IOException {
		System.out.println("=========running  ===========" + ppTestFile); 	// + args[0]);
		logger.debug("=========running  ==========={}", ppTestFile);
	
		COGS cogs = new COGS(COGS_PATH);
		try {
				cogs.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		PaypalCSVTxnParser parser = new PaypalCSVTxnParser(ppTestFile);     //args[0]);
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(ppTestFile);
		logger.info("find NFAccountEnum from file name:{}", nfAcct);
		parser.setCOGS(nfAcct,  cogs);
		parser.initOutputFile();
		
		parser.parseFile();		//parse PP file and create output .xlsx
		
		
//		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(args[0]);
//		System.out.println("find NFAccountEnum from file name:" + nfAcct);
		
//		parser.setCOGS(nfAcct,  cogs);

//		parser.parseFile();
		parser.displaySummary();

	}

}
