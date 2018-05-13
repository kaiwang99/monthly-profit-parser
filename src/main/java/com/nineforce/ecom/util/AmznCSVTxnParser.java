package com.nineforce.ecom.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

// parse amazon csv file from reports. mostly use for the month for bonus calculation
// Hold data in csvRecord and also by type in enummap 

public class AmznCSVTxnParser implements NFcsvParser {
	final int SUMMARY_LEN = 16;   //summar section of the xlsx file
	final String HEADER = "header";   // just a flage to indicate header of csv. 
	
	String csvInputFile;
	String xlsxOutputFile;
	XSSFWorkbook workbook;
	XSSFSheet spreadsheet;
	int rowid;
	Locale curLocale; 
	HashMap<String, String> locTypeStdTypeMap;
	
	COGS cogs;
	NFAccountEnum enumAccount;
	XSSFRow frontRow[];
	
	float totalCOGS = (float) 0.0;
	float totalNet = (float) 0.0; 
	
	EnumMap<AmznTxnTypeEnum, AmznTxnTypeSum> txnByTypes;
	private ResourceBundle messages; 
	
	public AmznCSVTxnParser(String csvFile) {
		this.curLocale = new Locale("en", "US");
		this.csvInputFile = csvFile;
		txnByTypes = new EnumMap<> (AmznTxnTypeEnum.class);
		
		// initiate the map, so don't check null in parse CSVRecord loop
		initEnumMap();
		curLocale = getLocale(csvFile);
		 messages = ResourceBundle.getBundle("MessagesBundle",curLocale);
		locTypeStdTypeMap = Util.getAmznLocTypeStdTypeMap(curLocale);
	}

	void initEnumMap() {
		for (AmznTxnTypeEnum p : AmznTxnTypeEnum.values()) {
			txnByTypes.put(p, new AmznTxnTypeSum(p));
		}
	}
	

	/**
	 * create xlsx file and leave enough for summary 
	 * 
	 * @return 
	 */
	private void initOutputFile() {
		int lastDotIndex = csvInputFile.lastIndexOf('.');
		xlsxOutputFile = csvInputFile.substring(0, lastDotIndex) + ".xlsx";
		
		workbook = new XSSFWorkbook();
		spreadsheet = workbook.createSheet(enumAccount.toString());
		
		//Create row object
		frontRow = new XSSFRow[SUMMARY_LEN];
		for (int i=0; i<frontRow.length; i++) 
			frontRow[i] = spreadsheet.createRow(i);
		
		rowid = SUMMARY_LEN;
	}
	
	
	void writeOutHeaderLine(Map<String, Integer> hdrMap) {
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
		for (Map.Entry<String, Integer> entry : hdrMap.entrySet()) {
		    System.out.println(entry.getKey() + " = " + entry.getValue());
		    cellid = entry.getValue();
			cell = row.createCell(cellid);
	        cell.setCellValue(entry.getKey());		
		}
		cellid = hdrMap.size();
		cell = row.createCell(cellid++); cell.setCellValue("CGOS");
		cell = row.createCell(cellid++); cell.setCellValue("Nets");
	}
	
	/**
	 * Write content of xlsx, line by line
	 * 1) Header: add COGS and Net
	 * 2) Order: add those 2 values. (total - COGS = net)
	 * 3) Other type: just copy over
	 * 
	 * @param csvRecord
	 * @param typeEnum
	 */
	void writeOutItemLine(CSVRecord csvRecord, AmznTxnTypeEnum typeEnum, double total) {
		
		XSSFRow row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		Cell cell = null;
		
		// copy over typical line items
		for (cellid=0; cellid<csvRecord.size(); cellid++) {
            cell = row.createCell(cellid);
            cell.setCellValue(csvRecord.get(cellid));
         }
		  
		// Write out COGS and Net for ORDER Type 
		String locSKU= messages.getString("sku");

		// type order handle. Too bad, due to HEADER, can't use AmznTxnTypeEnum
		if (typeEnum == AmznTxnTypeEnum.ORDER) {
			String sku = csvRecord.get(locSKU);
			
	// TODO easy to get exception on new SKU
	System.out.println("Get SKU[" + sku + "] for file:" + csvInputFile);
			if(sku != null) {
				float skuCOGS_RMB = cogs.getCOGS	(enumAccount, sku);
				float skuCOGS_Loc = (float) (skuCOGS_RMB/Util.getCurrentRate(curLocale));
				float net = (float)total - skuCOGS_Loc;
				
		         cell = row.createCell(cellid++);
		         cell.setCellValue(skuCOGS_Loc);
		         cell = row.createCell(cellid++);
		         cell.setCellValue(net);
		         
		         totalCOGS += skuCOGS_Loc;
		         totalNet += net;
			}
		}
	}
	
