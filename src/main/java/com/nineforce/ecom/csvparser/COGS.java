package com.nineforce.ecom.csvparser;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nineforce.ecom.csvparser.NFAccountEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read from files and read from database in the future It will hold data in
 * acctSKU, COGS pair, and masterSKU, COGS pair. This is mainly for faster
 * profit calculation. Can be release once the calculation is done.
 * 
 * COGS is in RMB and HAS ADMIN_OVERHEAD included.
 * 
 * For mapping masterSKU vs. acctSKU, it can be another class, such Product,
 * which hold masterSKU, product info, COGS, and sold history can be in another
 * table/class.
 * 
 * @author kaiwang
 *
 */

// now only hold SKU, Account, Price in RMB
public class COGS {
	
	final static double 	DEFAULT_US_COGS_USD = 0.01; 
	final  static double DEFAULT_SKU_COST = DEFAULT_US_COGS_USD*Util.USDRMB_CURRENT; 	 //used this when no sku found. So we won't stop running. 
	
	final static String SL_SUFFIX = "__SL";
	final static String SL_SUFFIX2 = "_SL"; // single _
	final static String KWH_SUFFIX = "-KWH";
	final static String NC_SUFFIX = "-NC";
	final static String NC_SUFFIX_LONG = "--NC";
	final static String CESL_SUFFIX = "-CE__SL";
	final static String A_SUFFIX = "_A";
	final static String CN_SUFFIX = "-CN";
	final static String GIFT_SUFFIX = "_Gift";

	final static String RCTOP_PREFIX = "RC_TOP";
	final static String RCUND_PREFIX = "RC_UND";

	// this will be not used TODO
	final static String ONE_SUFFIX = "-1";

	HashMap<NFAccountEnum, HashMap<String, Float>> allCOGS;
	String file;

	public static Logger logger = (Logger) LoggerFactory.getLogger(COGS.class);

	public COGS(String file) {
		this.file = file;
		initMap();
	}

	void initMap() {
		allCOGS = new HashMap<NFAccountEnum, HashMap<String, Float>>();

		for (NFAccountEnum p : NFAccountEnum.values()) {
			HashMap<String, Float> hm = new HashMap<String, Float>();
			allCOGS.put(p, hm);
		}
	}

	public int parse() throws IOException {

		int count = 0;
		Reader reader = Files.newBufferedReader(Paths.get(this.file));
		logger.info("Start parsing file {}", this.file);

		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
		{

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				// Accessing values by Header names

				String tqsSKU = csvRecord.get("TQS_SKU");
				String ssSKU = csvRecord.get("SS_SKU");
				String wsdSKU = csvRecord.get("WSD_SKU");
				String veSKU = csvRecord.get("VE_SKU");
				String adSKU = csvRecord.get("AD_SKU");
				String hgSKU = csvRecord.get("HG_SKU");
				String sqbSKU = csvRecord.get("SQB_SKU");
				String emcSKU = csvRecord.get("EMC_SKU");
				String priceStr = csvRecord.get("Price");
				if (priceStr == null && priceStr == "")
					continue;
				if (tqsSKU == null && ssSKU == null && wsdSKU == null && veSKU == null && adSKU == null && hgSKU == null
						&& sqbSKU == null && emcSKU == null)
					continue;

				float price = (float) (Float.parseFloat(priceStr) * (1 + Util.ADMIN_OVERHEAD));

				if (tqsSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_TQS).put(tqsSKU, price);
				if (wsdSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_WSD).put(wsdSKU, price);
				if (adSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_AD).put(adSKU, price);
				if (hgSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_HG).put(hgSKU, price);
				if (sqbSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_SQB).put(sqbSKU, price);
				if (emcSKU != null)
					allCOGS.get(NFAccountEnum.AMZN_EMC).put(emcSKU, price);

				// TODO, for ebay, wmt, and etsy, the report has 15% fees. only add shipping
				// cost?
				// or handle it elesewhere ? this is COGS - can be pure of GOODs

				if (ssSKU != null)
					allCOGS.get(NFAccountEnum.EBAY_SS).put(ssSKU, price);
				if (veSKU != null)
					allCOGS.get(NFAccountEnum.EBAY_VE).put(veSKU, price);

				// System.out.println(tqsSKU + ssSKU + wsdSKU + veSKU + adSKU + hgSKU + sqbSKU +
				// ", " + price);

				// logger.debug("The new entry is {}. It replaces {}.", entry, oldEntry);
				Object[] paramArray = { tqsSKU, ssSKU, wsdSKU, veSKU, adSKU, hgSKU, sqbSKU, price };
				logger.debug("{}, {}, {}, {}, {}, {}, {} - {}", paramArray);
				csvParser.close();
			}
		}

