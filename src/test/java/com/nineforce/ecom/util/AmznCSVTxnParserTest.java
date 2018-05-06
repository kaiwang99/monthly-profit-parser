package com.nineforce.ecom.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AmznCSVTxnParserTest {
	
	static AmznCSVTxnParser parser = null;
	private static final String SAMPLE_CSV_FILE_PATH = "./src/test/resources/2018FebMonthlyTransaction-AD.csv";
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		parser = new AmznCSVTxnParser(SAMPLE_CSV_FILE_PATH);
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
	@Disabled
	final void testGetParsedRecordCnt() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	final void testParseFile() {
		try {
			int count = parser.parseFile();
			int count2 = parser.getParsedRecordCnt();
			
			assertEquals(count, count2);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Disabled
	final void testIsHeader() {
		fail("Not yet implemented"); // TODO
	}

}
