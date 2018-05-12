package com.nineforce.ecom.util;


import java.io.File;
//import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

//import static com.nineforce.ecom.util.NFAccountTypeEnum.*;

/**
 * This class handles all accounts. it invokes  COGS to read cogs file, and the each account-type parser
 * It reads in source file directory, which contains all the files. 
 * @author kaiwang
 *
 */

public class CSVMonthlyTxn {
	// TODO This can be in another class. Current Rate and 3% for commission. 
	// Commission can be different to each account and can be trigger more or less by growth. 
	static public final double CUR_USDRMB = 6.33; 
	static public final double Bonus_RATE = 0.03;
	
    String sourceDir;
    ArrayList<String> sourceFiles = new ArrayList<String>();
    COGS cogs;
    
    public CSVMonthlyTxn(String sourceDir) {
    		this.sourceDir = sourceDir;
    		getSoruceFiles();
    		initCOGS();
    }
    
    void getSoruceFiles() {
    		File folder = new File(sourceDir);
        File[] files = folder.listFiles();
        String fileName;
        
        //TODO  if we output .xls file, then take in all other format: csv, txt etc. 
        int lastPeriodPos = 0;
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileName = files[i].getName();
                
    				// Excel output file
    				if (fileName.endsWith(".xlsx") 	|| 
    					fileName.startsWith(".")		||
    					fileName.startsWith("COGS")) 
    					continue;
    			
                sourceFiles.add(fileName);
                
                // TODO  do I take only csv, txt here? 
                lastPeriodPos = fileName.lastIndexOf('.');
                if (lastPeriodPos > 0)
                		fileName = fileName.substring(0, lastPeriodPos);
                
                System.out.println("File name is " + fileName);
            }
        }
    }
    
    void initCOGS() {
    		cogs = new COGS(this.sourceDir + "/COGS.csv");
    		try {
    				cogs.parse();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    /**
     * Go over the directory and invoke AmznCSVTxnParser and other Parsers on each type and country. 
     * File names must indicate "AMZN-TQS" or "ebay-ss"  case insensitive. 
     * 
     * TODO  EU countries for currency? EU COGS is different anyway. May need amzn-tqs-fr eventually. 
     * 
     * Write out the result
     * @return
     * @throws IOException 
     */
    /**
     * @return
     * @throws IOException
     */
    int parseFileInDir() throws IOException {
    		int  numFileParsed = 0;
    		NFcsvParser parser = null;
    		
    		for(String aFile : sourceFiles) {
    			
    			NFAccountEnum nfAcct = NFAccountEnum.getEnumType(aFile);
    			if (nfAcct == null) continue; 
    			
    			System.out.println("parsing for "+ nfAcct);
    			//TODO parent parse or interface, then do the parse
    			
    			switch (nfAcct.getAccountType()) {
    				case AMZN: parser = new AmznCSVTxnParser(sourceDir + File.separator + aFile); break;
    				case EBAY: 
    				case WMT:
    				case ETSY:
    				default: System.out.println(" TO BE DONE");
    			}
    			
    			parser.setCOGS(nfAcct, cogs);
    			parser.parseFile();
    			parser.displaySummary();
    		}
    		
    		return numFileParsed; 
    }
    

    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    		
    		System.out.println("=========running  ===========\n");
    		
    		CSVMonthlyTxn mon = new CSVMonthlyTxn("FebTxn");
    		mon.parseFileInDir();
    }
    
}