		return count;
	}

	/**
	 * Get real COGS in RMB
	 * @param a
	 * @param sku
	 * @return
	 */
	public double getRealCOGSInRMB(NFAccountEnum a, String sku) {
		// System.out.println("getting COGS on SKU :" + sku + " for accout:" + a);
		if (allCOGS == null)
			System.out.print("allCOGS is null");

		if (allCOGS.get(a) == null)
			System.out.print("allCOGS.get(a) is null");

		String rootSKU = getRealSKU(sku); // remove __SL, -KWH etc.
		logger.debug("get SKU for account [{}] for SKU[{}] and real SKU[{}]", a, rootSKU, sku);

		// when pp-wsd or pp-ve, we have to check both account of SKU match.
		// since on PP, wsd and ve is one account.
		if (a == NFAccountEnum.PP_VE || a == NFAccountEnum.PP_WSD) {
			// logger.debug("amzn_wad: {}", allCOGS.get(NFAccountEnum.AMZN_WSD) );
			// logger.debug("ebay_ve: {}", allCOGS.get(NFAccountEnum.EBAY_VE));

			if (allCOGS.get(NFAccountEnum.AMZN_WSD).get(rootSKU) != null)
				return allCOGS.get(NFAccountEnum.AMZN_WSD).get(rootSKU);
			else
				if (allCOGS.get(NFAccountEnum.EBAY_VE).get(rootSKU) != null)
					return allCOGS.get(NFAccountEnum.EBAY_VE).get(rootSKU);
				else {
					recordNullSKU(a, rootSKU);
					//TODO  take a number and see... 
					return DEFAULT_SKU_COST;
				}
					
		}

		if (a == NFAccountEnum.PP_TQS || a == NFAccountEnum.WMT_TQS)
			a = NFAccountEnum.AMZN_TQS;

		if (a == NFAccountEnum.PP_SS)
			a = NFAccountEnum.EBAY_SS;

		HashMap<String, Float> acctMap = allCOGS.get(a);
		
		if (acctMap.get(rootSKU) != null)
			return acctMap.get(rootSKU);
		
		// Nothing found, log and keep running
		logger.warn("SKU[{}] can't find for Account[{}]", sku, a);
		return DEFAULT_SKU_COST;
	}

	
	/**
	 * Now, only write to log. We can create a file in the future
	 * TODO
	 * For ebay combined orders, 
	 */
	public void recordNullSKU(NFAccountEnum nfa, String aSKU) {
		logger.warn("When parsing for account[{}], SKU [{}] cannot be found.", nfa, aSKU);
	}
	
	
	/**
	 * Remove __SL, _SL, -KWH etc. to get real SKU 6-100068221-2002-98210010-CE__SL,
	 * shall I do recursive or just test? Jsut test now.
	 * 
	 * @param sku
	 * @return
	 */
	public static String getRealSKU(String sku) {
		String realSKU = sku;

		// RC TOP and UND
		if (sku.startsWith(RCTOP_PREFIX)) {
			return sku.replaceFirst("_TOP", "_SWM");
		}
		if (sku.startsWith(RCUND_PREFIX)) {
			return sku.replaceFirst("_UND", "_SWM");
		}

		// long suffix to start. Otherwise, shrot one returns directly.
		if (sku.endsWith(GIFT_SUFFIX)) {
			return sku.substring(0, sku.length() - GIFT_SUFFIX.length());
		}
		if (sku.endsWith(CESL_SUFFIX)) {
			return sku.substring(0, sku.length() - CESL_SUFFIX.length());
		}
		if (sku.endsWith(SL_SUFFIX)) {
			return sku.substring(0, sku.length() - SL_SUFFIX.length());
		}
		if (sku.endsWith(SL_SUFFIX2)) {
			//System.out.println("finding SL 2:" + sku);
			return sku.substring(0, sku.length() - SL_SUFFIX2.length());
		}
		if (sku.endsWith(KWH_SUFFIX)) {
			return sku.substring(0, sku.length() - KWH_SUFFIX.length());
		}
		
		if (sku.endsWith(NC_SUFFIX_LONG)) {
			return sku.substring(0, sku.length() - NC_SUFFIX_LONG.length());
		}
		
		if (sku.endsWith(NC_SUFFIX)) {
			return sku.substring(0, sku.length() - NC_SUFFIX.length());
		}
		if (sku.endsWith(A_SUFFIX)) {
			return sku.substring(0, sku.length() - A_SUFFIX.length());
		}
		if (sku.endsWith(ONE_SUFFIX)) {
			return sku.substring(0, sku.length() - ONE_SUFFIX.length());
		}
		if (sku.endsWith(CN_SUFFIX)) {
			return sku.substring(0, sku.length() - CN_SUFFIX.length());
		}

		return realSKU;
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();

		for (Map.Entry<NFAccountEnum, HashMap<String, Float>> entry1 : allCOGS.entrySet()) {
			ret.append("==================================\n===      ");
			ret.append(entry1.getKey() + "      ===\n");
			ret.append("==================================\n");

			HashMap<String, Float> acctCOGS = (HashMap<String, Float>) entry1.getValue();

			for (Iterator<String> iterator = acctCOGS.keySet().iterator(); iterator.hasNext();) {
				String sku = iterator.next();
				float cogs = acctCOGS.get(sku);
				ret.append(sku + "\t\t" + cogs + "\n");
			}
		}
		return ret.toString();
	}

	public static void main(String[] args) throws IOException {
		// COGS cogs = new COGS("./src/main/resources/COGS.csv");
		COGS cogs = new COGS("./MayTxn/COGS.csv");
		File fileOutput = new File("./src/main/resources/parseCOGS.txt");
		try {
			cogs.parse();
			// useless, too many to read all
			// System.out.println(cogs.toString());

			BufferedWriter br = new BufferedWriter(new FileWriter(fileOutput));
			br.write(cogs.toString());
			br.close();

			System.out.println(cogs.getRealCOGSInRMB(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011"));

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
