package com.nineforce.ecom.csvparser;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.nineforce.ecom.csvparser.AmznCSVTxnParser;
import com.nineforce.ecom.csvparser.COGS;
import com.nineforce.ecom.csvparser.NFAccountEnum;

class AmznCSVTxnParserTest {
	
	static AmznCSVTxnParser parser = null;
	private static COGS cogs;
	private static final String SAMPLE_CSV_FILE_PATH = "./src/test/resources/2018FebMonthlyTransaction-AD.csv";
	private static final String COGS_PATH = "./src/test/resources/COGS.csv";
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		parser = new AmznCSVTxnParser(SAMPLE_CSV_FILE_PATH);
		cogs = new COGS(COGS_PATH);
		try {
				cogs.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	// now, initOutputFile is called when setCOGS
	@Test
	void testInitOutputFile() {
		parser.setCOGS(NFAccountEnum.AMZN_AD, cogs);
		// above invoeks parser.initOutputFile();
		
		String xlsx = SAMPLE_CSV_FILE_PATH.replace("csv", "xlsx");
		assertEquals(parser.xlsxOutputFile, xlsx);
		
	}
	
	@Test
	void testIsEnglishLocale() {
		assertTrue(AmznCSVTxnParser.isEnglishLocale(new Locale("en", "CN")));
		assertFalse(AmznCSVTxnParser.isEnglishLocale(new Locale("fr", "EN")));
	}
	
	@Test
	void testIsUSLocale() {
		assertTrue(AmznCSVTxnParser.isEnglishLocale(new Locale("en", "US")));
		assertFalse(AmznCSVTxnParser.isEnglishLocale(new Locale("fr", "EN")));		
	}
	
	@Test
	void testGetLocale() {
		//constructor init. this.curLocale already
		Locale l = parser.curLocale;
		assertEquals(l.getCountry(), "US");
		assertEquals(l.getDisplayLanguage(), "English");
		
		// test frech 
		l = AmznCSVTxnParser.getLocale("mytestfielhasthefrom-amaZoN-hg-fr");
		assertEquals(l.getCountry(), "FR");
		assertEquals(l.getDisplayLanguage(), "French");
		
		// test en, UK
		l = AmznCSVTxnParser.getLocale("mytestfielhasthefrom-amaZoN-tqs-uk");
		assertEquals(l.getCountry(), "UK");
		assertEquals(l.getDisplayLanguage(), "English");
		
		// test default... input not generate decal 
		l = AmznCSVTxnParser.getLocale("mytestfielhasthefrom-esty-tqs");	
		assertEquals(l.getCountry(), "US");
		assertEquals(l.getDisplayLanguage(), "English");		
	}
}
