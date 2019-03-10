package com.nineforce.ecom.csvparser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nineforce.ecom.csvparser.Util.*;


public class PaypalCSVTxnParser extends NFCsvTxnParser implements NFcsvParser {
	//String csvPayPalFile;    //PP file
	//String csvEbayFile;		// ebay file corresponding. not class variable, only use in parser although 
	
	EnumMap<PaypalTxnTypeEnum, PaypalTxnTypeSum> txnByTypes;
	public static Logger logger = (Logger) LoggerFactory.getLogger(PaypalCSVTxnParser.class);
	
	double totalCOGS = 0.0;
	double totalNetFromEbay =  0.0; 
	double totalShipping = 0.0;
	double monthlyNetForBonus = 0.0;
	
	// This 3 for small income statement beginning part
	double totalPPGross = 0.0; 
	double totalPPFee = 0.0; 
	double totalPPNet = 0.0; 
	
	public PaypalCSVTxnParser(String csvFile) {
		super(csvFile);    //super initOutputFile
		
	//	this.csvPayPalFile = csvFile;
	//	this.csvEbayFile = getPairingEbayCsv();
		
		initEnumMap();
		
		// initiate the map, so don't check null in parse CSVRecord loop
		
	}
	
	/**
	 * To find ebay-ve from pp-wsd, create overloaded functions. 
	 * This is for class method to call directly
	 * @return
	 */
	String getPairingEbayCsv() {
		return getPairingEbayCsv(this.csvInputFile); 
	}
	
	/**
	 * To find ebay-ve from pp-wsd, create overloaded functions. 
	 * This is transition method. Nobody will call it at this time. June 2018.
	 * @param inputPPfile
	 * @return
	 */
	String getPairingEbayCsv(String inputPPfile) {
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(inputPPfile);
		logger.debug("In getPairingEbayCsv(). NFAcct is:{}", nfAcct);
		String acctName = nfAcct.getAccountName();
		NFAccountEnum ebayAcctEnum = NFAccountEnum.getEnumType(NFAccountTypeEnum.EBAY, acctName);
		
		return getPairingEbayCsv(inputPPfile, ebayAcctEnum);
	}
	
	/**
	 * To find ebay-ve from pp-wsd, create overloaded functions. 
	 *  Will call this with pp-wsd file and ebay-ve enum
	 * @param inputPPfile
	 * @return
	 */
	String getPairingEbayCsv(String inputPPfile, NFAccountEnum ebayAcctEnum) {		
		
		logger.debug("Finding corresponding ebay file:{}", ebayAcctEnum);
		String ebayFile = null; 
		File  ppFile = new File(inputPPfile);
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
		logger.debug("Find ebay file:{} by pp file:{}", ebayFile, inputPPfile);
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
        		/* not UTF-8, can't handle some chars. x
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
	 * For pp-wsd, it has both ebay-wsd and ebay-ve transaction. 
	 * 
	 */
	Hashtable<String, String> getTxnSkuMap() {
		
		String csvEbayFile = getPairingEbayCsv();
		if (csvEbayFile == null) {
			System.out.println("Can't find corresponding ebay file for:" + csvInputFile);
			logger.error("Can't find corresponding ebay file for:{}", csvInputFile);
			//Shall program quit, or just this parser? 
			return null;
		}
		
		return parseEbayFile(csvEbayFile);		
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
        		
        		
        		double gross = Util.parseDouble(strGross);
        		double fee = Util.parseDouble(strFee);
        		double grossNet = Util.parseDouble(strNet);
        		
        		PaypalTxnTypeEnum curTypeEnum = PaypalTxnTypeEnum.getEnumType(type);
        		PaypalTxnTypeSum curTypeSum = txnByTypes.get(curTypeEnum);
        		
        		//statics 
        		curTypeSum.increaseTxnCnt();
        		curTypeSum.addTxnGross(gross);
        		curTypeSum.addTxnFee(fee);
        		curTypeSum.addTxnGrossNet(grossNet);
        		
        		writeOutItemLine(csvRecord, curTypeEnum, grossNet);
        		totalPPGross += gross; 
        		totalPPFee += fee; 
        		totalPPNet += grossNet; 
        }
        
        writeOutSummary();
        closeOutputFile();
        
      } catch(Exception e) {
    	  	e.printStackTrace();
    	  	logger.error("Exception", e);
      }
  		
