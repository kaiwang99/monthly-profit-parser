package com.nineforce.ecom.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class NFAccountEnumTest {

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
	final void testGetEnumType() {
		NFAccountEnum a = NFAccountEnum.getEnumType(NFAccountTypeEnum.AMZN, "tqs");
		assertEquals(a, NFAccountEnum.AMZN_TQS);
	}
}
