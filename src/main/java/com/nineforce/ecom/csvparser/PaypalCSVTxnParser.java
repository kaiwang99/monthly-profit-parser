package com.nineforce.ecom.csvparser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Hashtable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PaypalCSVTxnParser implements NFcsvParser {
	String csvPayPalFile;    //PP file
	String csvEbayFile;		// ebay file corresponding
	
	EnumMap<PaypalTxnTypeEnum, PaypalTxnTypeSum> txnByTypes;
	public static Logger logger = (Logger) LoggerFactory.getLogger(PaypalCSVTxnParser.class);
	
	Hashtable<String, String> txnSKUmap; 
	
	public PaypalCSVTxnParser(String csvFile) {
		this.csvPayPalFile = csvFile;
		this.csvEbayFile = getPairingEbayCsv();
		
		if (this.csvEbayFile == null) {
			System.out.println("Can't find corresponding ebay file for:" + csvPayPalFile);
			logger.error("Can't find corresponding ebay file for:{}", csvPayPalFile);
			//Shall program quit, or just this parser? 
			System.exit(-1);
		}
		
		txnByTypes = new EnumMap<> (PaypalTxnTypeEnum.class);
		
		// initiate the map, so don't check null in parse CSVRecord loop
		initEnumMap();
	}
	
	String getPairingEbayCsv() {
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(csvPayPalFile);
		logger.debug("In getPairingEbayCsv(). NFAcct is:{}", nfAcct);
		String acctName = nfAcct.getAccountName();
		NFAccountEnum ebayAcctEnum = NFAccountEnum.getEnumType(NFAccountTypeEnum.EBAY, acctName);
		
		logger.debug("Finding corresponding ebay file:{}", ebayAcctEnum);
		String ebayFile = null; 
		File  ppFile = new File(csvPayPalFile);
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
		logger.debug("Find ebay file:{} by pp file:{}", ebayFile, csvPayPalFile);
		return ebayFile;
	}
	
	void initEnumMap() {
		for (PaypalTxnTypeEnum p : PaypalTxnTypeEnum.values()) {
			txnByTypes.put(p, new PaypalTxnTypeSum(p));
		}
	}
	
	/**
	 * extract transaction, SKU pair and put in a hashtable.
	 */
	void parseEbayFile() {
		txnSKUmap = new Hashtable<String, String> ();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(this.csvEbayFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
            
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                // Accessing values by Header names

                String name = csvRecord.get("name");
            }
        } catch(Exception e) {
        	
        }
	}

	@Override
	public int parseFile() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displaySummary() {
		// TODO Auto-generated method stub
		
	}
	

	private static final String COGS_PATH = "./src/test/resources/COGS.csv";  //for testing in main
	private static final String ppTestFile = "./AprTxn/pp-tqs-2018-04-alltxn.CSV";
	
	public static void main(String[] args) {
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
		parser.parseEbayFile();	//create a has
		
//		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(args[0]);
//		System.out.println("find NFAccountEnum from file name:" + nfAcct);
		
//		parser.setCOGS(nfAcct,  cogs);

//		parser.parseFile();
		parser.displaySummary();

	}

}
