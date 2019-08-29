package com.nineforce.ecom.csvparser;


import java.io.File;
//import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import static com.nineforce.ecom.util.NFAccountTypeEnum.*;    
//test

/**
 * This is the main entry that handles all accounts. it invokes  COGS to read cogs file
 * and the each account-type parser. 
 * It reads in source file directory, which contains all the files. 
 * 
 * @author kaiwang
 *
 */

public class CSVMonthlyTxn {
	
	public static Logger logger = (Logger) LoggerFactory.getLogger(CSVMonthlyTxn.class);
	
    String sourceDir;
    ArrayList<String> sourceFiles = new ArrayList<String>();
    COGS cogs;
    
    
    public CSVMonthlyTxn(String sourceDir) {
    		this.sourceDir = sourceDir;
    		getSoruceFiles();
    		initCOGS();
    }
    
    /** 
     * Go through the source directory and populate sourceFile. 
     */
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
                logger.info("File name is {}", fileName);
            }
        }
    }
    
    void initCOGS() {
    		logger.info("initCOGS() on file: {}/COGS.csv", this.sourceDir);
    		cogs = new COGS(this.sourceDir + "/COGS.csv");
    		try {
    				cogs.parse();
			} catch (IOException e) {
				// TODO Auto-generated catch block. Exit. Too much an error. 
				e.printStackTrace();
				System.exit(-1);
			}
    }
    
    /**
     * Go over sourceFile list and invoke AmznCSVTxnParser and other Parsers on each type and country. 
     * File names must indicate market place and account name, such as "AMZN-TQS" or "ebay-ss"  case insensitive. 
     * More examples: 
     * 	 	amazon-ad
	 * 		ebay-ve
	 * 		wmt-tqs
	 * 		etsy-tqs
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
    			logger.debug("File [{}] get NFAccountEnum is [{}]", aFile, nfAcct);
    			
    			if (nfAcct == null) continue; 
    			
    			String fullSourcePath = sourceDir + File.separator + aFile;
    			System.out.println("Will parse for "+ nfAcct + ". File " + aFile);
    			//TODO parent parse or interface, then do the parse
    			
    			switch (nfAcct.getAccountType()) {
    				case AMZN: parser = new AmznCSVTxnParser(fullSourcePath); break;
    				case PP:   parser = new PaypalCSVTxnParser(fullSourcePath); break;
    				case EBAY: logger.debug("Not parse ebay {}. Parse PP", nfAcct); continue;    // no need for ebay. parsed by PP;
    				case WMT:  parser = new WmtCSVTxnParser(fullSourcePath); break;
    				case ETSY:
    				default: System.out.println(" TO BE DONE");
    			}
    			
    			parser.setAccountAndCOGS(nfAcct, cogs);
    			parser.initOutputFile();
    			
    			logger.info("\n\n\n========================================\nParse {}\n{}\n=============================\n", 
    					fullSourcePath, parser);
    			parser.parseFile();
    			parser.displaySummary();
    		}
    		
    		return numFileParsed; 
    }
    

    /**
     * TODO   parse arguments if give sales to run directly 
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    		
    		System.out.println("=========running  ===========\n");
    		
    		CSVMonthlyTxn mon = new CSVMonthlyTxn("2019July");
    		mon.parseFileInDir();
    }
    
}