package com.nineforce.ecom.amzn.file;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FbaReturnGeneratorTest {
	static FbaReturnGenerator gen = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 gen = new FbaReturnGenerator("test-file"); 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
/** use 5.0 @Ignore 
	@Test
	public final void testParse() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGenReturnFiles() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testCreateFile() {
		fail("Not yet implemented"); // TODO
	}
	
	*/
	
	@Test
	public final void testGetLoopCount() {
		assertEquals(1, gen.getLoopCount(1));
		assertEquals(1, gen.getLoopCount(gen.ORDER_SIZE));
		assertEquals(2, gen.getLoopCount(gen.ORDER_SIZE + 1));
		assertEquals(10, gen.getLoopCount(330));
		
	}

}
