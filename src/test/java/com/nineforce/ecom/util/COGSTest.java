package com.nineforce.ecom.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class COGSTest {
	
	static final String COGS_file = "./src/main/resources/COGS.csv";
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
		assertEquals(11, cogs.allCOGS.size());
	}

	@Test
	final void testParse() throws IOException {
		//fail("Not yet implemented"); // TODO
		//get a fix number to test.. can be changed. Can be more convoluted: read in file again and then compare
		cogs.parse();
		assertEquals(cogs.getCOGS(NFAccountEnum.AMZN_TQS, "7-100072801-1001-38620011"), 1.5);
		assertEquals(cogs.getCOGS(NFAccountEnum.EBAY_SS, "XB1717-ASCBF337-302HMT"), 10);
		
		double delta = Math.abs(cogs.getCOGS(NFAccountEnum.AMZN_WSD, "CALLOFD-TSION5-8-LIGTBAR56") - 3.6);
		assertTrue(delta < 0.001);
		
	}

}
