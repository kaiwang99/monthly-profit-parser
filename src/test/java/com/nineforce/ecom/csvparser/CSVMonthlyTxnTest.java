package com.nineforce.ecom.csvparser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nineforce.ecom.csvparser.CSVMonthlyTxn;
import com.nineforce.ecom.csvparser.NFAccountEnum;

class CSVMonthlyTxnTest {
	static CSVMonthlyTxn mon;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		mon = new CSVMonthlyTxn("FebTxn");
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
	final void testGetSoruceFiles() {
		//fail("Not yet implemented"); // TODO
	}

	@Test
	final void testInitCOGS() {
		// 
		assertEquals(mon.cogs.getCOGS(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011"), 1.5);
		assertEquals(mon.cogs.getCOGS(NFAccountEnum.EBAY_SS, "XB1717-ASCBF337-302HMT"), 10);
		
		double delta = Math.abs(mon.cogs.getCOGS(NFAccountEnum.AMZN_WSD, "CALLOFD-TSION5-8-LIGTBAR56") - 3.6);
		assertTrue(delta < 0.001);
	}

}
