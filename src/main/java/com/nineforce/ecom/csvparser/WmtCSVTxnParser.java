package com.nineforce.ecom.csvparser;

import static com.nineforce.ecom.csvparser.Util.US_LOCALE;
import static com.nineforce.ecom.csvparser.Util.US_SHIP_COST;
import static com.nineforce.ecom.csvparser.Util.round;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
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
	//double totalNetFromEbay =  0.0; 
	double totalGross = 0.0;
	double totalShippingCharge = 0.0;
	double totalTax = 0.0;
	double totalCommission = 0.0;
	double totalCancel = 0.0;
	double totalShippingPaid = 0.0;
	double totalNet = 0.0;
	
	double monthlyNetForBonus = 0.0;
	
	
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
        		String strSKU = csvRecord.get("SKU");

        		String strGross = csvRecord.get("Item Cost");
        		//String strFee = csvRecord.get("Fee");  wmt fees in anothe file
        		String strShippingCharge = csvRecord.get("Shipping Cost");
        		String strTax = csvRecord.get("Tax");
        		
        		double gross = Double.parseDouble(strGross);
        		double shippingCharge = Double.parseDouble(strShippingCharge);
        		double tax = Double.parseDouble(strTax);
        		
        		/*
        		PaypalTxnTypeEnum curTypeEnum = PaypalTxnTypeEnum.getEnumType(type);
        		PaypalTxnTypeSum curTypeSum = txnByTypes.get(curTypeEnum);
        		

        		//statics 
        		curTypeSum.increaseTxnCnt();
        		curTypeSum.addTxnGross(gross);
        		curTypeSum.addTxnFee(fee);
        		curTypeSum.addTxnGrossNet(grossNet);
        		*/
        		
        		//WMT source file has no type.We can still create type and TypeEunmSum, 
        		// but not do it right now
        		
        		totalGross += gross;
        		totalShippingCharge += shippingCharge;
        		totalTax += tax;
        		
        		
        		writeOutItemLine(csvRecord, gross);
        }
        
        writeOutSummary();
        closeOutputFile();
        
      } catch(Exception e) {
    	  	e.printStackTrace();
      }
  		
      return 0;
	}
	
	
	//Write out summary section
	void writeOutSummary() {
		Cell cell = null;
		int topRowid = 1;
			

		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Gross");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalGross));
		topRowid++;
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Tax");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalTax));
		topRowid++;

		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Commission");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalCommission));
		topRowid++;
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Cancel");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalCancel));
		topRowid++;

		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Shipping Charge");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalShippingCharge));
		topRowid++;

		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Shipping Paid");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalShippingPaid));
		topRowid++;
		
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total COGS");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalCOGS));
		topRowid++;
			
		double totalNetFromWmt = totalGross + totalShippingCharge - 
					(totalCommission + totalCancel + totalShippingPaid + totalCOGS);
		frontRow[topRowid].createCell(0); 
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Total Net From WMT");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(totalNetFromWmt));
		topRowid++;
		topRowid++;
		
		monthlyNetForBonus = totalNetFromWmt;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Monthly Total For Bonus");
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
		
		
	}
	
	/**
	 * Write out line. 
	 * 
	 * @param csvRecord
	 * @param gross
	 */
	void writeOutItemLine(CSVRecord csvRecord,  double gross) {
		
		String strSKU = csvRecord.get("SKU");
		double skuCOGS_RMB = cogs.getCOGS(enumAccount, strSKU);
		double skuCOGS_Loc = (float) (skuCOGS_RMB/Util.getCurrentRate(Util.US_LOCALE));
		double net = gross - skuCOGS_Loc - US_SHIP_COST;	
		
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
		for (cellid=0; cellid<csvRecord.size(); cellid++) {
            cell = row.createCell(cellid);
            cell.setCellValue(csvRecord.get(cellid));
         }
		
        cell = row.createCell(cellid++);
        cell.setCellValue(round(skuCOGS_Loc));
        cell = row.createCell(cellid++);
        cell.setCellValue(US_SHIP_COST);			//shipping avg about $3.2
        cell = row.createCell(cellid++);
        cell.setCellValue(round(net));
        
        totalCOGS += skuCOGS_Loc;
        totalShippingPaid += US_SHIP_COST;
        totalNet += net;
	}
	
	
	
	
	private static final String COGS_PATH = "./JunTxn/COGS.csv";  //for testing in main
	private static final String wmtTestFile = "./JunTxn/pp-test/PO_Data_2018-07-05_04_01_51PST-Walmart-TQS.csv";

	
	public static void main(String[] args) throws IOException {
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
		
		parser.parseFile();		//parse PP file and create output .xlsx
		
	}

}