		//Write out summary section
	void writeOutSummary() {
		Cell cell = null;
		
		frontRow[0].createCell(0); 
		cell = frontRow[0].createCell(1); cell.setCellValue("Total COGS");
		cell = frontRow[0].createCell(2); cell.setCellValue(round(totalCOGS));
		
		frontRow[1].createCell(0); 
		cell = frontRow[1].createCell(1); cell.setCellValue("Total Net");
		cell = frontRow[1].createCell(2); cell.setCellValue(round(totalNet));
		
		int topRowid = 2;
		double monthlyGross = 0.0;
		
		HashMap<String, String>  stdTypeLocType = Util.getAmznStdTypeLocTypeMap(curLocale);
		
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
        		double amt = ((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnAmt(); 
        		frontRow[topRowid].createCell(0); 
        		String curTypeStr = curTxnType.getTypeName();
        		cell = frontRow[topRowid].createCell(1); cell.setCellValue(curTypeStr);
        		cell = frontRow[topRowid].createCell(2); cell.setCellValue(stdTypeLocType.get(curTypeStr));
        		cell = frontRow[topRowid].createCell(3); cell.setCellValue(round(amt));
        		topRowid++;
        		
        		if (curTxnType != AmznTxnTypeEnum.TRANSFER)   monthlyGross += amt; 
        }
        
        monthlyGross -= totalCOGS;
        topRowid++;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Monthly Gross");
		cell = frontRow[topRowid].createCell(2); cell.setCellValue(round(monthlyGross));
		cell = frontRow[topRowid].createCell(3); cell.setCellValue(Util.currencyByLocale(curLocale));
		
		cell = frontRow[topRowid].createCell(5); cell.setCellValue("Bonus");
		cell = frontRow[topRowid].createCell(6); 
		cell.setCellValue(round(monthlyGross * Util.BONUS_RATE));
		
		cell = frontRow[topRowid].createCell(8); cell.setCellValue("ExchgRate");
		cell = frontRow[topRowid].createCell(9); cell.setCellValue(Util.getCurrentRate(curLocale));
		
		cell = frontRow[topRowid].createCell(10); 
		cell.setCellValue(round(monthlyGross *  Util.BONUS_RATE * Util.getCurrentRate(curLocale)));
		cell = frontRow[topRowid].createCell(11); cell.setCellValue("RMB");
		
		//Warn TQS-EU and US about ads-cost
        topRowid++;
		cell = frontRow[topRowid].createCell(1); cell.setCellValue("Possible ad-exense");

	}
	
	void closeOutputFile() {
	    FileOutputStream out;
		try {
			out = new FileOutputStream(new File(xlsxOutputFile));
		      workbook.write(out);
		      out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	double round(double num) {
		return Math.round(num*100)/100.00;
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
	
	/**
	 * Once COGS set, a new version of xlsx should be created and COGS for each item added to the side
	 * in this output file. 
	 */
	@Override	
	public void setCOGS(NFAccountEnum enumAccount, COGS cogs) {
		this.enumAccount = enumAccount;
		this.cogs = cogs;
		
		initOutputFile();
	}
	
	public static  boolean isEnglishLocale(Locale aLocale) {
		return aLocale.getLanguage().equalsIgnoreCase("en");
	}
	
	public static  boolean isUSLocale(Locale aLocale) {
		return aLocale.getCountry().equalsIgnoreCase("US");
	}
	
	
    /**
     * Detect locael from csv file name pattern
     * @param fileName
     * @return
     */
    public static Locale getLocale(String fileName) {
    		Locale retLocale = Util.US_LOCALE;
    		String lowerFileName = fileName.toLowerCase();
    		
    		if(lowerFileName.contains("amazon-tqs-uk") || lowerFileName.contains("amazon-hg-uk"))
    			retLocale = Util.UK_LOCALE;
    		else if (lowerFileName.contains("amazon-tqs-de") || lowerFileName.contains("amazon-hg-de"))
    			retLocale = Util.DE_LOCALE;
    		else if (lowerFileName.contains("amazon-tqs-fr") || lowerFileName.contains("amazon-hg-fr"))
    			retLocale = Util.FR_LOCALE;
    		else if (lowerFileName.contains("amazon-tqs-it") || lowerFileName.contains("amazon-hg-it"))
    			retLocale = Util.IT_LOCALE;
    		else if (lowerFileName.contains("amazon-tqs-es") || lowerFileName.contains("amazon-hg-es"))
    			retLocale = Util.ES_LOCALE;
    		
    		return retLocale;
    }
    
    /**
     * Since non-US file use withFirstRecordAsHeader(), the parser doesn't give the header. 
     * Need to get it for output file. 
     * 
     * Just some redundaten code. 
     * 
   
    CSVRecord getHeader(File inputFile) {
    	try {
			CSVParser headerParser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), 
					CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim());
			
			headerParser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
      * @return  
     */
    
    
    
	/**
	 * Parse input file. Create txnByTypes that organize records by types. 
	 * Write out xlsx file. Add column of COGS and net.  Add top section of summary and total. 
	 * 
	 * @return  number of records that are really useful. 
	 * @throws IOException
	 */
	@Override
	public int parseFile() throws IOException {

		boolean foundHeaderUS = false; 
		int recordCnt = 0; 
		CSVParser csvParser = null;
		
		String locType = messages.getString("type");
		String locTotal = messages.getString("total");	
		
		
        try {
        		File inputFile = new File(csvInputFile);
        		
        		// a bit hack for en vs. non-en locales
        		if(isUSLocale(curLocale)) {
        			Reader reader = Files.newBufferedReader(Paths.get(this.csvInputFile));
        			csvParser = new CSVParser(reader, CSVFormat.DEFAULT
            				.withHeader(AmznCsvHeaderEnum.class)
                        .withIgnoreHeaderCase()
                        .withTrim());
        		} else {
            		csvParser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), 
            				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            		
            		//headerNonUS = getHeader(inputFile); 
        		}
           
        		// write out header for non-US file. Manuaully remove first 6 lines. 
        		// withFirstRecordAsHeader() - already remove the header. 
        		if(!isUSLocale(curLocale)) {
        			writeOutHeaderLine(csvParser.getHeaderMap());		//Header line has COGS and net
        		}
        		
        		
            // create enumMap by its xxxTxnType
        		List<CSVRecord> csvRecords = csvParser.getRecords();    
        		System.out.println("In parseFile()"  + csvInputFile + ". Total records:" + csvRecords.size());
            for (CSVRecord csvRecord : csvRecords) {
            		String type = HEADER;  // first is always "header", then normal type
            	
            		if (!foundHeaderUS && isUSLocale(curLocale)) {
		        		// sanity check. recordCnt = sum(type) after parse
		        		foundHeaderUS = isHeader(csvRecord); 
		        		if (foundHeaderUS)
		        			writeOutHeaderLine(csvParser.getHeaderMap());  // Header line has COGS and net
		        		continue;
            		}  

            		recordCnt ++; 
	
                // Accessing values by Header names
            		type = csvRecord.get(locType);
            		String stdType = (String) locTypeStdTypeMap.get(type);
            		
            		// TODO log4j here. This is a place that get null exception if 
            		// I see HG Apr 18, 10% promotion has no type info and strange order number. 
            	if(stdType == null)	
          	System.out.println("local type:" + type + "  stdType:" + stdType);
            	
            		AmznTxnTypeEnum curTypeEnum = AmznTxnTypeEnum.getEnumType(stdType);
            		AmznTxnTypeSum curTypeSum = txnByTypes.get(curTypeEnum);
            		
            		if (curTypeSum == null) 
            			System.out.println("Wrong: no AmznTxnTypeSum found");
            		
            		Number num = null;
				try {
					num = NumberFormat.getNumberInstance(curLocale).
								parse(csvRecord.get(locTotal));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            		double totalAmt = num.doubleValue();
            		
            		//TODO 1. write out Excel.   2. Order type. sum COGS
            		
            		curTypeSum.increaseTxnCnt();
            		curTypeSum.addTxnAmt(totalAmt);
            		
            		writeOutItemLine(csvRecord, curTypeEnum, totalAmt);
            } // end for
            
            if (recordCnt == getParsedRecordCnt()) 
            		System.out.println("Parsed " + recordCnt + " useful records successfully!");
            else 
            		System.out.println("sum types  = " + getParsedRecordCnt() + 
            					"\ntlt useful = "  + recordCnt + 
            		             "\ntlt record = " + csvRecords.size()); 
            writeOutSummary();
            closeOutputFile();
            
        }  catch(IOException e) {
        		e.printStackTrace();
        }
        
        
        return recordCnt; 
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
			System.out.println(rec.get(enumHdr) + "=" + enumHdr.getHeaderName()); 
			if (!rec.get(enumHdr).equals(enumHdr.getHeaderName()))  {
				
				return false; 
			}
		}
		return true; 
	}
	
	/**
	 * Print our summary to stand out
	 */
	@Override
	public void displaySummary() {
        Iterator<AmznTxnTypeEnum> enumKeySet = txnByTypes.keySet().iterator();
        while(enumKeySet.hasNext()){
        		AmznTxnTypeEnum curTxnType = enumKeySet.next();
            System.out.printf("%-26s Txn = %6d\tAmt = %8.2f%n", curTxnType,  
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnCnt(), 
            			((AmznTxnTypeSum)txnByTypes.get(curTxnType)).getTotalTxnAmt());
        }
        
        System.out.println("Total CGOS = " + totalCOGS + ". Total Net = "  + totalNet); 
	}
	
    /**
     * @param args
     * @throws IOException
     * 
     * Should do unit test
     */
    //private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2017DecMonthlyTransaction.csv";	
	//private static final String SAMPLE_CSV_FILE_PATH = "./src/main/resources/2018FebMonthlyTransaction-AD.csv";
    
	
	private static final String COGS_PATH = "./src/test/resources/COGS.csv";
	
	public static void main(String[] args) throws IOException {
    		System.out.println("=========running  ===========\n" + args[0]);
    		
    		COGS cogs = new COGS(COGS_PATH);
    		try {
    				cogs.parse();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		AmznCSVTxnParser parser = new AmznCSVTxnParser(args[0]);
    		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(args[0]);
    		System.out.println("find NFAccountEnum from file name:" + nfAcct);
    		
    		parser.setCOGS(nfAcct,  cogs);

    		parser.parseFile();
    		parser.displaySummary();
    }
}
