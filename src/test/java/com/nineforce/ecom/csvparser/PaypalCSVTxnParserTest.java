package com.nineforce.ecom.csvparser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PaypalCSVTxnParserTest {
	
	private static final String wsdPPFile = "./src/test/resources/pp-wsd.CSV";
	private static final String tqsPPFile = "./src/test/resources/pp-tqs-201805.CSV";
	
	
	private static final String tqsEbayFile = "./src/test/resources/FileExchange_Response-eBay-TQS-201805.csv";
	private static final String veEbayFile = "./src/test/resources/FileExchange_Response_43979200-ebay-VE.csv";
	private static final String wsdEbayFile = "./src/test/resources/FileExchange_Response_43979201-eBay-WSD.csv";
	
	public static Logger logger = (Logger) LoggerFactory.getLogger(PaypalCSVTxnParser.class);

	private static final String COGS_PATH = "./src/main/resources/COGS_v2.csv";  //for testing in main
	public static PaypalCSVTxnParser tqsParser, wsdParser; 

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		COGS cogs = new COGS(COGS_PATH);
		try {
				cogs.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tqsParser = new PaypalCSVTxnParser(tqsPPFile);     //args[0]);
		NFAccountEnum nfAcct = NFAccountEnum.getEnumType(tqsPPFile);
		logger.info("find NFAccountEnum from file name:{}", nfAcct);
		tqsParser.setCOGS(nfAcct,  cogs);
		tqsParser.initOutputFile();
		
		wsdParser = new PaypalCSVTxnParser(wsdPPFile);     //args[0]);
		nfAcct = NFAccountEnum.getEnumType(wsdPPFile);
		logger.info("find NFAccountEnum from file name:{}", nfAcct);
		wsdParser.setCOGS(nfAcct,  cogs);
		wsdParser.initOutputFile();

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	final void test() {
		//fail("Not yet implemented"); // TODO
	}
	
	/**
	 * 
	 */
	@Test
	void testGetPairingEbayCsv() {
		String parsedTqsEbayFile = tqsParser.getPairingEbayCsv();
		assertEquals(parsedTqsEbayFile, tqsEbayFile);
		
		String parsedWsdEbayFile = wsdParser.getPairingEbayCsv();
		assertEquals(parsedWsdEbayFile, wsdEbayFile);
		
		String parsedVeEbayFile = tqsParser.getPairingEbayCsv(tqsParser.getCsvInputFile(), NFAccountEnum.EBAY_VE);
		assertEquals(parsedVeEbayFile, veEbayFile);
	
		// another parser should get the same VE ebay file
		parsedVeEbayFile = wsdParser.getPairingEbayCsv(tqsParser.getCsvInputFile(), NFAccountEnum.EBAY_VE);
		assertEquals(parsedVeEbayFile, veEbayFile);
	}

}
