package com.nineforce.ecom.csvparser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WmtCSVTxnParser extends NFCsvTxnParser implements NFcsvParser {
	//String csvPayPalFile;    //PP file
	//String csvEbayFile;		// ebay file corresponding. not class variable, only use in parser although 
	public static Logger logger = (Logger) LoggerFactory.getLogger(WmtCSVTxnParser.class);

	// put in Util filepublic final static double US_SHIP_COST = 3.2; 
	private final static String WMT_FEE_FILE = "Walmart-TQS-fees.csv";
	
	
	
	EnumMap<PaypalTxnTypeEnum, PaypalTxnTypeSum> txnByTypes;
	
	double totalCOGS = 0.0;
	double totalNetFromEbay =  0.0; 
	double totalShipping = 0.0;
	double monthlyNetForBonus = 0.0;
	
	double totalCommission = 0.0;
	double totalCancel = 0.0;
	
	
	public WmtCSVTxnParser(String csvFile) {
		super(csvFile);    //super initOutputFile
		
		//  no need for wmt 
		// initEnumMap();
		getWmtFees();
	}
	
	/** 
	 * 1. get the a file name "Walmart-TQS-fees.csv" file
	 * 2. get wmt sum of commission and cancellation 
	 */
	void getWmtFees() {
		String wmtFeeFile = getWmtFeeFile();
		logger.debug("Get wmtFee file {}", wmtFeeFile);
		
	      try {
      		File inputFile = new File(wmtFeeFile);
      		CSVParser csvParser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), 
      				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
          
          Iterable<CSVRecord> csvRecords = csvParser.getRecords();

          for (CSVRecord csvRecord : csvRecords) {
              // Accessing values by Header names

              String strCancel = csvRecord.get("Canceled Sales");
              String strCommission = csvRecord.get("Commission Charged");
              
              totalCancel += Double.parseDouble(strCancel);
              totalCommission += Double.parseDouble(strCommission);
              
          }
      } catch(Exception e) {
      		e.printStackTrace();
      }
	      
      logger.info("In getWmtFees. totalCancel is {}. totalCommission is {}", totalCancel, totalCommission);
      return ;
	
	}

	
	String getWmtFeeFile() {
		logger.debug("Finding corresponding wmt fee file:{}", csvInputFile);
		String wmtFeeFile = null; 
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
				
				if(fileName.contains(WMT_FEE_FILE)) {
					return folder.toString() + File.separator + fileName;
				}
            }
        } 	// end for
        
		logger.debug("Do NOT Find wmt fee file:{} by wmt file:{}", wmtFeeFile, csvInputFile);
		return wmtFeeFile;	
	}
	
	
	@Override
	void initEnumMap() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Parse wmt  txn file.  we should already parsed wmt fees file and have 
	 */
	
	public int parseFile() throws IOException {

		
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

        		String strGross = csvRecord.get("Gross");
        		String strFee = csvRecord.get("Fee");
        		String strNet = csvRecord.get("Net");
        		double gross = Double.parseDouble(strGross);
        		double fee = Double.parseDouble(strFee);
        		double grossNet = Double.parseDouble(strNet);
        		
        		PaypalTxnTypeEnum curTypeEnum = PaypalTxnTypeEnum.getEnumType(type);
        		PaypalTxnTypeSum curTypeSum = txnByTypes.get(curTypeEnum);
        		
        		//statics 
        		curTypeSum.increaseTxnCnt();
        		curTypeSum.addTxnGross(gross);
        		curTypeSum.addTxnFee(fee);
        		curTypeSum.addTxnGrossNet(grossNet);
        		
        //		writeOutItemLine(csvRecord, curTypeEnum, grossNet);
        }
        
      //  writeOutSummary();
        closeOutputFile();
        
      } catch(Exception e) {
    	  	e.printStackTrace();
      }
  		
		return 0;
	}
	
	
	
	private static final String COGS_PATH = "./MayTxn/COGS.csv";  //for testing in main
	private static final String wmtTestFile = "./MayTxn/From_XM/PO_Data_2018-06-08_03_31_58PST-Walmart-TQS-txn.csv";

	
	public static void main(String[] args) {
		logger.debug("=========running  ==========={}", wmtTestFile);
		
		COGS cogs = new COGS(COGS_PATH);
		try {
				cogs.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		WmtCSVTxnParser parser = new WmtCSVTxnParser(wmtTestFile);     //args[0]);
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(wmtTestFile);
		logger.info("find NFAccountEnum from file name:{}", nfAcct);
		parser.setCOGS(nfAcct,  cogs);
		parser.initOutputFile();
		
		//parser.parseFile();		//parse PP file and create output .xlsx
		
	}

}
