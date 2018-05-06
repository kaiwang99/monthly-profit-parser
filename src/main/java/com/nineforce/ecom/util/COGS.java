package com.nineforce.ecom.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.nineforce.ecom.util.NFAccountEnum;


/** 
 * Read from files and read from database in the future
 * It will hold data in  acctSKU, COGS pair, and masterSKU, COGS pair. 
 * This is mainly for faster profit calculation. Can be release once the calculation is done. 
 * 
 * For mapping masterSKU vs. acctSKU, it can be another class, such Product, which 
 * hold masterSKU, product info, COGS, and sold history can be in another table/class.
 * 
 * @author kaiwang
 *
 */

// now only hold SKU, Account, Price in RMB
public class COGS {
	
	HashMap<NFAccountEnum, HashMap<String, Float>> allCOGS;
	String file; 
	
	
	public COGS(String file) {
		this.file = file;
		initMap();
	}
	
	void initMap() {
		allCOGS = new HashMap<NFAccountEnum, HashMap<String, Float>> ();
		
		for (NFAccountEnum p : NFAccountEnum.values()) {
			HashMap<String, Float> hm = new HashMap<String, Float>();
			allCOGS.put(p, hm);
		}
	}
	
	
	int parse() throws IOException {
		
		int count = 0;
        Reader reader = Files.newBufferedReader(Paths.get(this.file));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()); {
                	
			    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
			
			    for (CSVRecord csvRecord : csvRecords) {
			        // Accessing values by Header names
			
			        String tqsSKU = csvRecord.get("TQS_SKU").trim();
			        String ssSKU = csvRecord.get("SS_SKU").trim();
			        String wsdSKU = csvRecord.get("WSD_SKU").trim();
			        String veSKU = csvRecord.get("VE_SKU").trim();
			        String adSKU = csvRecord.get("AD_SKU").trim();
			        String hgSKU = csvRecord.get("HG_SKU").trim();
			        String sqbSKU = csvRecord.get("SQB_SKU").trim();
			        String priceStr = csvRecord.get("Price").trim();
			        float price = Float.parseFloat(priceStr);
			        
			        if(tqsSKU != null)	allCOGS.get(NFAccountEnum.AMZN_TQS).put(tqsSKU, price);
			        if(wsdSKU != null)	allCOGS.get(NFAccountEnum.AMZN_WSD).put(wsdSKU, price);
			        if(adSKU != null)	allCOGS.get(NFAccountEnum.AMZN_AD).put(adSKU, price);
			        if(hgSKU != null)	allCOGS.get(NFAccountEnum.AMZN_HG).put(hgSKU, price);
			        if(sqbSKU != null)	allCOGS.get(NFAccountEnum.AMZN_SQB).put(sqbSKU, price);
			        
			        // TODO, for ebay, wmt, and etsy, the report has 15% fees. only add shipping cost?
			        //		or handle it elesewhere ? this is COGS - can be pure of GOODs
			        
			        if(ssSKU != null)	allCOGS.get(NFAccountEnum.EBAY_SS).put(ssSKU, price);
			        if(veSKU != null)	allCOGS.get(NFAccountEnum.EBAY_VE).put(veSKU, price);

			        //System.out.println(tqsSKU + ssSKU + wsdSKU + veSKU + adSKU + hgSKU + sqbSKU + ", " + price);
			        csvParser.close();
			    }
        }
            
		return count;
	}
	
	public float getCOGS(NFAccountEnum a, String sku) {
		System.out.println(a);
		if (allCOGS == null) 
			System.out.print("allCOGS is null");	
		else
			System.out.println(allCOGS);
		
		if (allCOGS.get(a) == null) 
			System.out.print("allCOGS.get(a) is null");	
		else 
			System.out.println(allCOGS.get(a));
		
		
		return allCOGS.get(a).get(sku);
	}
	
	public String toString() {
		StringBuffer ret = new StringBuffer();

		for(Map.Entry<NFAccountEnum, HashMap<String, Float>> entry1 : allCOGS.entrySet()) {
			ret.append("==================================\n==="); 
			ret.append(entry1.getKey() + "===\n");
			ret.append("==================================\n");
			
			HashMap<String, Float> acctCOGS = (HashMap<String, Float>)entry1.getValue();
			
			for (Iterator<String> iterator = acctCOGS.keySet().iterator(); iterator.hasNext();) {
				String sku = iterator.next();
				float cogs = acctCOGS.get(sku);
				ret.append(sku + "\t\t" + cogs + "\n");
			}
		}
		return ret.toString();
	}
	
	
    public static void main(String[] args) throws IOException {
    		COGS  cogs = new COGS("./src/main/resources/COGS.csv");
    		File fileOutput = new File("./src/main/resources/parseCOGS.txt");
    		try {
    			cogs.parse();
    			// useless, too many to read all
    			//System.out.println(cogs.toString());
    			
    			BufferedWriter br = new BufferedWriter(new FileWriter(fileOutput));
    			br.write(cogs.toString());
    			br.close();
    			
    			System.out.println(cogs.getCOGS(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011"));
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    			
    		}
    	
    }
}