		return 0;
	}
	
	
	//Write out summary section
	void writeOutSummary() {
		Cell cell = null;
		int topRowid = EXCEL_START_LINE;
		
		
		//////////////////////////////////
		//  Summary By Type 
		//////////////////////////////////
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Summary By Type");
		cell.setCellStyle(style6);
		topRowid++; 
		topRowid++;
		
		// create a header for type
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Type");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue("Cnt");
		cell = frontRow[topRowid].createCell(3); cell.setCellValue("Gross");
		cell = frontRow[topRowid].createCell(4); cell.setCellValue("Fee");
		cell = frontRow[topRowid].createCell(5); cell.setCellValue("Net");
		topRowid++;
		
		//	HashMap<String, String>  stdTypeLocType = Util.getAmznStdTypeLocTypeMap(curLocale);
		Iterator<PaypalTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
		while(enumKeySet.hasNext()) {
			PaypalTxnTypeEnum curTxnType = enumKeySet.next();
			double gross = ((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGross(); 
			frontRow[topRowid].createCell(0);  //skip firt col
				
			// type, count write out
			String curTypeStr = curTxnType.getTypeName();
			cell = frontRow[topRowid].createCell(1); cell.setCellValue(curTypeStr);
			cell = frontRow[topRowid].createCell(2); cell.setCellValue(((PaypalTxnTypeSum)txnByTypes.
					get(curTxnType)).getTotalTxnCnt());				
				// gross, fee, net writ out
			cell = frontRow[topRowid].createCell(3); cell.setCellValue(round(gross));
			
			double fee = ((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnFee(); 
			cell = frontRow[topRowid].createCell(4); cell.setCellValue(round(fee));
			
			double grossNet = ((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGrossNet(); 				
			cell = frontRow[topRowid].createCell(5); cell.setCellValue(round(grossNet));
			
			topRowid++;
				
			// PP transfer  TODO if (curTxnType != AmznTxnTypeEnum.TRANSFER)   monthlyGross += amt; 
		}
		

		////////////////////////////////////////////////////////////
		// write out a small income statement on the right section 
		////////////////////////////////////////////////////////////
		
		topRowid = EXCEL_START_LINE;
		int colid = STMT_START_COL;
		cell = frontRow[topRowid].createCell(colid); cell.setCellValue("Small Income Statment");
		cell.setCellStyle(style6);
		topRowid++;
		topRowid++;
	
		
		cell = frontRow[topRowid].createCell(colid); 	cell.setCellValue("Paypal Income");
		cell = frontRow[topRowid].createCell(colid + 2); cell.setCellValue(totalPPGross);
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 	cell.setCellValue("Paypal Fee");
		cell = frontRow[topRowid].createCell(colid + 2); cell.setCellValue(totalPPFee);
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 		cell.setCellValue("Paypal Net");
		cell = frontRow[topRowid].createCell(colid + 2); 		cell.setCellValue(totalPPNet);

		topRowid++;
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 		cell.setCellValue("COGS");
		cell = frontRow[topRowid].createCell(colid + 2); 		cell.setCellValue(totalCOGS);
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 		cell.setCellValue("Est. Shipping");
		cell = frontRow[topRowid].createCell(colid + 2); 		cell.setCellValue(totalShipping);				
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 		cell.setCellValue("ebay FVF on Card");
		cell = frontRow[topRowid].createCell(colid + 2); 		cell.setCellValue(0);	
		cell = frontRow[topRowid].createCell(colid + 3); 		cell.setCellValue("手动添加");	
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); 		cell.setCellValue("Large Shipping Cost");
		cell = frontRow[topRowid].createCell(colid + 2); 		cell.setCellValue(0);	
		cell = frontRow[topRowid].createCell(colid + 3); 		cell.setCellValue("手动添加");	
		
		topRowid++;
		topRowid++;
		cell = frontRow[topRowid].createCell(colid); cell.setCellValue("Total Net From ebay");
		cell = frontRow[topRowid].createCell(colid + 2); cell.setCellValue(round(totalNetFromEbay));		

		////////////////////////////////////////////////////////////
		// write out bonus
		////////////////////////////////////////////////////////////	
		topRowid = EXCEL_START_LINE;
		colid = BONUS_START_COL;
		cell = frontRow[topRowid].createCell(colid); cell.setCellValue("Bonus Calculation");
		cell.setCellStyle(style6);
		topRowid++;
		topRowid++;
		
		cell = frontRow[topRowid].createCell(colid); 	cell.setCellValue("Gross Profit");
		cell = frontRow[topRowid].createCell(colid + 2); cell.setCellValue(round(totalNetFromEbay));	
		topRowid++;
		
		cell = frontRow[topRowid].createCell(colid);		cell.setCellValue("Bonus USD");
		cell = frontRow[topRowid].createCell(colid + 2); cell.setCellValue(round(totalNetFromEbay * Util.BONUS_RATE));
		topRowid++;

		cell = frontRow[topRowid].createCell(colid); 	cell.setCellValue("Rate When Purchase");
		cell = frontRow[topRowid].createCell(colid + 2); 	cell.setCellValue(USDRMB_CURRENT);
		topRowid++;
		
		cell = frontRow[topRowid].createCell(colid);		cell.setCellValue("Bonus RMB");
		cell = frontRow[topRowid].createCell(colid + 2);	cell.setCellValue(round(totalNetFromEbay *  Util.BONUS_RATE * USDRMB_CURRENT));
			
		
/*
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total COGS");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalCOGS));
		topRowid++;
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Shipping");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalShipping));
		topRowid++;
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Net From ebay");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalNetFromEbay));
		topRowid++;
		topRowid++;
		
		

		
		
		topRowid++;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Monthly Gross For Bonus");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(monthlyNetForBonus));
		cell = frontRow[topRowid].createCell(3); cell.setCellValue(Util.currencyByLocale(US_LOCALE));
		
		cell = frontRow[topRowid].createCell(5); cell.setCellValue("Bonus");
		cell = frontRow[topRowid].createCell(6); 
		cell.setCellValue(round(monthlyNetForBonus * Util.BONUS_RATE));
		
		cell = frontRow[topRowid].createCell(8); cell.setCellValue("ExchgRate");
		cell = frontRow[topRowid].createCell(9); cell.setCellValue(Util.getCurrentRate(US_LOCALE));
		
		cell = frontRow[topRowid].createCell(10); 
		cell.setCellValue(round(monthlyNetForBonus *  Util.BONUS_RATE * Util.getCurrentRate(US_LOCALE)));
		cell = frontRow[topRowid].createCell(11); cell.setCellValue("RMB");
		
		//Warn TQS-EU and US about ads-cost
		topRowid++;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Possible FVF exense on credit card");

		*/
		}
	
	// patch for wsd and ve share same PP file
	void addVESku(Hashtable<String, String> txnSkuMap) {
		NFAccountEnum ebayVE = NFAccountEnum.EBAY_VE;
		String veEbay = getPairingEbayCsv(this.csvInputFile, ebayVE); 
		
		Hashtable<String, String> veTxnMap = parseEbayFile(veEbay);
		Iterator it = veTxnMap.keySet().iterator();
		
		while(it.hasNext()) {
			String aTxn = (String)it.next();
			String aSku =veTxnMap.get(aTxn); 
			txnSkuMap.put(aTxn,  aSku);
		}
		
		logger.info("Added {} txn,sku pair for VE", veTxnMap.size());
	}
	
	/**
	 * Write content of xlsx, line by line
	 * 1) Header: add Custom Label, COGS and Net
	 * 2) Order: add those 3 values. (GrossNet - COGS = net)   
	 * TODO --- shipping cost? 
	 * 3) Other type: just copy over
	 * 
	 * @param csvRecord
	 * @param typeEnum
	 */
	void writeOutItemLine(CSVRecord csvRecord, PaypalTxnTypeEnum typeEnum, double grossNet) {
		Hashtable<String, String> txnSkuMap = getTxnSkuMap();
		
		// if pp-wsd, then need to add ve's txn-sku map 
		if (enumAccount == NFAccountEnum.PP_WSD) 
			addVESku(txnSkuMap);
		
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
		// copy over typical line items
		for (cellid=0; cellid<csvRecord.size(); cellid++) {
            cell = row.createCell(cellid);
            cell.setCellValue(csvRecord.get(cellid));
         }
		  
		// type order handle. 
		if (typeEnum == PaypalTxnTypeEnum.EBAY_PAYMNET) {
    			String txnID = csvRecord.get("Transaction ID");
        		//those are by type, should be in writeLine
        		String sku = txnSkuMap.get(txnID);
        		
			// TODO easy to get exception on new SKU
			logger.debug("Get SKU[{}] for transaction {} from file:{}", sku, txnID, csvInputFile); 
		
			if(sku != null) {
				float skuCOGS_RMB = cogs.getCOGS	(enumAccount, sku);
				float skuCOGS_Loc = (float) (skuCOGS_RMB/Util.getCurrentRate(Util.US_LOCALE));
				double net = (float)grossNet - skuCOGS_Loc - US_SHIP_COST;
				
				//write out SKU, COGS, shipping, and net
				 cell = row.createCell(cellid++);
		         cell.setCellValue(sku);
		         cell = row.createCell(cellid++);
		         cell.setCellValue(round(skuCOGS_Loc));
		         cell = row.createCell(cellid++);
		         cell.setCellValue(US_SHIP_COST);			//shipping avg about $3.2
		         cell = row.createCell(cellid++);
		         cell.setCellValue(round(net));
		         
		         totalCOGS += skuCOGS_Loc;
		         totalShipping += US_SHIP_COST;
		         totalNetFromEbay += net;
		         
		         monthlyNetForBonus += net;
			}
		} else {  // not ebay txn, write out gross net directly
			cellid += 3; 
			cell = row.createCell(cellid++);
			cell.setCellValue(round(grossNet));
			
			monthlyNetForBonus += grossNet;
		}
	}
	
	
	
	/**
	 * Write out the header line after summary section by the csv record, 
	 * adding COGS and Net two columns.  
	 * 
	 * PaypalCSVTnxParser overwrite this function - need add SKU column. 
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
		cell = row.createCell(cellid++); cell.setCellValue("Custom Label");
		cell = row.createCell(cellid++); cell.setCellValue("CGOS");
		cell = row.createCell(cellid++); cell.setCellValue("Shipping");
		cell = row.createCell(cellid++); cell.setCellValue("Nets");
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
            			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGrossNet());
            
            if (curTxnType == PaypalTxnTypeEnum.ERROR   &&
            		((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt() > 0) {
            		String errorStr = "\n================================\n" + 
            						  "=== Has Error "  +  
            						  ((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt()  + 
            						  "         ===\n" +
            						  "================================\n";
            		//System.out.println(errorStr);
            		logger.error(errorStr);
            }
            
	        Object[] paramArray = {curTxnType,  
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGross(), 
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnFee(),
        			((PaypalTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnGrossNet()};
	        
	        logger.debug("typ={}, count={}, gross={}, fee={}, net={}", paramArray);
        }
        
  // todo      System.out.println("Total CGOS = " + totalCOGS + ". Total Net = "  + totalNet); 
	}
	

	private static final String COGS_PATH = "./OctTxn/COGS.csv";  //for testing in main
	//private static final String ppTestFile = "./MayTxn/ebay/pp-wsd.CSV";
	private static final String ppTestFile = "./OctTxn/pp-test/pp-tqs-201810.CSV";
	
	/**
	 * @param args
	 * @throws IOException
	 */
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
