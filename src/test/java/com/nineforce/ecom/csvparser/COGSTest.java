package com.nineforce.ecom.csvparser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nineforce.ecom.csvparser.COGS;
import com.nineforce.ecom.csvparser.NFAccountEnum;

class COGSTest {
	public static Logger logger = (Logger) LoggerFactory.getLogger(COGSTest.class);
	
	static final String COGS_file = "./JunTxn/COGS.csv";
	static COGS cogs;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		cogs = new COGS(COGS_file	);
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
	final void testInitMap() {
		//fail("Not yet implemented"); // TODO
		// initMap() is called in 
		assertEquals(14, cogs.allCOGS.size());
		
		assertEquals(NFAccountEnum.values().length, cogs.allCOGS.size());
	}

	@Test
	final void testParse() throws IOException {
		//fail("Not yet implemented"); // TODO
		//get a fix number to test.. can be changed. Can be more convoluted: read in file again and then compare
		cogs.parse();
		
		//assertEquals(cogs.getCOGS(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011"), 1.5*(1 + Util.ADMIN_OVERHEAD));
		double delta = Math.abs(cogs.getCOGS(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011") -
								1.5*(1 + Util.ADMIN_OVERHEAD));
		assertTrue(delta < 0.001);
		
		
		
		assertEquals(cogs.getCOGS(NFAccountEnum.EBAY_SS, "XB1717-ASCBF337-302HMT"), 10*(1 + Util.ADMIN_OVERHEAD));
		
		delta = Math.abs(cogs.getCOGS(NFAccountEnum.AMZN_WSD, "CALLOFD-TSION5-8-LIGTBAR56") - 3.6*(1 + Util.ADMIN_OVERHEAD));
		assertTrue(delta < 0.001);
		
		
		//test parse has enogh SKU for each account
		/*  what to do with those shared SKU? 
		for (NFAccountEnum p : NFAccountEnum.values()) {
			HashMap<String, Float>  acctSkuMap = cogs.allCOGS.get(p);
			logger.debug("Account {} has {}  SKU", p, acctSkuMap.size());
			assertTrue(acctSkuMap.size() > 10); 
		}
		*/
	}

}
