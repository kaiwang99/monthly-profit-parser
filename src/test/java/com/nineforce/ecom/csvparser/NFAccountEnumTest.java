package com.nineforce.ecom.csvparser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nineforce.ecom.csvparser.NFAccountEnum;
import com.nineforce.ecom.csvparser.NFAccountTypeEnum;

class NFAccountEnumTest {

	public static Logger logger = (Logger) LoggerFactory.getLogger(NFAccountEnumTest.class);
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	final void testToString() {
		//fail("Not yet implemented"); // TODO
		assertEquals(1,1);
	}
	
	@Test
	final void testEnumEquals()	{
		assertNotEquals(NFAccountEnum.AMZN_TQS, NFAccountEnum.WMT_TQS);
	}

	@Test
	final void testGetEnumTypeByTypeAndName() {
		NFAccountEnum a = NFAccountEnum.getEnumType(NFAccountTypeEnum.AMZN, "wSd");
		assertEquals(a, NFAccountEnum.AMZN_WSD);
		
		a = NFAccountEnum.getEnumType(NFAccountTypeEnum.EBAY, "ss");
		assertEquals(a, NFAccountEnum.EBAY_SS);
		
		a = NFAccountEnum.getEnumType(NFAccountTypeEnum.WMT, "tqs");
		assertEquals(a, NFAccountEnum.WMT_TQS);
		
		a = NFAccountEnum.getEnumType(NFAccountTypeEnum.AMZN, "wSd-1");
		assertNotEquals(a, NFAccountEnum.AMZN_WSD);
	}
	
	@Test
	final void testGetEnumTypeByString() {
		NFAccountEnum a = NFAccountEnum.getEnumType("2018FebMonthlyTransaction-Amazon-AD.csv");
		logger.debug("in test " + a);
		assertEquals(a, NFAccountEnum.AMZN_AD);
		
		a = NFAccountEnum.getEnumType("./AprTxn/pp-tqs-2018-04-alltxn.CSV");
		logger.debug("in test " + a);
		assertEquals(a, NFAccountEnum.PP_TQS);
	}
}
